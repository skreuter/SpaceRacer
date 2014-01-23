package edu.fhm.cs.spaceracing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.fhm.cs.spaceracing.model.level.Checkpoint;
import edu.fhm.cs.spaceracing.model.level.Level;
import edu.fhm.cs.spaceracing.model.space.Obstacle;
import edu.fhm.cs.spaceracing.model.space.Space;

/**
 * Represents a running game and holds all data to keep the entire state of the
 * game.
 * 
 * A Game object is created when the actual game i.e. the race is started.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class Game implements Serializable
{
	private List<Player> players = new ArrayList<Player>(8);
	
	private Level level;
	
	private Space space;
	
	public void fillSpace()
	{
		space = new Space();
		
		for(Player p : players)
		{
			space.addShip(p.getShip());
		}
		
		// load level into space
		
		for(Checkpoint c : level.getCheckpoints())
		{
			space.addCheckpoint(c);
		}
		
		for(Obstacle o : level.getObstacles())
		{
			space.addObstacle(o);
		}
	}
	
	public void assignStartPositions()
	{
		for (int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i); 
			
			level.toStartPoint(i, p.getShip());
		}
	}
	
	/*
	 * getters/setters
	 */
	
	public List<Player> getPlayers()
	{
		return players;
	}
	
	public void addPlayer(Player p)
	{
		players.add(p);
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	public void setLevel(Level level)
	{
		this.level = level;
	}
	
	public Space getSpace()
	{
		return space;
	}
}
