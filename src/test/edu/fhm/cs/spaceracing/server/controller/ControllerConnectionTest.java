package edu.fhm.cs.spaceracing.server.controller;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.fhm.cs.spaceracing.client.controller.events.ShipstateChange;
import edu.fhm.cs.spaceracing.controller.ControllerConnection;
import edu.fhm.cs.spaceracing.controller.IShipstateChange;
import edu.fhm.cs.spaceracing.net.udp.UDPPacketManager;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;

public class ControllerConnectionTest
{
	private static ControllerConnection<IShipstateChange, ShipstateChange> shipstateChangeReceiver;
	private static SocketAddress shipstateChangeReceiverAddress;
	private static SocketAddress managerAddress;
	private static UDPPacketManager manager;
	
	@BeforeClass
	public static void init() throws UnableToCreateSocketException, IOException
	{
		shipstateChangeReceiver = new ControllerConnection<IShipstateChange, ShipstateChange>(7777);
		shipstateChangeReceiverAddress = new InetSocketAddress("localhost", 7777);
		manager = new UDPPacketManager(8888);
		managerAddress = new InetSocketAddress("localhost", 8888);
		shipstateChangeReceiver.addClient(managerAddress);
		manager.registerNewClient(shipstateChangeReceiverAddress);
	}
	
	@AfterClass
	public static void finish() throws IOException
	{
		manager.close();
	}
	
	@Test
	public void receivePacketTest() throws Exception
	{
		final ShipstateChange sendObj = new ShipstateChange(1);
		sendObj.setFired(true);
		sendObj.setForwardThrustChange(1234);
		sendObj.setSidewaysThrustChange(5678);
		sendObj.setYRotation(345.678);
		sendObj.setXRotation(123.345);
		sendObj.setRollingThrustChange(1234567);
		sendObj.setPlayerId(1);

		manager.addEventObject(shipstateChangeReceiverAddress, sendObj);
		manager.flushEventObjectQue();

		IShipstateChange recvObj = null;
		while(recvObj == null)
		{
			recvObj = (ShipstateChange) shipstateChangeReceiver.take();
			shipstateChangeReceiver.run();
		}
		equal(sendObj, recvObj);
	}
	
	@Test
	public void massReceiveTest() throws Exception
	{
		int runs = 10000;
		for(int i = 0; i < runs; i++)
		{
			receivePacketTest();
		}
	}
	
	@Test
	public void sendPacketTest() throws Exception
	{
		final ShipstateChange sendObj = new ShipstateChange(1);
		sendObj.setFired(true);
		sendObj.setForwardThrustChange(1234);
		sendObj.setSidewaysThrustChange(5678);
		sendObj.setYRotation(345.678);
		sendObj.setXRotation(123.345);
		sendObj.setRollingThrustChange(1234567);
		sendObj.setPlayerId(1);
		
		shipstateChangeReceiver.put(sendObj);
		
		shipstateChangeReceiver.run();
		
		IShipstateChange recvObj = null;
		while(recvObj == null)
		{
			recvObj = (ShipstateChange) manager.getNextEventObject();
			shipstateChangeReceiver.run();
		}
		equal(sendObj, recvObj);
	}
	
	@Test
	public void massSendPacketTest() throws Exception
	{
		int runs = 10000;
		for(int i = 0; i < runs; i++)
		{
			sendPacketTest();
		}
	}
	
	void equal(IShipstateChange obj1, IShipstateChange obj2)
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
		assertTrue(obj1.getPlayerId() == obj2.getPlayerId());
	}
}
