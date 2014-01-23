package edu.fhm.cs.spaceracing.model.generic;

import java.io.Serializable;

/**
 * Immutable vector object. Represent a three dimensional vector.
 * 
 * Provides several methods for vector arithmetic.
 * 
 * @author christian.knuechel@gmx.de
 * @author and others
 */
@SuppressWarnings("serial")
public class Vector implements Serializable
{
	private final double x, y, z;
	
	public Vector(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public double getZ()
	{
		return z;
	}
	
	public double distance(Vector other)
	{
		/*
		 * Distance formula: sqrt((xb-xa)^2+(yb-ya)^2+(zb-za)^2)
		 */
		
		double dx = (other.getX() - getX())*(other.getX() - getX());
		double dy = (other.getY() - getY())*(other.getY() - getY());
		double dz = (other.getZ() - getZ())*(other.getZ() - getZ());
		
		return Math.sqrt(dx + dy + dz);
	}
	
	/**
	 * Returns the vector displaced by the displacement.
	 * 
	 * @param displacement Shortest line between current position and new position.
	 * @return The vector displaced by the displacement.
	 */
	public Vector displaced(int displacement)
	{
		return new Vector(getX() + displacement, 
				getY() + displacement, 
				getZ() + displacement);
	}
	
	public Vector add(Vector other)
	{
		return new Vector(getX() + other.getX(), 
				getY() + other.getY(), 
				getZ() + other.getZ());
	}
	
	public Vector subtract(Vector other)
	{
		return new Vector(getX() - other.getX(), 
				getY() - other.getY(), 
				getZ() - other.getZ());
	}
	
	public Vector multiplyWithScalar(double scalar)
	{
		return new Vector(getX() * scalar, 
				getY() * scalar, 
				getZ() * scalar);
	}
	
	public double getMagnitude()
	{
		return Math.sqrt(getX() * getX() + 
				getY() * getY() + 
				getZ() * getZ());
	}
	
	public Vector normalize()
	{
		double magnitude = getMagnitude();
		
		return new Vector(getX() / magnitude, 
				getY() / magnitude, 
				getZ() / magnitude);
	}
	
	public double getDotProduct(Vector other)
	{
		return getX() * other.getX() + 
			getY() * other.getY() + 
			getZ() * other.getZ();
	}
	
	/**
	 * Returns the angle between the given vectors in degrees.
	 * 
	 * @param other
	 * @return The angle in degrees.
	 */
	public double getAngle(Vector other)
	{
		double dotProduct = getDotProduct(other);
		
		double magnitude = getMagnitude();
		double otherMagnitude = other.getMagnitude();
		
		return Math.acos(dotProduct / (magnitude * otherMagnitude));
	}
	
	public Vector crossProduct(Vector other)
	{
		double x = getY() * other.getZ() - getZ() * other.getY();
		double y = getZ() * other.getX() - getX() * other.getZ();
		double z = getX() * other.getY() - getY() * other.getX();
		
		return new Vector(x,y,z);
	}
	
	public double[] asArray()
	{
		return new double[] {getX(), getY(), getZ()};
	}
	
	public double scalarProduct(Vector other)
	{
		return getX() * other.getX() + 
			getY() * other.getY() + 
			getZ() * other.getZ();
	}
	
	public Vector rotate(double rotation, Vector rotationAxis)
	{
		return new RotationMatrix(rotation, rotationAxis).multiplicate(this);
	}
	
	public double getLength()
	{
		// Wurzel aus Skalarprodukt mit sich selbst.
		return Math.sqrt(scalarProduct(this));
	}
	
	public Vector setLength(double length)
	{
		return normalize().multiplyWithScalar(length);
	}
	
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		
		if(o == null || o.getClass() != this.getClass())				// Besser als instanceof wegen Ableitungen
			return false;
			
		Vector v = (Vector) o;
		return v.x == x && v.y == y && v.z == z;
	}
	
	public int hashCode()
	{
		return (int)(x * 100 + y * 10 + z);
	}
	
	public String toString()
	{
		return getX() + "," + getY() + "," + getZ();
	}
}
