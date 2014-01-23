package edu.fhm.cs.spaceracing.controller;

import java.util.List;

import edu.fhm.cs.spaceracing.model.Game;
import edu.fhm.cs.spaceracing.model.generic.Sphere;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.model.space.Ring;
import edu.fhm.cs.spaceracing.model.space.SpaceObject;
import edu.fhm.cs.spaceracing.server.controller.collision.Collision;
import edu.fhm.cs.spaceracing.server.controller.collision.CollisionListener;


/**
 * Spielphysik
 * 
 * @author Dominik Kleeeis
 * @author Stefan Kreuter
 */

public class Physics 
{
	private static double MAXIMAL_FORWARD_THRUST = 1.5;
	private static double MAXIMAL_SIDE_THRUST = 0.3;
	
	private Ship ship;
	
	private double forwardThrust = 0;
	private double sideThrust = 0;
	private double rotationX = 0;
	private double rotationY = 0;
	private Vector speedVector;
	private Collision collision;
	private List<SpaceObject> collisionList;
	
	private int thrustChangeSide = 0;
	private int thrustChange = 0;
	
	
	public Physics(Ship ship, Game game)
	{
		this.ship = ship;
		collision = new Collision(game);
	}
	
	private double calcThrust(double thrust, double thrustChange, double max)
	{
		double friction = thrustChange * (Math.abs(thrust) / max);
		return thrust + thrustChange - friction;
	}

	/**
	 * Vorwärts/Rückwärtsbeschleunigung
	 * 
	 * @param thrustChange
	 */
	public void updateThrust(int thrustChange)
	{
		this.thrustChange = thrustChange; 
	}

	/**
	 * Seitwärtsbeschleunigung
	 * 
	 * @param sideThrust
	 */
	public void updateSideThrust(int thrustChangeSide)
	{
		this.thrustChangeSide = thrustChangeSide;
	}
	
	/**
	 * Simulationsschritt
	 */
	public void stepSimulation()
	{
//		stepCount = (stepCount + 1) % 20;
		
		forwardThrust = 
			calcThrust(forwardThrust, 0.05 * thrustChange, MAXIMAL_FORWARD_THRUST);
		
		sideThrust = 
			calcThrust(sideThrust, 0.05 * thrustChangeSide, MAXIMAL_SIDE_THRUST);
		
		//Neue beschleunigung und Geschwindigkeit berechen
		//double ae =	newThrust - (20 * speed ) / 1000;
		//speed = speed + ae * dt;
		
		speedVector = ship.getSpeed();
		
		ship.setPosition(ship.getPosition().add(ship.getSpeed()));
		ship.setPositionOfSpheres(ship.getPosition());
		
		ship.setForwardThrust(forwardThrust*30.0);
		ship.setLateralThrust(sideThrust);		
		
		collision.checkpoints(ship);
						
		collisionList = collision.isColliding(ship);

		if(collisionList != null)
		{
			resolveCollision();
		}
		else
		{
			calculateMovement();
		}
		
		
		// Reibungen (-1%)
		forwardThrust -= 0.005 * forwardThrust;
		sideThrust -= 0.005 * sideThrust;
	}

	
	/**
	 * Richtung des Schiffes berechnen
	 *
	 */
	private void calculateMovement()
	{
		//hoch, runter
		Vector leftVector = ship.getDirection().crossProduct(ship.getUp());
		ship.setDirection(ship.getDirection().rotate(rotationX, leftVector));
		ship.setUp(ship.getUp().rotate(rotationX, leftVector));
		
		// rechts, links
		ship.setDirection(ship.getDirection().rotate(rotationY, ship.getUp()));
		
		// side thrust
//		ship.setDirection(ship.getDirection().rotate(yaw, ship.getDirection()));
//		ship.setUp(ship.getUp().rotate(yaw, ship.getDirection()));
		
		Vector addPos = ship.getDirection().multiplyWithScalar(forwardThrust);
		addPos = addPos.add(leftVector.multiplyWithScalar(sideThrust));
		
		ship.setSpeed(addPos);
	}

		
	/**
	 * x/y Rotation
	 * 
	 * @param rotationX
	 * @param rotationY
	 */
	
	public void updateRotation(double rotationX, double rotationY)
	{
		if(rotationX != 0)
		{
			this.rotationX = -rotationX / 2000;
		}
		
		if(rotationY != 0)
		{
			this.rotationY = -rotationY / 2000;
		}
		
	}
	
	public void register(CollisionListener c)
	{
		collision.register(c);
	}
	
	/**
	 * Schiff hat gefeuert
	 */
	public void fired()
	{
		ship.setFired(true);
	}
	
	
	/** 
	 * Kollisionsbehandlung
	 * 
	 */
	private void resolveCollision()
	{
		for(SpaceObject e : collisionList)
		{
			
			if(e instanceof Ship || e instanceof Ring)
			{				
				
				Sphere s = new Sphere(e.getPosition(), 0);
				
				//Bei Ringen Kollisionskugel Suchen, mit der kollidiert wurde
				if(e instanceof Ring)
					for(Sphere n: e.getSpheres())
						if(n.isFlag())
						{
							s = n;
							break;
						}
				
				//First, find the normalized vector n from the center of
				//circle1 to the center of circle2
				Vector n = ship.getPosition().subtract(s.getPosition());
				n = n.normalize();
						
				
				//Find the length of the component of each of the movement
				//vectors along n. 
				double a1 = speedVector.getDotProduct(n);
				double a2 = e.getSpeed().getDotProduct(n);
				
				double optimizedP = (2.0 * (a1 - a2)) / (ship.getWeight() + e.getWeight());
				
				
				Vector v1new;
				Vector v2new;
				
				//Calculate the new movement vector of circle1
				v1new = speedVector.subtract(n.multiplyWithScalar(optimizedP * e.getWeight()));
				
				//Calculate the new movement vector of circle1
				v2new = e.getSpeed().add(n.multiplyWithScalar(optimizedP * ship.getWeight()));
				
				ship.setSpeed(v1new);
				
				
				//Position des anderen Schiffs neu setzen
				if(e instanceof Ship)
				{
					e.setSpeed(v2new);
					e.setPosition(e.getPosition().add(v2new));
					e.setPositionOfSpheres(e.getPosition());
				}
			}
			else
			{
				calculateMovement();
			}
		}
	}
}


