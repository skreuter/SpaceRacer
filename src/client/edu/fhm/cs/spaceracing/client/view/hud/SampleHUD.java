package edu.fhm.cs.spaceracing.client.view.hud;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import edu.fhm.cs.spaceracing.client.view.tools.GLTools;

/**
 * Ein unglaublich geniales Beispiel-HUD.
 * Unbeweglich und in schwarz-weiß.
 * @author Thomas "HUFFMAN" Brunner
 */
public class SampleHUD extends HUD
{
	// Texturnummern
	private static int speedTexture = -1;
	private static int speedMaskTexture = -1;
	private static int positionTexture = -1;
	private static int positionMaskTexture = -1;

	/**
	 * Stellt den Inhalt des HUD dar.
	 * @param gl das GL-Objekt.
	 * @param resolutionX die Fenstergröße.
	 * @param resolutionY die Fenstergröße.
	 */
	protected void drawContents(GLAutoDrawable autoDrawable, int resolutionX, int resolutionY)
	{
		GL gl = autoDrawable.getGL();
		
		if(speedTexture == -1 || speedMaskTexture == -1 || positionTexture == -1 || positionMaskTexture == -1)
			preLoad(gl);
		
		
		gl.glDisable(GL.GL_DEPTH_TEST);					// Immer im Vordergrund, wenn zuletzt gezeichnet
		gl.glEnable(GL.GL_BLEND);	

		drawSpeedMeter(gl);
		drawRanking(gl);
		
		gl.glDisable(GL.GL_BLEND);
		gl.glEnable(GL.GL_DEPTH_TEST);	
		
	}


	// Zeichnet das Ranking (1st, 2nd, 3rd, ...)
	private void drawRanking(GL gl)
	{
		// Schwarzen Hintergrund (Maske) zeichnen
		gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_ZERO);
		gl.glBindTexture(GL.GL_TEXTURE_2D, positionMaskTexture);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.99f, 0.99f);  gl.glVertex2d(  1.00f, -0.35f);
			gl.glTexCoord2f(0.01f, 0.99f);	gl.glVertex2d(  0.45f, -0.35f);
			gl.glTexCoord2f(0.01f, 0.01f);	gl.glVertex2d(  0.45f, -1.00f);
			gl.glTexCoord2f(0.99f, 0.01f);	gl.glVertex2d(  1.00f, -1.00f);
		gl.glEnd();
		
		// Textur zeichnen
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);	
		gl.glBindTexture(GL.GL_TEXTURE_2D, positionTexture);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.99f, 0.99f);  gl.glVertex2d(  1.00f, -0.35f);
			gl.glTexCoord2f(0.01f, 0.99f);	gl.glVertex2d(  0.45f, -0.35f);
			gl.glTexCoord2f(0.01f, 0.01f);	gl.glVertex2d(  0.45f, -1.00f);
			gl.glTexCoord2f(0.99f, 0.01f);	gl.glVertex2d(  1.00f, -1.00f);
		gl.glEnd();
	}


	// Zeichnet die Geschwindigkeitsanzeige.
	private void drawSpeedMeter(GL gl)
	{
		// Schwarzen Hintergrund (Maske) zeichnen
		gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_ZERO);
		gl.glBindTexture(GL.GL_TEXTURE_2D, speedMaskTexture);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.99f, 0.99f);  gl.glVertex2d( -0.45f, -0.35f);
			gl.glTexCoord2f(0.01f, 0.99f);	gl.glVertex2d( -1.00f, -0.35f);
			gl.glTexCoord2f(0.01f, 0.01f);	gl.glVertex2d( -1.00f, -1.00f);
			gl.glTexCoord2f(0.99f, 0.01f);	gl.glVertex2d( -0.45f, -1.00f);
		gl.glEnd();
		
		// Textur zeichnen
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);	
		gl.glBindTexture(GL.GL_TEXTURE_2D, speedTexture);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.99f, 0.99f);  gl.glVertex2d( -0.45f, -0.35f);
			gl.glTexCoord2f(0.01f, 0.99f);	gl.glVertex2d( -1.00f, -0.35f);
			gl.glTexCoord2f(0.01f, 0.01f);	gl.glVertex2d( -1.00f, -1.00f);
			gl.glTexCoord2f(0.99f, 0.01f);	gl.glVertex2d( -0.45f, -1.00f);
		gl.glEnd();
	}

	
	/**
	 * Lädt die Texturen des SampleHUD.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		speedTexture = GLTools.loadTexture(gl, "images/HUD/speed.jpg");
		speedMaskTexture = GLTools.loadTexture(gl, "images/HUD/speed-mask.gif");
		positionTexture = GLTools.loadTexture(gl, "images/HUD/position.jpg");
		positionMaskTexture = GLTools.loadTexture(gl, "images/HUD/position-mask.gif");
	}

}
