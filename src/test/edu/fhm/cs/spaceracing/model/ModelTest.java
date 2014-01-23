package edu.fhm.cs.spaceracing.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.level.CircleLevel;
import edu.fhm.cs.spaceracing.model.level.EightLevel;
import edu.fhm.cs.spaceracing.model.space.Obstacle;

/**
 * Integration test for the model. Tests the game factory, the game object
 * itself and the two level implementations.
 * 
 * @author christian.knuechel@gmx.de
 */
public class ModelTest
{
	@Test
	public void testNewGame()
	{
		/*
		 * circle
		 */
		
		{
			Game g = Factory.newGame(CircleLevel.ID);
			
			assertEquals(CircleLevel.CHECKPOINT_COUNT, g.getSpace().getObstacles().size());
			assertEquals(CircleLevel.CHECKPOINT_COUNT + 1, g.getSpace().getCheckpoints().size());
			
			Obstacle o1 = g.getSpace().getObstacles().get(0);
			Obstacle o2 = g.getSpace().getObstacles().get(CircleLevel.CHECKPOINT_COUNT / 2);
			
			Vector v1 = o1.getPosition();
			Vector v2 = o2.getPosition();
			
			assertEquals(2*CircleLevel.RADIUS, v1.distance(v2));
		}
		
		/*
		 * eight
		 */
		
		{
			Game g = Factory.newGame(EightLevel.ID);
			
			int skippedCheckpointCount = 2;
			
			assertEquals(EightLevel.CHECKPOINT_COUNT - skippedCheckpointCount, g.getSpace().getObstacles().size());
			assertEquals(EightLevel.CHECKPOINT_COUNT - skippedCheckpointCount + 1, g.getSpace().getCheckpoints().size());
			
			Obstacle o1 = g.getSpace().getObstacles().get(0);
			Obstacle o2 = g.getSpace().getObstacles().get(EightLevel.CHECKPOINT_COUNT / 2 - 1);
			
			Vector v1 = o1.getPosition();
			Vector v2 = o2.getPosition();
			
			double a = 2*EightLevel.RADIUS;
			double b = 2*EightLevel.HEIGHT;
			
			double expectedDistance = Math.sqrt(a*a + b*b);
			
			assertEquals(expectedDistance, v1.distance(v2), 0.01);
		}
	}
}
