
package edu.fhm.cs.spaceracing.net.udp.internal;

import java.net.SocketAddress;

import edu.fhm.cs.spaceracing.net.udp.UDPUnserializer;

/**
 * 
 * @author bernhard
 * 
 */
public class RecvPacket
{
	RecvPacket(SocketAddress sender, UDPUnserializer unser)
	{
		client = sender;
		unserializer = unser;
	}

	public SocketAddress getClient()
	{
		return client;
	}

	public UDPUnserializer getUnserializer()
	{
		return unserializer;
	}

	private final SocketAddress client;

	private final UDPUnserializer unserializer;

}
