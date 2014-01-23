package edu.fhm.cs.spaceracing.controller;

import edu.fhm.cs.spaceracing.client.controller.events.ShipstateChange;

/**
 * Diese Klasse gibt die Aenderungen des Schiffsstatus an die Physik weiter.
 * 
 * @author Sebastian Gift
 */
public class UpdateModel
{
	private IShipstateChange shipstateChange;
	private final Physics physics;
	
	public UpdateModel(IShipstateChange shipstateChange, Physics physics)
	{
		if(shipstateChange == null)
			throw new IllegalArgumentException("shipstateChange darf nicht null sein.");
		if(physics == null)
			throw new IllegalArgumentException("physics darf nicht null sein.");
		this.shipstateChange = shipstateChange;
		this.physics = physics;
	}
	
	public void updateChanges()
	{
		if(shipstateChange.isFired())
		{
			physics.fired();
			shipstateChange.setFired(false);
		}
		physics.updateThrust(shipstateChange.getForwardThrustChange());
		physics.updateSideThrust(shipstateChange.getSidewaysThrustChange());
		physics.updateRotation(shipstateChange.getXRotation(), shipstateChange.getYRotation());
	}
	
	public void setShipstateChange(ShipstateChange shipstateChange)
	{
		if(shipstateChange == null)
			throw new IllegalArgumentException("shipstateChange darf nicht null sein.");
		this.shipstateChange = shipstateChange;
	}
}