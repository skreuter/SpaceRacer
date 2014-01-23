package edu.fhm.cs.spaceracing.model.level;

import java.util.ArrayList;
import java.util.List;

import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.model.space.Obstacle;

/**
 * Abstract base class for Level objects. Provides functionality which is common
 * to all level implementations.
 * 
 * @author christian.knuechel@gmx.de
 */
public abstract class AbstractLevel implements Level
{
	protected List<Obstacle> obstacles = new ArrayList<Obstacle>();
	protected List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	
	protected Checkpoint startFinish = null;
	
	public List<Obstacle> getObstacles()
	{
		return obstacles;
	}
	
	public List<Checkpoint> getCheckpoints()
	{
		return checkpoints;
	}
	
	public void toStartPoint(int index, Ship s)
	{
		// TODO vary position depending on index
		double distance = -5;
//		
//		Vector v = startFinish.getUp().crossProduct(startFinish.getDirection()).normalize();
//		v = v.multiplyWithScalar(distance);
//		
//		Vector v2 = startFinish.getDirection().normalize();
//		v2 = v2.multiplyWithScalar(-distance);
//		
//		Vector position = startFinish.getPosition().add(v);
//		position = position.add(v2);
		
		/* Kein richtiges system, nur um die Schiffe etwas zu versetzten... */
		Vector position;
		if( index % 2 == 0)
		{
			position = startFinish.getDirection();
			position = position.multiplyWithScalar(index * distance);
		}
		else
		{
			Vector leftVector = s.getDirection().crossProduct(s.getUp());
			position = startFinish.getDirection().normalize();
			position = position.multiplyWithScalar(index * distance);
			position.add(leftVector);
		}
			
		
		s.setPosition(startFinish.getPosition().add(position));
		s.setPositionOfSpheres(startFinish.getPosition().add(position));
		s.setUp(startFinish.getUp());
		s.setDirection(startFinish.getDirection());
	}
}
