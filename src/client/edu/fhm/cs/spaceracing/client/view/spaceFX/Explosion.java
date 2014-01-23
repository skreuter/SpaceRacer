package edu.fhm.cs.spaceracing.client.view.spaceFX;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.view.tools.GLTools;
import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Eine Sprite-Explosion.
 * @author Thomas "HUFFMAN" Brunner
 */
public class Explosion
{
	private static final long MILLIS_PER_FRAME = 100;
	private static final int NUM_FRAMES = 16;
	private long startingTimeMillis;
	private Vector position;
	private float scale;
	private boolean isDead = false;
	
	private static int[] textures = null;
	private static final String MODEL_DIR = "models/sfx/explosion/exsprite";
	
	/**
	 * Erzeugt eine neue Explosion.
	 * @param position die Position der Explosion.
	 * @param scale die Skalierungsgröße der Explosion.
	 */
	public Explosion(Vector position, float scale)
	{
		this.position = position;
		this.scale = scale;
		startingTimeMillis = System.currentTimeMillis();
	}
	
	/**
	 * Zeichnet die Explosion.
	 * @param gl das GL-Objekt.
	 */
	public void draw(GL gl)
	{
		if(textures == null)
			preLoad(gl);
		
		if(isDead)
			return;
		
		// Frame auswählen
		long timeElapsedMillis = System.currentTimeMillis() - startingTimeMillis;
		int currentFrame = (int)(timeElapsedMillis / MILLIS_PER_FRAME);
		if(currentFrame >= NUM_FRAMES)
		{
			isDead = true;
			return;
		}

		gl.glTranslated(position.getX(), position.getY(), position.getZ());
		
		// Billboard: Explosion zeigt immer zur Kamera
		GLTools.resetRotation(gl);
		
		// Textur zeichnen
		gl.glDisable(GL.GL_LIGHTING);
        gl.glDepthMask(false);
        gl.glEnable(GL.GL_BLEND);                    
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[currentFrame]);			
		gl.glBegin(GL.GL_TRIANGLE_STRIP);			
			gl.glTexCoord2f(0.0f, 0.0f);					
			gl.glVertex2d(+ scale, - scale);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex2d(+ scale, + scale);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex2d(- scale, - scale);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex2d(- scale, + scale);
		gl.glEnd();		
        gl.glDisable(GL.GL_BLEND);
        gl.glDepthMask(true);
        gl.glEnable(GL.GL_LIGHTING);
    }

	/**
	 * Gibt an, ob die Explosion beendet ist.
	 * @return true, wenn die Explosion vorbei ist.
	 */
	public boolean isDead()
	{
		return isDead;
	}
	
	/**
	 * Lädt die Texturen der Explosion.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		textures = new int[NUM_FRAMES];		 	  		
	    for(int i = 0; i < NUM_FRAMES; i++)
	    	textures[i] = GLTools.loadTexture(gl, MODEL_DIR + "/frame"+i+".jpg");
	}
}
