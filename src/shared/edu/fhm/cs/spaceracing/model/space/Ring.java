package edu.fhm.cs.spaceracing.model.space;

import java.util.ArrayList;
import java.util.Collection;

import edu.fhm.cs.spaceracing.model.generic.Sphere;
import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * An obstacle thats shaped like a ring. A ring is commonly used to mark a
 * checkpoint.
 * 
 * @see edu.fhm.cs.spaceracing.model.level.Checkpoint
 * @author christian.knuechel@gmx.de
 * @author Stefan Kreuter
 * @author and others
 */

@SuppressWarnings("serial")
public class Ring extends Obstacle
{
	private static final double SPHERES_COUNT = 32;

	public Ring(Vector position)
	{
		super(position);
		
		width = 10;
		height = 10;
		length = 1;
		weight = Integer.MAX_VALUE;

	}

	/**
	 * Setzt die Kollisionskugeln um den Ring
	 */
	public void setRingSpheres()
	{
		Collection<Sphere> spheres = new ArrayList<Sphere>();
		
		// Die Kollisionskugeln werden in richtung des Up Vektors gesetzt
		Vector radiusVector = getUp().setLength(2.4);
		
		//Up Vektor Rotieren
		for(double i = 0.0; i < 2*Math.PI; i += 2*Math.PI/SPHERES_COUNT)
		{
			radiusVector = radiusVector.rotate(i, getDirection());
			spheres.add(new Sphere(radiusVector, 0.35));
			// s.add(new Sphere(new Vector(-up.getX(), up.getY(), up.getZ()), 0.01));
		}
		
		setSphere(spheres);
		setPositionOfSpheres(getPosition());
	}
}