package edu.fhm.cs.spaceracing.model;

import edu.fhm.cs.spaceracing.model.level.CircleLevel;
import edu.fhm.cs.spaceracing.model.level.EightLevel;
import edu.fhm.cs.spaceracing.model.level.Level;

/**
 * Factory for Game objects.
 * 
 * @see #newGame(String)
 * @author christian.knuechel@gmx.de
 */
public class Factory
{
	/**
	 * Creates a fully configured Game object.
	 * 
	 * @param levelId
	 *            the level that should be loaded into the game.
	 * @return
	 */
	public static Game newGame(String levelId/*, Context context*/)
	{
		Game game = new Game();
		
		// select level
		
		Level level;
		
		if(levelId.equals(CircleLevel.ID))
		{
			level = new CircleLevel();
		}
		else if(levelId.equals(EightLevel.ID))
		{
			level = new EightLevel();
		}
		else
		{
			throw new RuntimeException("Invalid level ID.");
		}
		
		game.setLevel(level);
		
		///
		
		return game;
	}
}
