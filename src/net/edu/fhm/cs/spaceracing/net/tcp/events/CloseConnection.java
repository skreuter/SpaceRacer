
package edu.fhm.cs.spaceracing.net.tcp.events;

import java.net.Socket;

/**
 * Emitted if client connection was closed e.g an error occured.
 *  
 * @author bernhard
 *
 */
public class CloseConnection
{
	public CloseConnection(Socket sock)
	{
		socket = sock;
	}

	public Socket getSocket()
	{
		return socket;
	}

	private final Socket socket;

}
