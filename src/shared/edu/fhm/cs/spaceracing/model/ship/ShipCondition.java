package edu.fhm.cs.spaceracing.model.ship;

import java.io.Serializable;

/**
 * Currently unused.
 * 
 * @author christian.knuechel@gmx.de
 */
@SuppressWarnings("serial")
public class ShipCondition implements Serializable
{
	/**
	 * damage in percent. 100% = no damage.
	 */
	int damage = 100;
}
