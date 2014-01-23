package edu.fhm.cs.spaceracing.model.space;

import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Represents an obstacle in space in which a ship can crash into.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class Obstacle extends SpaceObject
{
	public Obstacle(Vector position)
	{
		super(position);
	}
}
