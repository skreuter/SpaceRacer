
package edu.fhm.cs.spaceracing.net.udp.internal;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDP
{
	UDP(int port) throws IOException
	{
		dgChannel = DatagramChannel.open();
		dgSocket = dgChannel.socket();
		dgSocket.bind(new InetSocketAddress(port));

		dgChannel.configureBlocking(false);
	}

	public SocketAddress receive(ByteBuffer databuffer) throws IOException
	{
		databuffer.clear();
		return dgChannel.receive(databuffer); // Achtung: Zuviele Daten werden
		// einfach discarded
	}

	public void send(ByteBuffer databuffer, SocketAddress sa)
			throws IOException
	{
		do
		{
			dgChannel.send(databuffer, sa);
		}
		while (databuffer.hasRemaining());
	}

	public void close() throws IOException
	{
		dgChannel.close();
	}

	private DatagramSocket dgSocket;

	private DatagramChannel dgChannel;

}
