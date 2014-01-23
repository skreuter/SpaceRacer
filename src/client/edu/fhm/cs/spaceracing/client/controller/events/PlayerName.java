package edu.fhm.cs.spaceracing.client.controller.events;

import java.io.Serializable;

public class PlayerName implements Serializable
{
	private static final long serialVersionUID = -928522007248454935L;
	private final String playername;
	private final int playerid;
	
	public PlayerName(String playername, int playerid)
	{
		this.playername = playername;
		this.playerid = playerid;
	}

	public int getPlayerid()
	{
		return playerid;
	}

	public String getPlayername()
	{
		return playername;
	}
	
}
