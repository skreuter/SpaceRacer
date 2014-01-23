package edu.fhm.cs.spaceracing.client.controller;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;

import javax.media.opengl.GLCanvas;
import javax.swing.*;

import edu.fhm.cs.spaceracing.client.view.GraphicsSettings;
import edu.fhm.cs.spaceracing.client.view.ISceneView;
import edu.fhm.cs.spaceracing.client.view.SpaceView;
import edu.fhm.cs.spaceracing.client.view.ViewWindow;
import edu.fhm.cs.spaceracing.frames.*;
import edu.fhm.cs.spaceracing.frames.menus.*;
import edu.fhm.cs.spaceracing.model.Game;
import edu.fhm.cs.spaceracing.model.Player;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.model.space.Space;
import edu.fhm.cs.spaceracing.server.controller.GameServer;
import edu.fhm.cs.spaceracing.server.controller.events.GameWon;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;

/**
 * Der GameManager ist fuer die Initialisierung des eigentlichen Spiels zustaendig.
 * Er wird aufgerufen sobald alle Vorbereitungen abgeschlossen sind und ein Spiel
 * starten kann.
 * 
 * @author Sebastian Gift / Jan Bouillon
 */
class GameManager implements Runnable
{
	private ViewWindow window;
	private final Game game;
	private int xSize = 1024;
	private int ySize = 768;
	private Collection<IController> controller = new HashSet<IController>();
	private boolean windowCreated = false;
	private final GameServer gameServer;
	private final Socket socket;
	private final int playerId;
	//Der InputStream wird bereits in BeforeGame erzeugt. Weitere Aufrufe von getInputStream auf Socket fuehren zu Problemen
	private final ObjectInputStream objectInputStream;

	GameManager(Socket socket, GameServer gameServer, int playerId, Game game, ObjectInputStream objectInputStream) throws IOException
	{
		assert socket != null;
		assert playerId > 0;
		assert game != null;
		assert objectInputStream != null;
		this.playerId = playerId;
		this.socket = socket;
		this.gameServer = gameServer;
		this.game = game;
		this.objectInputStream = objectInputStream;
	}
	
	private void addController(IController controller)
	{
		this.controller.add(controller);
	}
	
	int getXSize()
	{
		return xSize;
	}
	
	int getYSize()
	{
		return ySize;
	}
	
	Game getGame()
	{
		return game;
	}
	
	Ship getShip()
	{
		return getPlayer().getShip();
	}
	
	private boolean isWindowCreated()
	{
		return windowCreated;
	}
	
	private void setWindowCreated(boolean started)
	{
		windowCreated = started;
	}
	
	private int getPlayerId()
	{
		return playerId;
	}
	
	private Player getPlayer()
	{
		//Player anhand von PlayerId finden
		Collection<Player> players = game.getPlayers();
		Player player = null;
		for(Player currentPlayer: players)
		{
			if(currentPlayer.getId() == getPlayerId())
			{
				player = currentPlayer;
			}
		}
		assert player != null;
		return player;
	}
	
	/**
	 * Erzeugt das Spielfenster
	 */
	protected void createGameScene()
	{
		GraphicsSettings.load();
		
		Space space = getGame().getSpace();
		SpaceView spaceView = new SpaceView(space, getShip());
		
		createWindowIfNeededAndSetSceneView(spaceView);
		
		//Schiffscontroller registrieren
		ShipController shipController = null;
		try
		{
			shipController = new ShipController(getPlayerId(), getShip(), getXSize(), getYSize(), game, socket);
		} 
		catch (UnableToCreateSocketException e)
		{
		}
		
		//Listener fuer das Menue entfernen
		removeListeners();
		
		//Listener fuer das eigentliche Spiel hinzufuegen
		window.getCanvas().addKeyListener(shipController);
		window.addKeyListener(shipController);
		window.getCanvas().addMouseMotionListener(shipController);
		window.getCanvas().addMouseListener(shipController);
		addController(shipController);
	}

	/**
	 * Entfernt alle Listene vom Bild-Objekt.
	 */
	private void removeListeners()
	{
		GLCanvas canvas = window.getCanvas();
		
		KeyListener[] listeners = canvas.getListeners(KeyListener.class);
		for (KeyListener listener : listeners)
		{
			canvas.removeKeyListener(listener);
		}
		
		MouseMotionListener[] mouseMotionListeners= canvas.getListeners(MouseMotionListener.class);
		for (MouseMotionListener listener : mouseMotionListeners)
		{
			canvas.removeMouseMotionListener(listener);
		}
		
		MouseListener[] mouseListeners = canvas.getListeners(MouseListener.class);
		for (MouseListener listener : mouseListeners)
		{
			canvas.removeMouseListener(listener);
		}
		
		listeners = window.getListeners(KeyListener.class);
		for (KeyListener listener : listeners)
		{
			window.removeKeyListener(listener);
		}
	}

	private void createWindowIfNeededAndSetSceneView(ISceneView scene)
	{
		if(!isWindowCreated())
		{
			setWindowCreated(true);
			
			//Spielfenster erzeugen
			window = new ViewWindow(scene, getXSize(), getYSize());
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else
		{
			window.setScene(scene);
		}
	}

	/**
	 * Erzeugt die Spielszene und wartet danach auf Informationen ueber das Spielende.
	 */
	public void run()
	{
		createGameScene();
		
		Object object = null;
		try
		{
			object = this.objectInputStream.readObject();
		} 
		catch (IOException e)
		{
			//TODO Sebastian: Sinnvoll abzufangen? Ueberhaupt abfangen?
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			//TODO Sebastian: Sinnvoll abzufangen? Ueberhaupt abfangen?
			e.printStackTrace();
		}
		
		if(object instanceof GameWon)
		{
			GameWon gameWon = (GameWon) object;
            Player winner = null;
			for(Player player: getGame().getPlayers())
			{
				if(player.getId() == gameWon.getWinner())
				{
					 winner = player;
				}
			}
			
			assert winner != null;
		}
	}
	
	public GameServer getGameServer()
	{
		return gameServer;
	}

	protected ViewWindow getWindow()
	{
		return window;
	}
}