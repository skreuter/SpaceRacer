package edu.fhm.cs.spaceracing.model.level;

import java.io.Serializable;
import java.util.List;

import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.model.space.Obstacle;

/**
 * Level objects ares used for loading/generating levels and represent the race
 * course.
 * 
 * @author christian.knuechel@gmx.de
 */
public interface Level extends Serializable
{
	public abstract List<Obstacle> getObstacles();
	
	/**
	 * First checkpoint is start and last is finish by convention. Start and finish can be identical.
	 * 
	 * @return
	 */
	public abstract List<Checkpoint> getCheckpoints();
	
	public abstract void toStartPoint(int index, Ship s);
}
