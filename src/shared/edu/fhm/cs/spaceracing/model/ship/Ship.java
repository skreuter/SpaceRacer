package edu.fhm.cs.spaceracing.model.ship;

import java.util.Collection;
import java.util.HashSet;

import edu.fhm.cs.spaceracing.model.Player;
import edu.fhm.cs.spaceracing.model.generic.Sphere;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.space.SpaceObject;

/**
 * The Ship object represents the vehicle that is flown by a player and holds
 * its physical state and other state.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class Ship extends SpaceObject
{
	private Player player;
	
	private ShipType type = new ShipType();

	/**
	 * Current thrust in N.
	 */
	private double forwardThrust = 0;
	private double lateralThrust = 0;
	
	/**
	 * Current shield level in percent. 100% = full shield.
	 */
	private int shieldLevel = 100;
	
	/**
	 * Current weapon energy level in percent. 100% = full shield.
	 */
	private int weaponLevel = 100;
	
	/**
	 * Schaden am Schiff
	 */
	private ShipCondition condition = new ShipCondition();
	
	/**
	 * Schiff hat gefeuert
	 */
	private boolean fired;
	
	private EnergyDistribution energyDistribution = new EnergyDistribution();

	public Ship(Player player)
	{
		super(null);
		
		this.player = player;
		
		width = 3;
		height = 2;
		length = 6;
		weight = 300;
		
		fired = false;
	
		
		Collection<Sphere> s = new HashSet<Sphere>();
		s.add(new Sphere(new Vector(0,0,0) , 0.3));
		setSphere(s);
		this.setPositionOfSpheres(getPosition());
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Current thrust level in percent. 100% = full shield.
	 */
	public int getThrustLevel()
	{
		return (int) (forwardThrust / type.maximumThrust * 100d);
	}
	
	public double getForwardThrust()
	{
		return forwardThrust;
	}
	
	/**
	 * @param forwardThrust positiv := vorwaerts, negativ := rueckwaerts
	 */
	public void setForwardThrust(double forwardThrust)
	{
		this.forwardThrust = forwardThrust;
	}
	
	public double getLateralThrust()
	{
		return lateralThrust;
	}
	
	/**
	 * @param sidewayThrust positiv := rechts, negativ := links
	 */
	public void setLateralThrust(double lateralThrust)
	{
		this.lateralThrust = lateralThrust;
	}
	

	public ShipCondition getCondition()
	{
		return condition;
	}
	
	public EnergyDistribution getEnergyDistribution()
	{
		return energyDistribution;
	}
	
	public int getShieldLevel()
	{
		return shieldLevel;
	}
	
	public ShipType getType()
	{
		return type;
	}
	
	public int getWeaponLevel()
	{
		return weaponLevel;
	}
	
	public boolean hasFired()
	{
		return this.fired;
	}
	
	public void setFired(boolean fired)
	{
		this.fired = fired;
	}
}
