package edu.fhm.cs.spaceracing.client.view.spaceobjects;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.view.models.Model;
import edu.fhm.cs.spaceracing.client.view.models.milkshapeloader.MS3DModel;
import edu.fhm.cs.spaceracing.model.space.SpaceObject;

/**
 * Zeichnet einen Ring.
 * @author Thomas "HUFFMAN" Brunner
 */
public class RingView extends SpaceObjectView
{
	private float scale = 0.004f;
	private static Model model3D = null;
	private static Model model3DMarked = null;
	
	
	/**
	 * Erzeugt ein neues RingView.
	 * @param refersTo das zugehörige SpaceObject.
	 */
	public RingView(SpaceObject refersTo)
	{
		super(refersTo);
	}
	
	
	/**
	 * Zeichnet den Ring.
	 * @param gl das GL-Objekt.
	 */
	@Override
	public void drawObject(GL gl)
	{
		if(model3D == null || model3DMarked == null)
			preLoad(gl);

		gl.glTranslated(tempPosition.getX(), tempPosition.getY(), tempPosition.getZ());
		gl.glScalef(scale, scale, scale);
		
		doRotation(gl);
		gl.glRotatef(90, 0, 1, 0);

		// Rote Farbe für markierten Ring, sonst blau
		if(isMarked)
			model3DMarked.draw(gl);
		else
			model3D.draw(gl);
	}

	
	/**
	 * Lädt das Model des Ringes.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		if(model3D != null)
			model3D.kill(gl);				// Garbage Collection

		// Das Model muss in jedem Fall neu erzeugt werden, da evtl. der GL-Kontext nicht mehr vorhanden ist.
		model3D = new MS3DModel("models" + File.separator + "ring" + File.separator);
		model3DMarked = new MS3DModel("models" + File.separator + "ring" + File.separator);
		try
		{
			model3D.loadModelData("ring.ms3d");
			model3D.preLoad(gl);
			model3DMarked.loadModelData("ring-marked.ms3d");
			model3DMarked.preLoad(gl);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
