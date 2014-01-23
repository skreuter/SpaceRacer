
package edu.fhm.cs.spaceracing.net.tcp.events;

import edu.fhm.cs.spaceracing.net.tcp.ClientWriter;

/**
 * Emitted if a new client connection is established
 * 
 * @author bernhard
 *
 */
public class NewConnection
{

	public NewConnection(ClientWriter writer)
	{
		clientwriter = writer;
	}

	public ClientWriter getClientWriter()
	{
		return clientwriter;
	}

	private ClientWriter clientwriter;
	
}