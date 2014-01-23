package edu.fhm.cs.spaceracing.client.view.spaceobjects;


import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.view.models.Model;
import edu.fhm.cs.spaceracing.client.view.models.milkshapeloader.MS3DModel;
import edu.fhm.cs.spaceracing.client.view.tools.GLTools;
import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Zeichnet einen Pfeil, der auf ein Ziel im Raum zeigt.
 * TODO: In ein HUD integrieren
 * @author Thomas "HUFFMAN" Brunner
 */
public class Arrow3D
{
	private float scale = 0.03f;
	private static Model model3D = null;
	private SpaceObjectView currentTarget = null;

	
	/**
	 * Zeichnet den Pfeil.
	 * @param gl das GL-Objekt.
	 * @param camera die Kamera des Spielers, bei dem der Pfeil angezeigt wird.
	 */
	public void draw(GL gl, Camera camera)
	{
		if(model3D == null)
			preLoad(gl);
		
		if(currentTarget == null)
			return;
		
		// Pfeil vor und über die Kamera setzen
		Vector arrowPos = camera.getPosition().add(camera.getDirection().multiplyWithScalar(-1.5));
		arrowPos = arrowPos.add(camera.getUpVector().multiplyWithScalar(1.3));
		gl.glTranslated(arrowPos.getX(), arrowPos.getY(), arrowPos.getZ());
		
		// In Richtung des Ziels drehen
		Vector direction = currentTarget.getPosition().subtract(camera.getPosition());
		GLTools.doRotation(gl, direction, new Vector(-direction.getZ(), 0, direction.getX()));
		gl.glRotatef(90, 0, 0, 1);
		
		setupLighting(gl);
		gl.glScalef(scale, scale, scale);
			
		gl.glDisable(GL.GL_CULL_FACE);
		model3D.draw(gl);
		gl.glEnable(GL.GL_CULL_FACE);
	}

	// Den Pfeil unabhängig vom Raum beleuchten.
	private void setupLighting(GL gl)
	{
	    float[] lightAmbient = {1.0f, 1.0f, 1.0f, 1.0f};
	    float[] lightDiffuse = {10.0f, 10.0f, 10.0f, 1.0f};
	    float[] lightPosition = {1.0f, 0.0f, 0.0f, 1.0f};
    	gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPosition, 0);

        gl.glEnable(GL.GL_LIGHT1);
        gl.glEnable(GL.GL_LIGHTING);
	}

	/**
	 * Lädt das Model des Pfeiles.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		if(model3D == null)
		{
			model3D = new MS3DModel("models" + File.separator + "arrow" + File.separator);
			try
			{
				model3D.loadModelData("arrow.ms3d");
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Gibt das aktuelle Ziel des Pfeiles zurück.
	 * @return das aktuelle Ziel des Pfeiles.
	 */
	public SpaceObjectView getCurrentTarget()
	{
		return currentTarget;
	}

	/**
	 * Setzt das neue Ziel des Pfeiles.
	 * @param currentTarget das neue Ziel des Pfeiles.
	 */
	public void setCurrentTarget(SpaceObjectView currentTarget)
	{
		this.currentTarget = currentTarget;
	}
}