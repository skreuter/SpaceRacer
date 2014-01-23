package edu.fhm.cs.spaceracing.model.ship;

import java.io.Serializable;

/**
 * Currently unused.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class EnergyDistribution implements Serializable
{
	public static final int MAXIMUM_POINTS = 10*3;
	
	private int thrustPoints = 10;
	private int shieldPoints = 10;
	private int weaponPoints = 10;
	
	public void increaseThrust()
	{
		// TODO range check
		
		thrustPoints += 2;
		shieldPoints -= 1;
		weaponPoints -= 1;
	}
	
	public void increaseShield()
	{
		// TODO range check
		
		thrustPoints -= 1;
		shieldPoints += 2;
		weaponPoints -= 1;
	}
	
	public void increaseWeapon()
	{
		// TODO range check
		
		thrustPoints -= 1;
		shieldPoints -= 1;
		weaponPoints += 2;
	}
	
	// TODO mehr modifikations möglichkeiten
}
