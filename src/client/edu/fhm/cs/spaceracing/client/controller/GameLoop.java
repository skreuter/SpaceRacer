package edu.fhm.cs.spaceracing.client.controller;

import edu.fhm.cs.spaceracing.client.controller.events.ShipstateChange;
import edu.fhm.cs.spaceracing.controller.AbstractGameLoop;
import edu.fhm.cs.spaceracing.controller.ControllerConnection;
import edu.fhm.cs.spaceracing.controller.Physics;
import edu.fhm.cs.spaceracing.model.Game;
import edu.fhm.cs.spaceracing.model.Player;
import edu.fhm.cs.spaceracing.model.config.ConfigurationManager;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.server.controller.events.NewShipState;


/**
 * Implementierung der GameLoop fuer den Client.
 * Die GameLoop uebertraegt die vom Server empfangenen Daten ins Model.
 * 
 * @author Sebastian Gift
 */
public class GameLoop extends AbstractGameLoop implements Runnable
{
	private final Physics physics;
	private final ControllerConnection<NewShipState, ShipstateChange> shipstateChangeConnector;
	
	GameLoop(Game game, Physics physics, ControllerConnection<NewShipState, ShipstateChange> shipstateChangeConnector)
	{
		super(game);
		assert physics != null:"Aufruffehler - Physik-Objekt darf nicht null sein.";
		assert shipstateChangeConnector != null: "ShipstateChangeConnector darf nicht null sein.";
		this.physics = physics;
		this.shipstateChangeConnector = shipstateChangeConnector;
	}
	
	@Override
	protected void actions()
	{
		runControllerConnection();
		
		if(ConfigurationManager.get().isDebug())
		{
			//Daten vom Server ins Model uebertragen
			NewShipState newShipState = getServerAnswer();
			while(newShipState != null)
			{
				newShipState.setShip(findShipToPlayerId(newShipState.getPlayerId()));
				newShipState.insertStateIntoShip();
				
				newShipState = getServerAnswer();
			}
		}

		// Lokal voraus rechnen
		physics.stepSimulation();
	}
	
	
	private void runControllerConnection()
	{
		if(ConfigurationManager.get().isLogging())
			System.out.println("Client: send & recv");
		shipstateChangeConnector.run();
	}
	
	private NewShipState getServerAnswer()
	{
		return shipstateChangeConnector.take();
	}
	
	private Ship findShipToPlayerId(int playerId)
	{
		Ship ship = null;
		for(Player player: getGame().getPlayers())
		{
			if(player.getId() == playerId)
			{
				ship = player.getShip();
			}
		}
		
		assert ship != null;
		
		return ship;
	}
}