package edu.fhm.cs.spaceracing.server.controller;

import java.io.Serializable;

public class ClientId implements Serializable
{
	final int clientId;
	
	ClientId(int clientId)
	{
		this.clientId = clientId;
	}
	
	public int getClientId()
	{
		return clientId;
	}
	
	private static final long serialVersionUID = -3838198437903270883L;
}
