package edu.fhm.cs.spaceracing.model;

import java.io.Serializable;

import edu.fhm.cs.spaceracing.model.ship.Ship;

/**
 * Represents one player in the game and holds data related to a player.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class Player implements Serializable
{
	private final int id; 
	private final String name;
	
	private Ship ship;
	
	private int lastCheckpointIndex = 0;
	
	public Player(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}
	
	public Ship getShip()
	{
		return ship;
	}
	
	public void setShip(Ship ship)
	{
		this.ship = ship;
	}
	
	public int getLastCheckpointIndex()
	{
		return lastCheckpointIndex;
	}
	
	public void setLastCheckpointIndex(int lastCheckpointIndex)
	{
		this.lastCheckpointIndex = lastCheckpointIndex;
	}
}
