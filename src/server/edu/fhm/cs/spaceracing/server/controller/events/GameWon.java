package edu.fhm.cs.spaceracing.server.controller.events;

import java.io.Serializable;

/**
 * Huelle zur Uebertragung des Siegers an die Clients.
 * 
 * @author Sebastian Gift
 */
@SuppressWarnings("serial")
public class GameWon implements Serializable
{
	private final int winner;
	
	public GameWon(int winner)
	{
		if(winner < 1)
			throw new IllegalArgumentException("Sieger muss >= 1 sein.");
		this.winner = winner;
	}
	
	public int getWinner()
	{
		return winner;
	}
}
