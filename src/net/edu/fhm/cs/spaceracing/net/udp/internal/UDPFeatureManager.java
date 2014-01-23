
package edu.fhm.cs.spaceracing.net.udp.internal;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

import edu.fhm.cs.spaceracing.net.udp.UDPSerializer;
import edu.fhm.cs.spaceracing.net.udp.UDPUnserializer;
import edu.fhm.cs.spaceracing.net.udp.exception.UDPFormatException;

/**
 * Responsible for: <br>
 * - Writing the UDPHeader <br> 
 * - Dropping old Packets (Sequencenumber too small, Outside of receive window) <br> 
 * - Dropping of invalid Packets (e.g. illegal client id)<br> 
 * - Assembling fragmentated UDP Packets <br>
 * 
 * Send & Receive will not block!
 * 
 * @author bernhard
 * 
 */
public class UDPFeatureManager
{

	/*
	 * Header: 
	 * - MagicID (4 Bytes) 
	 * - Sequence Number (4 Bytes) 
	 * - Nr of Packet if Fragmented (1 Byte) 
	 * - Nr of Total Packets (1 Byte) Total: 10
	 * 
	 * Caution: 
	 * - For fragmentation only 7 Bits are avialable 
	 * - Nr of Total Packet = 1 => Single Packet 
	 * - Nr of Total Packet = 2 => Nr of Packet 0..1 => 2 Single Packets
	 */
	public final static int HEADER_SIZE = 10;

	private final static int MAGIC_ID = 0x53705261;

	private class CachedPacket
	{
		int sequence_number;

		UDPUnserializer unserializer;

		void init(int seq, UDPUnserializer ser)
		{
			sequence_number = seq;
			unserializer = ser;
		}
	}

	/**
	 * Creates a UDP Listener at the specified port
	 * 
	 * @param port
	 * @throws IOException
	 */
	public UDPFeatureManager(int port) throws IOException
	{
		clientMap = new HashMap<SocketAddress, UDPClient>();
		cachedPackets = new HashMap<SocketAddress, CachedPacket>();
		udpSocket = new UDP(port);

		bufferpool = UDPBufferPool.getInstance();
	}

	/**
	 * Register a new client so incoming packets are received and not dropped
	 * 
	 * @param sd
	 *            SocketAddress needed for sending packets
	 * @throws IllegalArgumentException
	 *             Thrown if a client with the same id already exists
	 */
	public void registerNewClient(SocketAddress sd)
			throws IllegalArgumentException
	{
		if (clientMap.containsKey(sd))
		{
			throw new IllegalArgumentException(
					"Client with specified SocketAddress already exists!");
		}
		clientMap.put(sd, new UDPClient());
	}

	/**
	 * Removes a client with the specified id if it exists. It will also clean
	 * up any additional resources which have been allocated for the specific
	 * client
	 * 
	 * @param id
	 */
	public void unregisterClient(SocketAddress sa)
	{
		clientMap.remove(sa);
		CachedPacket cp = cachedPackets.remove(sa);
		if (cp != null)
		{
			cp.unserializer.destroy();
		}
	}

	/**
	 * Send data to the client. The client must have been registered before with
	 * register_new_client. The caller must destroy the databuffer if the method
	 * returns!
	 * 
	 * @param client_id
	 *            Receiver of the data
	 * @param databuffer
	 *            Data to send (Can be multiple packets)
	 * @throws IllegalArgumentException
	 *             client doesn't exist
	 * @throws IOException
	 */
	public void sendData(SocketAddress receiver, UDPSerializer databuffer)
			throws IllegalArgumentException, IOException
	{
		UDPClient client = clientMap.get(receiver);
		if (client == null)
		{
			throw new IllegalArgumentException(
					"Client with the specified SocketAddress doesn't exist!");
		}

		UDPSerializer.Iterator iter = databuffer.getBufferIterator();
		if (!iter.hasNext())
		{
			return; // Nothing to do!
		}

		int maxPackets = iter.maxNumberOfBuffers();
		if (maxPackets >= 127) {
			throw new IllegalStateException ("Fragmenente (>= 127) dieser größe werden nicht unterstützt!!");
		}
		// Fragmentierte Packete haben alle dieselbe MessageID
		int msgID = client.nextSendMessageID();
		for (byte i = 0; iter.hasNext(); ++i)
		{
			ByteBuffer buffer = iter.next();
			buffer.flip();

			// Write Header
			buffer.mark();
			buffer.putInt(MAGIC_ID);
			buffer.putInt(msgID);
			buffer.put(i);
			buffer.put((byte) maxPackets);
			buffer.reset();

			udpSocket.send(buffer, receiver);
		}
	}

	public RecvPacket receiveData() throws IOException, UDPFormatException
	{
		/*
		 * Da die Sequenznummer irgendwann überläuft führen wir ein Sequenz-Fenster ein, im welchen sich
		 * gülte UDP Pakete befinden müssen.
		 */
		final int MESSAGE_ID_WINDOW = Integer.MAX_VALUE;

		// Daten in ByteBuffer empfangen
		ByteBuffer databuffer = bufferpool.get();
		SocketAddress sender = null;
		try
		{
			sender = udpSocket.receive(databuffer);
			if (sender == null)
			{
				// Nothing received
				bufferpool.put(databuffer);
				return null;
			}
		}
		catch (IOException e)
		{
			bufferpool.put(databuffer);
			throw e;
		}
		databuffer.flip();

		// Gültiges Packet? Erfüllt Header Check?
		if (databuffer.limit() < HEADER_SIZE)
		{
			bufferpool.put(databuffer);
			return null;
		}

		// Packet muss nun auf eine Client "gemappt" werden (wg fragmentierung und zusätzlichen checks)
		UDPClient client = clientMap.get(sender);
		if (client == null)
		{ // Kein Client mit der Sender-Adresse vorhanden => Drop-Packet
			bufferpool.put(databuffer);
			return null;
		}

		if (databuffer.getInt() != MAGIC_ID)
		{
			throw new UDPFormatException("Received packet with wrong MAGIC_ID!");
		}

		// Check des Sequenz-Fensters
		int msgid = databuffer.getInt();
		int lastRecvMessageID = client.getLastRecvMessageID();
		if (msgid <= lastRecvMessageID)
		{ // Es könnte sein, dass die ID übergelaufen ist und wir somit ne
			// angeblich alte bekommen
			if (msgid >= (lastRecvMessageID + MESSAGE_ID_WINDOW))
			{
				// Packet ist zu alt
				bufferpool.put(databuffer);
				return null;
			}
		}

		// Ist das Packet Teil einer Multimsg?
		byte msgNumber = databuffer.get();
		byte msgTotal = databuffer.get();
		if (msgNumber < 0 || msgTotal < 0 || msgNumber >= msgTotal)
		{
			throw new UDPFormatException("Malformed UDP Packet!");
		}

		if (msgTotal != 1)
		{ // Teil einer MultiMsg
			CachedPacket cp = cachedPackets.get(sender);

			// Zustand des CachedPacket Prüfen und ggf reinitializsieren
			if (cp == null)
			{ // Wir haben noch kein CachedPacket Container für den Client
				cp = new CachedPacket();
				cachedPackets.put(sender, cp);

				cp.init(msgid, new UDPUnserializer(msgTotal));
			}
			else if (cp.sequence_number != msgid)
			{
				cp.unserializer.destroy();

				cp.init(msgid, new UDPUnserializer(msgTotal));
			}

			cp.unserializer.setBufferAt(msgNumber, databuffer);

			if (cp.unserializer.checkAllSet())
			{
				cachedPackets.remove(sender);
				client.setLastRecvMessageID(msgid);

				return new RecvPacket(sender, cp.unserializer);
			}
			else
			{
				return receiveData();
			}
		}
		else
		{
			client.setLastRecvMessageID(msgid);
			return new RecvPacket(sender, new UDPUnserializer(databuffer));
		}
	}

	public void close() throws IOException
	{
		udpSocket.close();
	}

	private final HashMap<SocketAddress, UDPClient> clientMap;

	private final HashMap<SocketAddress, CachedPacket> cachedPackets;

	private final UDP udpSocket;

	private final UDPBufferPool bufferpool;
}
