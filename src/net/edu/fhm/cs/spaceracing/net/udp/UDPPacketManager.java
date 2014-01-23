
package edu.fhm.cs.spaceracing.net.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import edu.fhm.cs.spaceracing.net.udp.exception.UDPFormatException;
import edu.fhm.cs.spaceracing.net.udp.exception.UnknownUDPEventObjectException;
import edu.fhm.cs.spaceracing.net.udp.internal.RecvPacket;
import edu.fhm.cs.spaceracing.net.udp.internal.UDPFeatureManager;

/**
 * Manages the objects which should be transmitted to each client.
 * It implements a que for each client to allow transmission of serveral event objects in one or more packets
 * 
 * @author bernhard
 *
 */
public class UDPPacketManager
{
	/*
	 * Allows debugging of unserializer data if necessary. so you can easily identify where a object starts and 
	 * where it ends 
	 */
	private final static boolean USE_MAGIC_OBJECT_SEPARATOR = false;
	private final static int MAGIC_OBJECT_SEPARATOR = 0xDEADBEEF;


	public UDPPacketManager(int port) throws IOException
	{
		featmanager = new UDPFeatureManager(port);
		sendEventQue = new HashMap<SocketAddress, UDPSerializer>();

		lastRecvPacket = null;
	}

	public void registerNewClient(SocketAddress sa)
	{
		featmanager.registerNewClient(sa);
	}

	public void unregisterClient(SocketAddress sa)
	{
		if (lastRecvPacket != null && lastRecvPacket.getClient().equals(sa))
		{
			lastRecvPacket = null;
		}
		UDPSerializer serializer = sendEventQue.remove(sa);
		if (serializer != null)
		{
			serializer.destroy();
		}

		featmanager.unregisterClient(sa);
	}

	/**
	 * Add a UDPEventObject to the outgoing EventObjectQue. This method will not
	 * send any packets!
	 * 
	 * @param receiver
	 * @param obj
	 */
	public void addEventObject(SocketAddress receiver, UDPSerializeable obj)
	{
		UDPSerializer serializer = sendEventQue.get(receiver);
		if (serializer == null)
		{
			serializer = new UDPSerializer();
			sendEventQue.put(receiver, serializer);
		}
		
		if (USE_MAGIC_OBJECT_SEPARATOR) {
			serializer.putInt(MAGIC_OBJECT_SEPARATOR);
		}
		UDPObjectConverter.serialize(obj, serializer);
		if (USE_MAGIC_OBJECT_SEPARATOR) {
			serializer.putInt(MAGIC_OBJECT_SEPARATOR);
		}
	}

	/**
	 * Flush EventObjectQue and send packets to the specified clients.
	 * If a error occurs the current objects for this connections may be lost.
	 * The caller must ensure if those objects where transmitted or not!
	 */
	public void flushEventObjectQue() throws IOException
	{
		for (Map.Entry<SocketAddress, UDPSerializer> entry : sendEventQue
				.entrySet())
		{
			UDPSerializer serializer = entry.getValue();
			if (!serializer.isValid()) {
				continue;
			}
			
			try {
				featmanager.sendData(entry.getKey(), serializer);
			}
			catch (IOException e) {
				throw e;
			}
			finally {
				serializer.destroy();
			}
		}
		sendEventQue.clear();
	}

	/**
	 * Receives a udp packet and converts it to one or more EventObjects
	 * 
	 * @return
	 * @throws IOException
	 * @throws UDPFormatException
	 */
	public Object getNextEventObject() throws IOException, UDPFormatException
	{
		if (lastRecvPacket == null)
		{
			lastRecvPacket = featmanager.receiveData();
			if (lastRecvPacket == null)
				return null;
		}
		
		UDPUnserializer unserializer = lastRecvPacket.getUnserializer();
			
		if (USE_MAGIC_OBJECT_SEPARATOR && unserializer.getInt() != MAGIC_OBJECT_SEPARATOR) 
		{
			unserializer.printCurrentByteBuffer();
			throw new UnknownUDPEventObjectException("Magic Object ID Check Failed! (Check1: separator start)");
		}
		Object obj = UDPObjectConverter.unserialize(unserializer);
		if (USE_MAGIC_OBJECT_SEPARATOR && unserializer.getInt() != MAGIC_OBJECT_SEPARATOR) 
		{
			unserializer.printCurrentByteBuffer();
			throw new UnknownUDPEventObjectException("Magic Object ID Check Failed! (Check2: separator end)");
		}
		
		if (!unserializer.hasRemaining())
		{
			unserializer.destroy();
			lastRecvPacket = null;
		}
		
		return obj;
	}
	
	public void close() throws IOException 
	{
		featmanager.close();
	}

	private final UDPFeatureManager featmanager;

	private final HashMap<SocketAddress, UDPSerializer> sendEventQue;

	private RecvPacket lastRecvPacket;

}
