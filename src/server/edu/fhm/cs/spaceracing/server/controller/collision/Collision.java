package edu.fhm.cs.spaceracing.server.controller.collision;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.fhm.cs.spaceracing.model.Game;
import edu.fhm.cs.spaceracing.model.generic.Sphere;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.level.Checkpoint;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.model.space.SpaceObject;

/**
 * 
 * @author Martin Hackenberg
 *
 */
public class Collision
{
	private final Game game;
	
	//controller macht etwas bei der registrierung falsch darum static
	private static Collection<CollisionListener> listener = new LinkedList<CollisionListener>();
	
	public Collision(Game g)
	{
		game = g;
	}
	
	private boolean isColliding(SpaceObject one, SpaceObject two)
	{
		for(Sphere s: one.getSpheres())
		{
			for(Sphere t: two.getSpheres())
			{
				Vector posOne = s.getPosition();
				Vector posTwo = t.getPosition();
				
				double x = (posOne.getX() - posTwo.getX())*(posOne.getX() - posTwo.getX());
				double y = (posOne.getY() - posTwo.getY())*(posOne.getY() - posTwo.getY());
				double z = (posOne.getZ() - posTwo.getZ())*(posOne.getZ() - posTwo.getZ());
				
				double distance = Math.sqrt(x + y + z);
				
				if(distance <= s.getRadius() + t.getRadius())
				{
					//flags der spheren werden gesetzt
					s.flag();
					t.flag();
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void register(CollisionListener c)
	{
		if(!listener.contains(c))
			listener.add(c);
	}
	
	/**
	 * Prueft ob uebergebens Ship Checkpoints berührt. Ergebnis wird ueber listener notifiziert.
	 * @param s Ship
	 */
	public void checkpoints(Ship s)
	{
		Collection<Checkpoint> check = game.getSpace().getCheckpoints();
		for(Checkpoint o: check)
			if(penetrats(s, o))
			{
				for(CollisionListener l : listener)
				{
					l.notify(s.getPlayer(), o);
				}
			}
	}
	
	/**
	 * Methode zum finden von Kollisionen, und flagt die spheren die kollidieren, für die physik
	 * @param s
	 * @return Liste der Kollidierenden objecte, oder null falls nichts kollidiert
	 */
	public List<SpaceObject> isColliding(SpaceObject s)
	{
		List<SpaceObject> colliding = new LinkedList<SpaceObject>();
		for(SpaceObject e: game.getSpace().getShips())
		{
			if( !s.equals(e) && isColliding(s, e))
				colliding.add(e);
		}
		for(SpaceObject e: game.getSpace().getObstacles())
		{
			if(!s.equals(e) && isColliding(s, e))
				colliding.add(e);
		}
		if(colliding.isEmpty())
			return null;
		else
			return colliding;
	}
	
	/**
	 * Schaut ob ein schiff durch einen checkpoint fliegt
	 */
	private boolean penetrats(Ship s, Checkpoint checkpoint)
	{
		if(inArea(s, checkpoint))
		{
			return true;
			
			//rechnet den abstand des objekts zum checkpoint aus
//			Vector direction = checkpoint.getDirection();
//			Vector normDir = direction.normalize();
//			Vector positionRing = checkpoint.getPosition();
//			Vector positionShip = s.getPosition();
//			Vector ringShip = positionShip.subtract(positionRing);
//			
//			double a = normDir.getDotProduct(ringShip);
//			
//			if(a < 0)
//			{
//				a = -a;
//			}
//			
//			double distance = a;
//			
//			for(Sphere k: s.getSpheres())
//			{
//				if(distance < k.getRadius())
//				{
//					System.out.println("coll");
//					return true;
//				}
//			}
		}
		return false;
	}
	
	/**
	 * schaut ob ein schiff in der n�he eines checkpoints ist
	 */
	private boolean inArea(Ship s, Checkpoint checkpoint)
	{
		Vector posCheckpoint = checkpoint.getPosition();
		double r = checkpoint.getRadius();
		double distance = s.getPosition().distance(posCheckpoint);

		return distance <= r;
	}
	
	public Collection<SpaceObject> shoot(Vector position, Vector direction)
	{
		List<SpaceObject> temp = new LinkedList<SpaceObject>();
		for(SpaceObject e: game.getSpace().getObjects())
		{
			for(Sphere s: e.getSpheres())
			{
				Vector positionSphere = s.getPosition();
				Vector SphereMinusPos = positionSphere.subtract(position);
				double smp = SphereMinusPos.getDotProduct(direction.normalize());
				// ob in richtung des schussvektors liegt
				if(smp>0)
				{
					double smpSquare = SphereMinusPos.getDotProduct(SphereMinusPos);
					smp = smp*smp;
					double d = smpSquare - smp;
				
					if(d <= s.getRadius())
						temp.add(e);
				}
				
			}
		}
		return temp;
	}
	
}
