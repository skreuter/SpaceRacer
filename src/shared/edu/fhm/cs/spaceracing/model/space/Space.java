package edu.fhm.cs.spaceracing.model.space;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.fhm.cs.spaceracing.model.level.Checkpoint;
import edu.fhm.cs.spaceracing.model.ship.Ship;

/**
 * Represents the space where the race takes place. Contains all objects that
 * "fly/float around" in the game. These objects can either be ships, obstacles
 * or checkpoints.
 * 
 * Usually the center of the space coordinate system is in the center of the
 * level i.e the race course.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class Space implements Serializable
{
	Collection<SpaceObject> objects = new ArrayList<SpaceObject>(100);
	
	Collection<Ship> ships = new ArrayList<Ship>(8);
	List<Obstacle> obstacles = new ArrayList<Obstacle>();
	List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	
	public void addShip(Ship ship)
	{
		objects.add(ship);
		ships.add(ship);
	}
	
	public void addObstacle(Obstacle obstacle)
	{
		objects.add(obstacle);
		obstacles.add(obstacle);
	}
	
	public void addCheckpoint(Checkpoint checkpoint)
	{
		checkpoint.setId(checkpoints.size());
		
		objects.add(checkpoint);
		checkpoints.add(checkpoint);
	}
	
	public Collection<SpaceObject> getObjects()
	{
		return objects;
	}
	
	public Collection<Ship> getShips()
	{
		return ships;
	}
	
	public List<Obstacle> getObstacles()
	{
		return obstacles;
	}
	
	public List<Checkpoint> getCheckpoints()
	{
		return checkpoints;
	}
}
