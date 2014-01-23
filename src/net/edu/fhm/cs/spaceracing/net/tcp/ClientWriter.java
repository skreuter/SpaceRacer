
package edu.fhm.cs.spaceracing.net.tcp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Added to the readerQue (as NewConnection-Event) if a new client connection is established. <br>
 * It gives the controller the possibility to write objects or close the connection.
 *  
 * @author bernhard
 *
 */
public class ClientWriter
{
	public ClientWriter(Socket sock) throws IOException
	{
		clientsocket = sock;
		outputstream = new ObjectOutputStream(clientsocket.getOutputStream());
	}

	public void send(Object obj) throws IOException
	{
		outputstream.writeObject(obj);
	}

	public void closeConnection() throws IOException
	{
		clientsocket.close();
	}

	public Socket getClientSocket()
	{
		return clientsocket;
	}

	private final Socket clientsocket;

	private final ObjectOutputStream outputstream;

}
