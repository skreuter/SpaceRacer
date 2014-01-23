
package edu.fhm.cs.spaceracing.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.fhm.cs.spaceracing.net.udp.UDPSerializer;
import edu.fhm.cs.spaceracing.net.udp.internal.RecvPacket;
import edu.fhm.cs.spaceracing.net.udp.internal.UDPFeatureManager;


public class UDPSimpleTests
{
	private static SocketAddress address1;
	private static SocketAddress address2;
	
	private static UDPFeatureManager manager1;
	private static UDPFeatureManager manager2;

	@BeforeClass
	public static void init() throws IOException
	{
		address1 = new InetSocketAddress("localhost", 62233);
		address2 = new InetSocketAddress("localhost", 63322);

		manager1 = new UDPFeatureManager(62233);
		manager1.registerNewClient(address2);

		manager2 = new UDPFeatureManager(63322);
		manager2.registerNewClient(address1);
	}
	
	@AfterClass
	public static void destroy() throws IOException
	{
		manager1.close();
		manager2.close();
	}

	/**
	 * Einfacher Test der UDP Verbindung
	 * 
	 */
	@Test
	public void simple_data_test() throws Exception
	{
		UDPSerializer ser;
		RecvPacket recv;

		ser = new UDPSerializer();
		ser.putInt(0xDEADDEAD);
		manager1.sendData(address2, ser);
		ser.destroy();

		recv = manager2.receiveData();
		assertEquals(recv.getClient(), address1);
		assertTrue(recv.getUnserializer().getInt() == 0xDEADDEAD);
		recv.getUnserializer().destroy();

		ser = new UDPSerializer();
		ser.putInt(0xADDEADDE);
		manager2.sendData(address1, ser);
		ser.destroy();

		recv = manager1.receiveData();
		assertEquals(recv.getClient(), address2);
		assertTrue(recv.getUnserializer().getInt() == 0xADDEADDE);
		recv.getUnserializer().destroy();
	}

	/**
	 * Test von fragmentierten UDP Packets. Getestet wird hierbei ob die UDP
	 * Verbindung fragmentierte Packets richtig sendet und auch empfangt.
	 * Desweiteren werden die Serializer ob ihre richtige Funktion hin
	 * überprüft.
	 * 
	 * Valdierung erfolgt nur über die Daten!
	 * 
	 * @throws Exception
	 */
	@Test
	public void fragmented_packet() throws Exception
	{
		final int DATA = 0xDEADDEAD;
		final int DATA_SIZE = 500;

		UDPSerializer ser;
		ser = new UDPSerializer();
		for (int i = 0; i < DATA_SIZE; ++i)
		{
			ser.putInt(DATA);
		}
		manager1.sendData(address2, ser);
		ser.destroy();

		RecvPacket recv;
		recv = manager2.receiveData();
		assertEquals(recv.getClient(), address1);
		for (int i = 0; i < DATA_SIZE; ++i)
		{
			assertTrue(recv.getUnserializer().getInt() == DATA);
		}
		recv.getUnserializer().destroy();
	}

}
