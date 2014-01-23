
package edu.fhm.cs.spaceracing.server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.fhm.cs.spaceracing.client.controller.events.ShipstateChange;
import edu.fhm.cs.spaceracing.net.udp.UDPPacketManager;
import edu.fhm.cs.spaceracing.net.udp.exception.UDPFormatException;

public class UDPSerializeTest
{
	static SocketAddress address1;

	static SocketAddress address2;

	static UDPPacketManager manager1;

	static UDPPacketManager manager2;

	@BeforeClass
	public static void init() throws IOException
	{
		address1 = new InetSocketAddress("localhost", 62233);
		address2 = new InetSocketAddress("localhost", 63322);

		manager1 = new UDPPacketManager(62233);
		manager2 = new UDPPacketManager(63322);

		manager1.registerNewClient(address2);

		manager2.registerNewClient(address1);
	}

	@AfterClass
	public static void destroy() throws IOException
	{
		manager1.close();
		manager2.close();
	}

	@Test
	public void test() throws IOException, UDPFormatException
	{
		final ShipstateChange sendObj = new ShipstateChange(1);
		sendObj.setFired(true);
		sendObj.setForwardThrustChange(1234);
		sendObj.setSidewaysThrustChange(5678);
		sendObj.setYRotation(345.678);
		sendObj.setXRotation(123.345);
		sendObj.setRollingThrustChange(1234567);

		manager1.addEventObject(address2, sendObj);
		manager1.flushEventObjectQue();

		ShipstateChange recvObj = (ShipstateChange) manager2
				.getNextEventObject();
		assertTrue(recvObj != null);
		equal(sendObj, recvObj);
	}

	@Test
	public void test2() throws IOException, UDPFormatException
	{
		// Bei Loopcount 2600 Test erfolgreich >= 2700 führt zu Paketverlusten (Linux 64Bit)
		final int loopcount = 100;

		final ShipstateChange sendObj = new ShipstateChange(1);
		sendObj.setFired(true);
		sendObj.setForwardThrustChange(1234);
		sendObj.setSidewaysThrustChange(5678);
		sendObj.setYRotation(345.678);
		sendObj.setXRotation(123.345);
		sendObj.setRollingThrustChange(1234567);

		for (int i = 0; i < loopcount; ++i)
		{
			manager1.addEventObject(address2, sendObj);
		}
		manager1.flushEventObjectQue();

		for (int i = 0; i < loopcount; ++i)
		{
			ShipstateChange recvObj = (ShipstateChange) manager2
					.getNextEventObject();
			assertTrue(recvObj != null);
			equal(sendObj, recvObj);
		}

		Object recvObj2 = manager2.getNextEventObject();
		assertTrue(recvObj2 == null);
	}
	
	@Test
	public void test3() throws Exception
	{
		final int loopcount = 2000;
		
		for (int i = 0; i < loopcount; ++i) {
			test2();
		}
	}

	void equal(ShipstateChange obj1, ShipstateChange obj2)
	{
		assertTrue(obj1.isFired() == obj2.isFired());
		assertTrue(obj1.getForwardThrustChange() == obj2
				.getForwardThrustChange());
		assertTrue(obj1.getSidewaysThrustChange() == obj2
				.getSidewaysThrustChange());
		assertTrue(obj1.getYRotation() == obj2.getYRotation());
		assertTrue(obj1.getXRotation() == obj2.getXRotation());
		assertTrue(obj1.getRollingThrustChange() == obj2
				.getRollingThrustChange());
	}

}
