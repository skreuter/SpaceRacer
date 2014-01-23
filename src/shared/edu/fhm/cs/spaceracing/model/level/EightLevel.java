package edu.fhm.cs.spaceracing.model.level;

import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.space.Obstacle;
import edu.fhm.cs.spaceracing.model.space.Ring;

/**
 * Eight level looks like an eight if viewed from the top. From the side it
 * looks like a sigmoid function.
 * 
 * @see #generate()
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class EightLevel extends AbstractLevel 
{
	public static final String ID = "level.eight";
	
	public static final int CHECKPOINT_COUNT = 36;
	public static final int RADIUS = 150;
	public static final int HEIGHT = 20;
	
	public EightLevel()
	{
		generate();
	}
	
	// References:
	// http://www.mathematische-basteleien.de/acht.htm
	// http://de.wikipedia.org/wiki/Sigmoidfunktion
	// http://de.wikipedia.org/wiki/Lissajous-Figur
	private void generate()
	{
		double angleIncrement = 2*Math.PI / CHECKPOINT_COUNT;
		
		for(int i = 0; i < CHECKPOINT_COUNT; i++)
		{
//			double x = radius*Math.sqrt(Math.cos(2*i*angleIncrement))*Math.cos(i*angleIncrement);
//			double y = radius*Math.sqrt(Math.cos(2*i*angleIncrement))*Math.sin(i*angleIncrement);
			double x = RADIUS*Math.cos(i*angleIncrement);
			double y = RADIUS*Math.sin(2*i*angleIncrement);
			
			double z = sigmoid(x, RADIUS, HEIGHT);
			
			// Ableitung bestimmen, Ableitungsvektor ist Tangente zur Kurve
			double dx_dt = -RADIUS*Math.sin(i*angleIncrement);
			double dy_dt = 2*RADIUS*Math.cos(2*i*angleIncrement);
//			double dz_dx = sigmoid_dx(x, radius, height);
			double dz_dt = sigmoid_dx(x, RADIUS, HEIGHT)*dx_dt; // Ableitung mit Kettenregel
			
			if(Math.abs(x) < 0.001 && Math.abs(y) < 0.001)
			{
				// skip rings in the center
				continue;
			}
			
			Vector position = new Vector(x, y, z);
			
			Checkpoint c = new Checkpoint(position);
			Obstacle o = new Ring(position);
			
			// Oben ist abhängig von der Ableitung von z nach x (dz/dx)
			c.setUp(new Vector(0, 0, 1));
			o.setUp(new Vector(0, 0, 1));
			
			// Richtungsvektor zeigt jeweils in Richtung der Kurventangente
			c.setDirection(new Vector(dx_dt, dy_dt, dz_dt).normalize());
			o.setDirection(new Vector(dx_dt, dy_dt, dz_dt).normalize());
			
			//Kollisionkugeln des Rings setzen
			((Ring)o).setRingSpheres();
			
			checkpoints.add(c);
			obstacles.add(o);
			
			if(i == 0)
			{
				startFinish = c;
			}
		}
		
		assert startFinish != null;
		
		checkpoints.add(startFinish);
	}
	
	private static double sigmoid(double x, double radius, double height)
	{
		double c = 1/radius*8;
		double xx = c*x; // Skalierung des x-Wert der Sigmoid Funktion
		
		double yy = 1/(1 + Math.pow(Math.E, xx));
		
		return height*(2*yy - 1); // Skalierung des y-Wert der Sigmoid Funktion
	}
	
	// derivative
	private static double sigmoid_dx(double x, double radius, double height)
	{
		double c = 1/radius*8;
		double xx = c*x;
		
		return -2*height/Math.pow(1 + Math.pow(Math.E, xx), 2) * Math.pow(Math.E, xx) * c;
	}
}
