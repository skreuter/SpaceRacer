package edu.fhm.cs.spaceracing.server.controller;

import edu.fhm.cs.spaceracing.model.Player;
import edu.fhm.cs.spaceracing.model.level.Checkpoint;
import edu.fhm.cs.spaceracing.server.controller.collision.CollisionListener;

/**
 * Entscheidet ob und wann das Spiel gewonnen wurde.
 * 
 * @author Sebastian Gift
 */
class RingChecker implements CollisionListener
{
	private final GameWonListener gameWonListener;
	//Id des Ziels
	private final int targetCheckpointId;
	
	RingChecker(GameWonListener gameWonListener, int targetCheckpointId)
	{
		assert gameWonListener != null:"GameLoop darf nicht null sein.";
		assert targetCheckpointId > 0: "Zielring muss eine Id groeszer 0 haben";
		this.gameWonListener = gameWonListener;
		this.targetCheckpointId = targetCheckpointId;
	}
	
	public void notify(Player player, Checkpoint checkpoint)
	{
		int checkpointId = checkpoint.getId();
		int lastCheckpointIndex = player.getLastCheckpointIndex();
		
		//Richtigen Ring durchflogen
		if(checkpointId == lastCheckpointIndex + 1)
		{
			player.setLastCheckpointIndex(checkpointId);
			//Ziel
			if(checkpointId == this.targetCheckpointId)
			{
				this.gameWonListener.gameWon(player.getId());
			}
		}
	}
}
