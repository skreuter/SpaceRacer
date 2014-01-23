package edu.fhm.cs.spaceracing.server.controller;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.fhm.cs.spaceracing.controller.AbstractGameLoop;
import edu.fhm.cs.spaceracing.controller.ControllerConnection;
import edu.fhm.cs.spaceracing.controller.IShipstateChange;
import edu.fhm.cs.spaceracing.controller.Physics;
import edu.fhm.cs.spaceracing.controller.UpdateModel;
import edu.fhm.cs.spaceracing.model.Game;
import edu.fhm.cs.spaceracing.model.Player;
import edu.fhm.cs.spaceracing.model.config.ConfigurationManager;
import edu.fhm.cs.spaceracing.model.level.Checkpoint;
import edu.fhm.cs.spaceracing.server.controller.events.NewShipState;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;

/**
 * Implementierung der GameLoop fuer den Server.
 * Die GameLoop empfaengt die Daten aller Clients, verarbeitet sie
 * und schickt danach die Ergebnisse an die Clients zurueck.
 * 
 * @author Sebastian Gift
 */
class GameLoop extends AbstractGameLoop implements Runnable, GameWonListener
{
	//Player-Id, Physik
	private final Map<Integer, Physics> shipPhysics = new HashMap<Integer, Physics>();
	private final ControllerConnection<IShipstateChange, NewShipState> shipstateChangeConnector;
	private final GameServer gameServer;

	GameLoop(Game game, int port, Set<SocketAddress> playerAddresses, GameServer gameServer) throws UnableToCreateSocketException
	{
		super(game);
		assert playerAddresses != null:"Set der Spieleraddressen darf nicht null sein.";
		assert gameServer != null:"GameServer darf nicht null sein.";
		this.gameServer = gameServer;
		//UDP-Pakete annehmen
		shipstateChangeConnector = new ControllerConnection<IShipstateChange, NewShipState>(port);
		for(SocketAddress playerAddress: playerAddresses)
		{
			shipstateChangeConnector.addClient(playerAddress);
		}
		//Ringchecker anlegen
		RingChecker ringChecker = new RingChecker(this, findTargetCheckpoint());
		//Eine Physik pro Spielerschiff + ringChecker registrieren
		List<Player> players = getGame().getPlayers();
		for(Player player: players)
		{
			Physics physics = new Physics(player.getShip(), game);
			shipPhysics.put(player.getId(), physics);
			physics.register(ringChecker);
		}
	}

	@Override
	protected void actions()
	{
		runControllerConnection();
		
		IShipstateChange shipstateChange = shipstateChangeConnector.take();
		while(shipstateChange != null)
		{
			int playerId = shipstateChange.getPlayerId();
			Physics playerPhysics = getShipPhysics().get(playerId);
			assert playerPhysics != null:"playerPhysic ist null";
			//Aenderungen auf den Server uebernehmen
			new UpdateModel(shipstateChange, playerPhysics).updateChanges();
			
			shipstateChange = shipstateChangeConnector.take();
		}

		//Naechsten Schritt machen
		for(Physics physics: shipPhysics.values())
		{
			physics.stepSimulation();
		}

		//Neuen State fuer jedes Schiff errechnen
		for(Player player: getGame().getPlayers())
		{
			NewShipState newShipState = new NewShipState();
			newShipState.setShip(player.getShip());
			newShipState.getStateFromShip();
			shipstateChangeConnector.put(newShipState);
		}
	}

	private Map<Integer, Physics> getShipPhysics()
	{
		return shipPhysics;
	}

	public void gameWon(int playerId)
	{
		gameServer.finishGame(playerId);
	}

	private int findTargetCheckpoint()
	{
		List<Checkpoint> checkpointList = getGame().getLevel().getCheckpoints();
		int lastCheckpoint = 0;
		for(Checkpoint checkpoint: checkpointList)
		{
			if(checkpoint.getId() > lastCheckpoint)
			{
				lastCheckpoint = checkpoint.getId();
			}
		}
		return lastCheckpoint;
	}

	private void runControllerConnection()
	{
		if(ConfigurationManager.get().isLogging())
			System.out.println("Server: send & recv");
		shipstateChangeConnector.run();
	}
}
