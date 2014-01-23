package edu.fhm.cs.spaceracing.model.generic;

import java.io.Serializable;

/**
 * 
 * @author Martin Hackenberg
 */
@SuppressWarnings("serial")
public class Sphere implements Serializable
{	
	private Vector relativePosition;
	private Vector absolutePosition;

	
	/**
	 * radius der Kugel
	 */
	private double radius;
	
	private boolean flag;
	
	/**
	 * 
	 * @param relativPosition relative Position der Sphere zu einer absoluten Position
	 */

	public Sphere(Vector relativePosition, double radius)
	{
		this.relativePosition = relativePosition;
		this.radius = radius;
		absolutePosition = new Vector(0,0,0);
	}
	
	public double getRadius()
	{
		return radius;
	}
	
	public void setRadius(double radius)
	{
		this.radius = radius;
	}
	
	public Vector getPosition()
	{
		return absolutePosition.add(relativePosition);
	}
	
	public void setPosition(Vector absolutePosition)
	{
		this.absolutePosition = absolutePosition;
	}
	
	public void flag()
	{
		flag = true;
	}
	
	public boolean isFlag()
	{
		if(flag)
		{
			flag = false;
			return true;
		}
		return false;
	}
}
