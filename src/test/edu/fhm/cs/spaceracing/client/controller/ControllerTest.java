package edu.fhm.cs.spaceracing.client.controller;

import java.io.IOException;
import java.net.UnknownHostException;

import edu.fhm.cs.spaceracing.client.controller.events.UnableToStartGameException;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;

public class ControllerTest
{
	public static void main(String... args) throws UnknownHostException, IOException, ClassNotFoundException, UnableToStartGameException, UnableToCreateSocketException
	{
		new BeforeGame(7777, "Alice");
	}
}
