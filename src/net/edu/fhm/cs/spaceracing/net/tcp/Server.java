
package edu.fhm.cs.spaceracing.net.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import edu.fhm.cs.spaceracing.net.tcp.events.NewConnection;
import edu.fhm.cs.spaceracing.net.tcp.events.ServerAcceptError;

/**
 * Listen for incoming connections and dispatch the correct events. <br>
 * 
 * If accept() fails a event will be dispatched and the thread will terminate!
 * The exception will not be propagated, so a registered exception-handler will not be called! 
 * 
 * @author bernhard
 *
 */
public class Server extends Thread
{
	public Server(int port, SynFIFO<Object> readerQue) throws IOException
	{
		serversocket = new ServerSocket(port);
		this.readerQue = readerQue;
	}

	@Override
	public void run()
	{
		while (!shutdown)
		{
			Socket clientsocket;

			try
			{
				clientsocket = serversocket.accept();
			}
			catch (IOException e)
			{
				if (shutdown) {
					return;
				}
				readerQue.add(new ServerAcceptError());
				return;
			}

			try
			{
				ClientReader reader = new ClientReader(clientsocket, readerQue);
				ClientWriter writer = new ClientWriter(clientsocket);

				readerQue.add(new NewConnection(writer));

				reader.start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				try
				{
					clientsocket.close();
				}
				catch (IOException e2)
				{
				}
			}
		}
	}
	
	public void shutdown () throws IOException {
		shutdown = true;
		serversocket.close();
	}

	private boolean shutdown;

	private final ServerSocket serversocket;

	private final SynFIFO<Object> readerQue;
}
