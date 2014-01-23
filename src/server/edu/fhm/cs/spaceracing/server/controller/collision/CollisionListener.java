package edu.fhm.cs.spaceracing.server.controller.collision;

import edu.fhm.cs.spaceracing.model.Player;
import edu.fhm.cs.spaceracing.model.level.Checkpoint;
/**
 * 
 * @author Martin Hackenberg
 *
 */
public interface CollisionListener
{
	public void notify(Player p, Checkpoint c);
}
