package edu.fhm.cs.spaceracing.model.ship;

import java.io.Serializable;

/**
 * Currently unused.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class ShipType implements Serializable
{
	/**
	 * in kg
	 */
	int weight = 3000;
	
	/**
	 * m/s
	 */
	int maximumSpeed = 150;
	
	int maximumThrust = 75; // eventuell aufsplitten in forward/backward/left/right/up/down
	
	int firepower;
	
	int energyRecoveryRate;
}