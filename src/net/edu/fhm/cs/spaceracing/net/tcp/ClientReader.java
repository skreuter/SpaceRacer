
package edu.fhm.cs.spaceracing.net.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import edu.fhm.cs.spaceracing.net.tcp.events.CloseConnection;

/**
 * Responsible for reading incoming "objects" and adding them to the readerQue
 * 
 * @author bernhard
 *
 */
public class ClientReader extends Thread
{

	public ClientReader(Socket sock, SynFIFO<Object> readerQue)
	{
		clientsocket = sock;
		this.readerQue = readerQue;
	}

	@Override
	public void run()
	{
		try
		{
			ObjectInputStream stream = new ObjectInputStream(clientsocket
					.getInputStream());

			while (true)
			{
				readerQue.add(stream.readObject());
			}
		}
		catch (IOException e)
		{

		}
		catch (ClassNotFoundException e)
		{
		}
		finally
		{
			try
			{
				clientsocket.close();
			}
			catch (IOException e2)
			{
			}
			readerQue.add(new CloseConnection(clientsocket));
		}
	}

	private final Socket clientsocket;

	private final SynFIFO<Object> readerQue;

}
