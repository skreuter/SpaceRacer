package edu.fhm.cs.spaceracing.model.level;

import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.space.Obstacle;
import edu.fhm.cs.spaceracing.model.space.Ring;

/**
 * Circle level with a 25m radius i. e. 50m circumference. The level has rings
 * as obstacles at "every hour" of the clock. Start and finish is at 12 o'clock.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class CircleLevel extends AbstractLevel 
{
	public static final String ID = "level.circle";
	
	public static final int CHECKPOINT_COUNT = 12;
	public static final int RADIUS = 125;
	
	public CircleLevel()
	{
		generate();
	}
	
	private void generate()
	{
		double angleIncrement = 2*Math.PI / CHECKPOINT_COUNT;
		
		for(int i = 0; i < CHECKPOINT_COUNT; i++)
		{
			double x = RADIUS * Math.cos(i*angleIncrement);
			double y = RADIUS * Math.sin(i*angleIncrement);
			double z = 0;
			
			Vector v = new Vector(x, y, z);
			
			Checkpoint c = new Checkpoint(v);
			Obstacle o = new Ring(v);
			
			// Oben ist immer in z-Richtung
			Vector up = new Vector(0, 0, 1).normalize();
			c.setUp(up);
			o.setUp(up);
			
			// Richtungsvektor zeigt jeweils in Richtung der Kreistangente
			Vector direction = up.crossProduct(new Vector(x, y, z)).normalize();
			c.setDirection(direction);
			o.setDirection(direction);
			
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
}
