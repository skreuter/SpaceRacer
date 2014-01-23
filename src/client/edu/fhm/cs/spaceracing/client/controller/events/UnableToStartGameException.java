package edu.fhm.cs.spaceracing.client.controller.events;

/**
 * Zeigt an, dass das Spiel nicht gestartet werden konnte wegen eines internen Fehlers.
 * Empfaenger sollten sich selbst korrekt beenden und die Meldung danach weiterreichen.
 * 
 * @author Sebastian Gift
 */
public class UnableToStartGameException extends Exception
{
	private static final long serialVersionUID = 1791801283181407772L;

	public UnableToStartGameException()
	{
		super();
	}
	
	public UnableToStartGameException(String string)
	{
		super(string);
	}
}
