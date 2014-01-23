package edu.fhm.cs.spaceracing.model.context;

/**
 * Describes a level that is available in the game.
 * 
 * The id can be passed to
 * {@link edu.fhm.cs.spaceracing.model.Factory#newGame(String)} when the actual
 * game is created.
 */
public class LevelDescriptor
{
	private String id;
	private String name;
	
	public LevelDescriptor(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
}
