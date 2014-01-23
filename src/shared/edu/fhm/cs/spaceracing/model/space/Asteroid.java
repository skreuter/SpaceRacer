package edu.fhm.cs.spaceracing.model.space;

import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Currently unused.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class Asteroid extends Obstacle
{
	public Asteroid(Vector position)
	{
		super(position);
		
		width = 10;
		height = 10;
		length = 10;
	}
}
