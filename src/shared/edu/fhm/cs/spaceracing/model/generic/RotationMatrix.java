package edu.fhm.cs.spaceracing.model.generic;

/**
 * @author Stefan Kreuter
 *
 */
public class RotationMatrix extends Matrix
{
	public RotationMatrix(double rotation, Vector rotationAxis)
	{
		super();
		
		double sin = Math.sin(rotation);
		double cos = Math.cos(rotation);
		
		double v1 = rotationAxis.getX();
		double v2 = rotationAxis.getY();
		double v3 = rotationAxis.getZ();
		
		setRowVector(0, new Vector(cos + v1*v1*(1-cos),	
						v1*v2*(1-cos) - v3*sin, 
						v1*v3*(1-cos) + v2*sin));
						
		setRowVector(1, new Vector(v2*v1*(1-cos) + v3*sin, 
						cos + v2*v2*(1-cos), 	
						v2*v3*(1-cos) - v1*sin));
						
		setRowVector(2, new Vector(v3*v1*(1-cos) - v2*sin, 
						v3*v2*(1-cos) + v1*sin, 
						cos + v3*v3*(1-cos)));
	}
}
