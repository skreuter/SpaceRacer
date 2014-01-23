package edu.fhm.cs.spaceracing.client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.fhm.cs.spaceracing.client.controller.events.PlayerName;
import edu.fhm.cs.spaceracing.client.controller.events.StartGame;
import edu.fhm.cs.spaceracing.client.controller.events.UnableToStartGameException;
import edu.fhm.cs.spaceracing.model.Game;
import edu.fhm.cs.spaceracing.server.controller.ClientId;
import edu.fhm.cs.spaceracing.server.controller.GameServer;
import edu.fhm.cs.spaceracing.server.controller.events.GameStarts;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;

/**
 * Die Klasse fuer alle Aktionen, die vor dem eigentlichen Spielstart passieren.
 * Hier wird ausgewaehlt, ob der Spieler Server ist oder zu einem bestehenden
 * Spiel verbindet.
 * 
 * @author Sebastian Gift
 */
public class BeforeGame implements Runnable
{
	private Socket socket;
	private Game game;
	private GameServer gameServer = null;
	private int playerId;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	
	/**
	 * Der Client schlieszt sich einem vorhandenen Spiel an.
	 * 
	 * @param server Server zu dem verbunden werden soll.
	 * @param port Port auf dem der Spaceracer-Server lauscht.
	 * @throws UnknownHostException Konnte den Host nicht finden zur uebergebenen URL.
	 * @throws IOException Fehler beim senden oder empfangen von Daten vom/zum Server.
	 * @throws ClassNotFoundException Konnte eine Klasse nicht erzeugen beim TCP Transfer.
	 * @throws UnableToStartGameException Spiel konnte wegen interner Fehler nicht gestartet werden.
	 */
	public BeforeGame(String server, int port, String playername) throws UnknownHostException, IOException, ClassNotFoundException, UnableToStartGameException
	{
		joinGame(server, port, playername);
	}
	
	/**
	 * Der Client startet ein neues Spiel
	 * 
	 * @param port Port auf dem der Spaceracer-Server lauscht.
	 * @throws UnknownHostException Konnte den Host nicht finden zur uebergebenen URL.
	 * @throws IOException Fehler beim senden oder empfangen von Daten vom/zum Server.
	 * @throws ClassNotFoundException Konnte eine Klasse nicht erzeugen beim TCP Transfer.
	 * @throws UnableToStartGameException Spiel konnte wegen interner Fehler nicht gestartet werden.
	 */
	public BeforeGame(int port, String playername) throws UnknownHostException, UnableToCreateSocketException, IOException, ClassNotFoundException, UnableToStartGameException
	{
		createGame(port, playername);
	}
	
	/**
	 * Erledigt das Empfangen und Versenden von Anweisungen an den Server
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws UnableToStartGameException
	 */
	private void waitForCommands() throws IOException, ClassNotFoundException, UnableToStartGameException
	{
		boolean gameBegins = false;
		
		while(!gameBegins)
		{
			Object object = objectInputStream.readObject();
			
			//Hier Abfragen, ob der Server uns andre Anweisungen melden will (Kartenname, usw.)
			
			//Das Spiel soll beginnen
			if(object instanceof GameStarts)
			{
				object = objectInputStream.readObject();
				//Model vom Server kriegen
				if(object instanceof Game)
				{
					game = (Game) object;
				}
				else
				{
					throw new UnableToStartGameException("Konnte das Model nicht empfangen.");
				}
				
				gameBegins = true;
			}
		}
		
		GameManager gameManager = new GameManager(socket, gameServer, playerId, game, objectInputStream);
		//Uebergang von vor dem Spiel ins Spiel
		new Thread(gameManager).start();
	}
	
	Game getGame()
	{
		return game;
	}
	
	/**
	 * Startet ein Spiel mit diesem Client als Server.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws ClassNotFoundException 
	 * @throws UnableToStartGameException 
	 *
	 */
	private void createGame(int port, String playername) throws UnableToCreateSocketException, UnknownHostException, IOException, ClassNotFoundException, UnableToStartGameException
	{
		gameServer = new GameServer(port);
		new Thread(gameServer).start();
		joinGame("localhost", port, playername);
	}
	
	/**
	 * Schlieszt sich einem bereits bestehenden Spiel an.
	 * 
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws UnableToStartGameException 
	 */
	private void joinGame(String server, int port, String playername) throws UnknownHostException, IOException, ClassNotFoundException, UnableToStartGameException
	{
		socket = new Socket(server, port);
		objectInputStream = new ObjectInputStream(socket.getInputStream());
		Object object = objectInputStream.readObject();
		//Spielerid empfangen
		if (object instanceof ClientId)
		{
			ClientId clientId = (ClientId) object;
			playerId = clientId.getClientId();
		}
		else
		{
			throw new UnableToStartGameException("Konnte die Spielerid nicht empfangen.");
		}
		
		//Dem Server unseren Namen mitteilen
		this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.writeObject(new PlayerName(playername, playerId));
		
	}
	
	/**
	 * Sendet das Spiel starten Kommando an den Server
	 * @throws IOException 
	 */
	public void startGame() throws IOException
	{
		objectOutputStream.writeObject(new StartGame());
	}

	public void run() 
	{
		try 
		{
			waitForCommands();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (UnableToStartGameException e) 
		{
		}
	}
}
