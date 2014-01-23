package edu.fhm.cs.spaceracing.server.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.fhm.cs.spaceracing.client.controller.events.StartGame;
import edu.fhm.cs.spaceracing.server.controller.events.GameStarts;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;

public class GameServerTest
{

	private static GameServer gameServer;

	@BeforeClass
	public static void beforeTest() throws UnableToCreateSocketException
	{
		gameServer = new GameServer(7777);
		new Thread(gameServer).start();
	}

	@Test
	public void getMultipleClientIdsTest() throws UnknownHostException, IOException, ClassNotFoundException
	{
		for(int i = 1; i <= 11; i++)
		{
			Socket socket = new Socket("localhost", 7777);
			ObjectInputStream objectStream = new ObjectInputStream(socket.getInputStream());
			Object object = objectStream.readObject();
			if (object instanceof ClientId)
			{
				ClientId clientId = (ClientId) object;
				assertEquals(i, clientId.getClientId());
			} 
			else
			{
				fail("Falsches Objekt bekommen");
			}
		}
	}
	
	@Test
	public void startGameTest() throws UnknownHostException, IOException, ClassNotFoundException
	{
		Socket socket = new Socket("localhost", 7777);
		ObjectInputStream objectStream = new ObjectInputStream(socket.getInputStream());
		objectStream.readObject(); //ClientId wegschmeissen
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.writeObject(new StartGame());
		Object object = objectStream.readObject();
		assertTrue(object instanceof GameStarts);
	}
	
	@AfterClass
	public static void closeServer()
	{
		gameServer.setShouldStop(true);
	}
}
