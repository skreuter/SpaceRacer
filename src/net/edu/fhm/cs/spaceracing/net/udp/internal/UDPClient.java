
package edu.fhm.cs.spaceracing.net.udp.internal;

/**
 * Contains client data necessary for udp transmission
 * 
 * @author bernhard
 * 
 */
public class UDPClient
{

	UDPClient()
	{
		nextSendMessageID = Integer.MIN_VALUE;
		lastRecvMessageID = Integer.MIN_VALUE;
	}

	public int nextSendMessageID()
	{
		return ++nextSendMessageID;
	}

	public int getLastRecvMessageID()
	{
		return lastRecvMessageID;
	}

	public void setLastRecvMessageID(int newid)
	{
		lastRecvMessageID = newid;
	}

	private int nextSendMessageID;

	private int lastRecvMessageID;

}
