package edu.fhm.cs.spaceracing.server.controller;

/**
 * Nimmt den Sieger des Spiels entgegen.
 * vgl Observer
 * 
 * @author Sebastian Gift
 */
public interface GameWonListener
{
	void gameWon(int playerId);
}
