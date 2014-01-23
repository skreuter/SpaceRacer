package edu.fhm.cs.spaceracing.client.view.spaceobjects;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.sound.EventSoundPlayer;
import edu.fhm.cs.spaceracing.client.sound.SoundOrMusicSource;
import edu.fhm.cs.spaceracing.client.view.GraphicsSettings;
import edu.fhm.cs.spaceracing.client.view.models.Model;
import edu.fhm.cs.spaceracing.client.view.models.milkshapeloader.MS3DModel;
import edu.fhm.cs.spaceracing.client.view.spaceFX.ISFXContainer;
import edu.fhm.cs.spaceracing.client.view.spaceFX.LaserBeam;
import edu.fhm.cs.spaceracing.client.view.spaceFX.ShipTrail;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.space.SpaceObject;

/**
 * Zeichnet ein Raumschiff mit ca. 4000 Polygonen.
 * @author Thomas "HUFFMAN" Brunner
 */
public class Fighter1View extends SpaceObjectView implements ISFXContainer
{
	private float scale = 0.08f;
	private LinkedList<ShipTrail> trails;
	private LaserBeam laserBeam = null;
	private static Model model3D = null;
	
	/**
	 * Erzeugt einen neuen Fighter1.
	 * @param refersTo das korrespondierende SpaceObject.
	 */
	public Fighter1View(SpaceObject refersTo)
	{
		super(refersTo);
		createTrails();
	}
	
	/**
	 * Zeichnet das Raumschiff.
	 * @param gl das GL-Objekt.
	 */
	@Override
	public void drawObject(GL gl)
	{
		if(model3D == null)
			preLoad(gl);
		
		gl.glTranslated(tempPosition.getX(), tempPosition.getY(), tempPosition.getZ());
		gl.glScalef(scale, scale, scale);

		// In die richtige Richtung drehen
		doRotation(gl);
		gl.glRotatef(90, 0, 1, 0);
		gl.glRotatef(270, 1, 0, 0);

		model3D.draw(gl);
	}

	/**
	 * Zeichnet die Spezialeffekte des Raumschiffs (Antriebsspuren und Laserstrahl).
	 */
	public void drawSFX(GL gl)
	{
		for(ShipTrail t : trails)
		{
			gl.glPushMatrix();
			t.draw(gl);
			gl.glPopMatrix();
		}
		if(laserBeam != null)
		{
			gl.glPushMatrix();
			laserBeam.draw(gl);
			gl.glPopMatrix();
			if(laserBeam.isDead())
				laserBeam = null;
		}
	}

	/**
	 * Erzeugt einen Laserstrahl am Raumschiff.
	 * @param target das Ziel des Lasers.
	 * @param hit gibt an, ob das Ziel getroffen wurde.
	 */
	public void fireLaser(Vector target, boolean hit)
	{
//		if(laserBeam != null)
//			return;
		
		Vector start = tempPosition;

		double length = target.subtract(start).getMagnitude();
		laserBeam = new LaserBeam(start.subtract(tempDirection.multiplyWithScalar(length/25)), target, hit);
		
		// HACK: Eigentlich sollte das im Controller passieren
		if(hit)
			new EventSoundPlayer(SoundOrMusicSource.SFXLaserHit).play();
		else
			new EventSoundPlayer(SoundOrMusicSource.SFXLaser).play();
		
	}
	
	// Antriebsspuren erzeugen
	private void createTrails()
	{
		trails = new LinkedList<ShipTrail>();
		
		// Mitte
		trails.add(new ShipTrail(this, new Vector(-0.160,  0.0245, -0.025), 0.8f));
		trails.add(new ShipTrail(this, new Vector(-0.160,  0.0395,  0.000), 1.7f));
		trails.add(new ShipTrail(this, new Vector(-0.160,  0.0245,  0.025), 0.8f));
		
		// Mitte Rechts
		trails.add(new ShipTrail(this, new Vector(-0.180, -0.0040,  0.130)));
		
		// Mitte Links
		trails.add(new ShipTrail(this, new Vector(-0.180, -0.0040, -0.130)));

		// Flügel Rechts
		trails.add(new ShipTrail(this, new Vector(-0.170,  0.0160,  0.260), 1.8f));
		trails.add(new ShipTrail(this, new Vector(-0.170,  0.1000,  0.205), 1.8f));
		
		// Flügel Lniks
		trails.add(new ShipTrail(this, new Vector(-0.170,  0.0160, -0.260), 1.8f));
		trails.add(new ShipTrail(this, new Vector(-0.170,  0.1000, -0.205), 1.8f));
	}

	/**
	 * Lädt das Model des Schiffes und alle zugehörigen Effekte.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		ShipTrail.preLoad(gl);
		
		if(model3D != null)
			model3D.kill(gl);				// Garbage Collection
		
		// Das Model muss in jedem Fall neu erzeugt werden, da evtl. der GL-Kontext nicht mehr vorhanden ist.
		model3D = new MS3DModel("models" + File.separator + "fighter1" + File.separator);
		
		// Lowpoly-Model laden
		if(GraphicsSettings.isLowPolyModels())
		{
			try
			{
				model3D.loadModelData("fighter1-lowpoly.ms3d");
				model3D.preLoad(gl);
				return;
			}
			catch(IOException e)
			{
				System.out.println("Warning: No lowpoly model for Fighter1View!");
			}
		}
		try
		{
			model3D.loadModelData("fighter1.ms3d");
			model3D.preLoad(gl);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
