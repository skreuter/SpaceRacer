package edu.fhm.cs.spaceracing.model.space;

import java.io.Serializable;
import java.util.Collection;

import edu.fhm.cs.spaceracing.model.generic.Sphere;
import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Represents an object in space with its position, direction and rotation.
 * 
 * Position, direction and rotation are stored in three vectors. The position is
 * the absolute position in space coordinates. The directions is the direction
 * of the object e. g. the ships nose. The rotation is specifed by the up
 * vector, that's where the "roof" of the object points to.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class SpaceObject implements Serializable
{
	/**
	 * Position of the object. Coordinates are in m.
	 */
	Vector position;
	
	/**
	 *  Drehung des Objekts: Richtungs- und Normalenvektor.
	 */
	Vector direction, up;
	
	/**
	 * Drehung des Schiffs um die direction-Achse in radian i.e. not degrees.
	 */
	
	/**
	 * speed vector, length = speed in m/s.
	 */
	private Vector speed = new Vector(0,0,0);
	
	/**
	 * Kollisionskugeln des SpaceObjects
	 */
	Collection<Sphere> spheres;
	
	/*
	 * All in m.
	 */
	protected int width;
	protected int height;
	protected int length;
	
	protected int weight;
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public int getWeight()
	{
		return weight;
	}

	public SpaceObject(Vector position)
	{
		if(position == null)
		{
			this.position = new Vector(0, 0, 0);
		}
		else
		{
			this.position = position;
		}
		direction = new Vector(0, 0, 1);
		up = new Vector(0, 1, 0);
	}
	
	public Vector getPosition()
	{
		return position;
	}
	
	public void setPosition(Vector position)
	{
		this.position = position;
	}
	
	public Vector getDirection()
	{
		return direction;
	}
	
	public void setDirection(Vector direction)
	{
		this.direction = direction;
	}

	public Vector getUp()
	{
		return up;
	}

	public void setUp(Vector up)
	{
		this.up = up;
	}
	
	public Vector getSpeed()
	{
		return speed;
	}
	
	public void setSpeed(Vector speed)
	{
		this.speed = speed;
	}
	
	/**
	 * 
	 * @return Kollisionskugeln
	 */
	public Collection<Sphere> getSpheres()
	{
		return spheres;
	}

	public void setSphere(Collection<Sphere> spheres)
	{
		this.spheres = spheres;
	}
	
	public void setPositionOfSpheres(Vector v)
	{
		for(Sphere s:spheres)
		{
			s.setPosition(v);
		}
	}
}