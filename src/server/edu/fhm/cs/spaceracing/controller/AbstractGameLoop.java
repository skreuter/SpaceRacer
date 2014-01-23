package edu.fhm.cs.spaceracing.controller;

import edu.fhm.cs.spaceracing.model.*;

/**
 * Implementierung einer GameLoop.
 * Eine GameLoop ist ein Thread der immer wieder die selben Aktionen ausfuehrt
 * nachdem ein bestimmtes Zeitintervall vergangen ist.
 * Diese GameLoop laeuft alle 33 ms.
 * 
 * @author Sebastian Gift
 */
public abstract class AbstractGameLoop
{

	private boolean stop = false;
	private static final long TIME_UNTIL_NEXT_GAMELOOP_IN_NANOSECONDS = 33000000;
	private final Game game;

	public AbstractGameLoop(Game game)
	{
		if(game == null)
			throw new IllegalArgumentException("Game darf nicht null sein.");
		this.game = game;
	}

	public void run()
	{
		while(!stop)
		{
			//Ca. alle 1/30 Sekunde ein Durchlauf
			long startTime = System.nanoTime();
			actions(); 
			long timeToSleep = TIME_UNTIL_NEXT_GAMELOOP_IN_NANOSECONDS - (System.nanoTime() - startTime);
			timeToSleep = timeToSleep/1000000;
			
			if (timeToSleep < 0) 
			{ 
			   timeToSleep = 0;
			} 
			try 
			{ 
			   Thread.sleep(timeToSleep); 
			} 
			catch (InterruptedException e) 
			{
			}
		}
	}

	protected void stopGameloop()
	{
		stop = true;
	}
	
	protected abstract void actions();

	protected Game getGame()
	{
		return game;
	}

}