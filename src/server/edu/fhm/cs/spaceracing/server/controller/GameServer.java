package edu.fhm.cs.spaceracing.server.controller;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.fhm.cs.spaceracing.client.controller.events.PlayerName;
import edu.fhm.cs.spaceracing.client.controller.events.StartGame;
import edu.fhm.cs.spaceracing.model.Factory;
import edu.fhm.cs.spaceracing.model.Game;
import edu.fhm.cs.spaceracing.model.Player;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.level.EightLevel;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.net.tcp.Server;
import edu.fhm.cs.spaceracing.net.tcp.SynFIFO;
import edu.fhm.cs.spaceracing.net.tcp.events.CloseConnection;
import edu.fhm.cs.spaceracing.net.tcp.events.NewConnection;
import edu.fhm.cs.spaceracing.server.controller.events.GameStarts;
import edu.fhm.cs.spaceracing.server.controller.events.GameWon;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;

/**
 * GameServer von Spaceracer. 
 * Er verwaltet das Spiel, nimmt neue Clients an und schmeisst
 * Clients raus, die nicht mehr anwesend sind.
 * 
 * @author Sebastian Gift
 */
public class GameServer implements Runnable
{
	private final Server server;
	private int lastGivenId = 0;
	private final Map<Integer, NewConnection> connections = new HashMap<Integer, NewConnection>();
	private final SynFIFO<Object> fifo;
	private boolean shouldStop = false;
	private GameLoop gameLoop;
	private Game game;
	private final int port;
	private final List<PlayerName> playerNames = new ArrayList<PlayerName>();
	
	public GameServer(int port) throws UnableToCreateSocketException
	{
		//Keine privilegierten Ports verwenden
		if(port <= 1024)
		{
			throw new IllegalArgumentException("Das Spiel kann nur auf Ports groeszer als 1024 laufen.");
		}
		
		fifo = new SynFIFO<Object>();
		this.port = port;
		
		try
		{
			server = new Server(port, fifo);
		}
		catch (IOException e)
		{
			//Dem Aufrufer die Chance geben sich vernuenftig zu beenden
			throw new UnableToCreateSocketException();
		}
	}
	
	private void acceptBeforeGameRequests()
	{	
		boolean gameBegins = false;
		
		while(!gameBegins && !shouldStop())
		{
			Object newClientObject = fifo.take();
			if(newClientObject instanceof NewConnection)
			{
				//Sobald eine neue Verbindung bei uns eingeht diese Verbindung speichern
				//und ihr eine ClientID zuweisen
				NewConnection newClient = (NewConnection) newClientObject;
				addClient(newClient);
			}
			else if(newClientObject instanceof CloseConnection)
			{
				deleteClient();
			}
			else if(newClientObject instanceof StartGame)
			{
				gameBegins = true;
			}
			else if(newClientObject instanceof PlayerName)
			{
				PlayerName playerName = (PlayerName) newClientObject;
				playerNames.add(playerName);
			}
			
			try
			{
				Thread.sleep(100);
			} 
			catch (InterruptedException e)
			{}
		}
	}
	
	private void informClientsAboutGameBegin()
	{
		Map<Integer, NewConnection> clients = getConnections();
		for(Map.Entry<Integer, NewConnection> clientEntry: clients.entrySet())
		{
			NewConnection client = clientEntry.getValue();
			try
			{
				client.getClientWriter().send(new GameStarts());
				client.getClientWriter().send(this.game);
			} 
			catch (IOException e)
			{
				//Client vermutlich unerreichbar -> weiteren Exceptions vorbeugen
				clients.remove(clientEntry);
				e.printStackTrace();
			}
		}
	}
	
	private void addClient(NewConnection newClient)
	{
		int clientId = lastGivenId + 1;
		lastGivenId++;
		connections.put(clientId, newClient);
		try
		{
			newClient.getClientWriter().send(new ClientId(clientId));
		} 
		catch (IOException e)
		{
			//Irgendwas ist schief gelaufen
			//Client wieder aus der Liste loeschen
			connections.remove(clientId);
			lastGivenId--;
		}
	}
	
	private void deleteClient()
	{
		//Client loeschen
	}

	public void run()
	{
		
		//Ab jetzt Anmeldungen von Clients annehmen
		server.start();
		acceptBeforeGameRequests();
		
		if(shouldStop())
		{
			stopServer();
			return;
		}
		
		//Ab hier werden keine Clients mehr angenommen
		//und es koennen keine Einstellungen mehr geaendert werden
		buildServerModel();
		informClientsAboutGameBegin();
		try
		{
			startGameLoop();
		} 
		catch (UnableToCreateSocketException e)
		{
			e.printStackTrace();
		}
	}
	
	private void buildServerModel()
	{
		this.game = Factory.newGame(EightLevel.ID);
		
		//Alle angemeldeten Spieler und ihre Schiffe anlegen
		for(PlayerName playerName: this.playerNames)
		{
			game.addPlayer(new Player(playerName.getPlayerid(), playerName.getPlayername()));
		}
		
		for (Player p : game.getPlayers())
		{
			p.setShip(new Ship(p));
			if(p.getId() == 1)
				p.getShip().setPosition(new Vector(10,0,0));
		}
		
		game.fillSpace();
		game.assignStartPositions();
	}
	
	private boolean shouldStop()
	{
		return shouldStop;
	}
	
	public void setShouldStop(boolean shouldStop)
	{
		this.shouldStop = shouldStop;
	}
	
	private void stopServer()
	{
		try
		{
			server.shutdown();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private Map<Integer, NewConnection> getConnections()
	{
		return connections;
	}
	
	void finishGame(int winner)
	{
		Map<Integer, NewConnection> clients = getConnections();
		for(Map.Entry<Integer, NewConnection> clientEntry: clients.entrySet())
		{
			NewConnection client = clientEntry.getValue();
			try
			{
				client.getClientWriter().send(new GameWon(winner));
			} 
			catch (IOException e)
			{
				//Das Spiel endet mit dieser Mittelung so oder so - ignorieren
			}
		}
	}
	
	private void startGameLoop() throws UnableToCreateSocketException
	{
		//Adressen aller Clients holen
		Collection<NewConnection>  addresses = this.connections.values();
		Set<SocketAddress> socketAddresses = new HashSet<SocketAddress>();
		for(NewConnection address: addresses)
		{
			//Aus der Connection den Writer ziehen, dann den Socket und daraus dann die Adresse des Clients
			socketAddresses.add(address.getClientWriter().getClientSocket().getRemoteSocketAddress());
		}
		
		this.gameLoop = new GameLoop(game, port, socketAddresses, this);
		new Thread(this.gameLoop).start();
	}
}