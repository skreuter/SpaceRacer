package edu.fhm.cs.spaceracing.client.view.hud;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import edu.fhm.cs.spaceracing.client.view.tools.GLTools;

/**
 * HUD-Basisklasse. "Head-Up-Display" für Geschwindigkeitsanzeige usw. 
 * Stellt die Projektion auf eine Ebene parallel zum Bildschirm um.
 * @author Thomas "HUFFMAN" Brunner
 */
public abstract class HUD
{
	private boolean isGameWon = false;
	private int gameWonTex = -1;

	/**
	 * Stellt die Projektion ein und zeichnet das HUD.
	 * @param autoDrawable das GL-Objekt.
	 * @param resolutionX die Fenstergröße.
	 * @param resolutionY die Fenstergröße.
	 */
	public void draw(GLAutoDrawable autoDrawable, int resolutionX, int resolutionY)
	{
		if(gameWonTex == -1)
			gameWonTex = GLTools.loadTexture(autoDrawable.getGL(), "images/HUD/gewonnen.gif");
		
		GL gl = autoDrawable.getGL();
		GLU glu = new GLU();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0.0, 0.0, 1.0, 1.0); 		// 2D-Projektion
		
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glDisable(GL.GL_LIGHTING);				// Das HUD wird nicht beleuchtet
		if(!isGameWon)
			drawContents(autoDrawable, resolutionX, resolutionY);
		else
		{
			// GEWONNEN-Bildschirm
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);	
			gl.glBindTexture(GL.GL_TEXTURE_2D, gameWonTex);
			gl.glColor4f(1,1,1,1);
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(1, 1);  gl.glVertex2d(  1.00f,  1.00f);
				gl.glTexCoord2f(0, 1);	gl.glVertex2d( -1.00f,  1.00f);
				gl.glTexCoord2f(0, 0);	gl.glVertex2d( -1.00f, -1.00f);
				gl.glTexCoord2f(1, 0);	gl.glVertex2d(  1.00f, -1.00f);
			gl.glEnd();
			gl.glDisable(GL.GL_BLEND);
		}
		gl.glEnable(GL.GL_LIGHTING);
	}
	
	/**
	 * Wird aufgerufen, wenn das Spiel gewonnen ist.
	 */
	public void gameWon()
	{
		this.isGameWon = true;
	}
	
	/**
	 * Stellt den Inhalt des HUD dar.
	 * @param autoDrawable das GL-Objekt.
	 * @param resolutionX die Fenstergröße.
	 * @param resolutionY die Fenstergröße.
	 */
	protected abstract void drawContents(GLAutoDrawable autoDrawable, int resolutionX, int resolutionY);
}
