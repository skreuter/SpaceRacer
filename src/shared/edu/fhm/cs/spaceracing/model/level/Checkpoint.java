package edu.fhm.cs.spaceracing.model.level;

import java.util.Collection;
import java.util.HashSet;

import edu.fhm.cs.spaceracing.model.generic.Sphere;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.space.SpaceObject;

/**
 * Represents a checkpoint in the level i.e. the race course. A checkpoint is a
 * waypoint that needs to be flown by in a predefined order.
 * 
 * Usually a checkpoint is combined with an obstacle like a ring, to mark the
 * checkpoint. A checkpoint itself is "bodyless".
 * 
 * @see edu.fhm.cs.spaceracing.model.space.Ring
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class Checkpoint extends SpaceObject
{
	private int id;
	
	private double radius = 2.4;
	
	public Checkpoint(Vector position)
	{
		super(position);
		
		Collection<Sphere> s = new HashSet<Sphere>();
		s.add(new Sphere(new Vector(0,0,0) , radius));
		setSphere(s);
		setPositionOfSpheres(position);
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public double getRadius()
	{
		return radius;
	}
	
	public void setRadius(double radius)
	{
		this.radius = radius;
	}
}
