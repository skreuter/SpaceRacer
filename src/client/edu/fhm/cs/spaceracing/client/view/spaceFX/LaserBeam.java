package edu.fhm.cs.spaceracing.client.view.spaceFX;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.view.tools.GLTools;
import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Eine Laserstrahl (aus einer Kanone).
 * @author Thomas "HUFFMAN" Brunner
 */
public class LaserBeam
{
	private Vector start;
	private Vector end;
	private boolean isDead = false;
	
	private float scale = 1.5f;
	private float rotate = 0.0f;
	
	private Explosion impactEffect = null;
	
	private long startingTimeMillis;
	private static final long DURATION_MILLIS = 1000;

	private static int beamTexture = -1;
	
	
	/**
	 * Erzeugt einen neuen Laserstrahl.
	 * @param from Startpunkt.
	 * @param to Endpunkt.
	 * @param das Ziel wurde getroffen.
	 */
	public LaserBeam(Vector from, Vector to, boolean hit)
	{
		this.start = from;
		this.end = to;
		startingTimeMillis = System.currentTimeMillis();
		if(hit)
			impactEffect = new Explosion(to, 8.0f);
	}

	/**
	 * Gibt zurück, ob die Animation des Strahls fertig ist.
	 * @return true für fertig.
	 */
	public boolean isDead()
	{
		return isDead;
	}

	
	/**
	 * Zeichnet den Laserstrahl.
	 * @param gl das GL-Objekt.
	 */
	public void draw(GL gl)
	{
		if(beamTexture == -1)
			preLoad(gl);
		
		if(isDead)
			return;
		
		long currentTimeMillis = System.currentTimeMillis() - startingTimeMillis;
		if(currentTimeMillis > DURATION_MILLIS)
		{
			// Auf den Einschlags-Effekt warten
			if(impactEffect == null || impactEffect.isDead())
			{
				isDead = true;
				return;
			}
			impactEffect.draw(gl);
			return;
		}
		
		gl.glPushMatrix();
		impactEffect.draw(gl);
		gl.glPopMatrix();
		
		// Schnell einblenden, aber langsam ausblenden.
		float alpha;
		float sinWave = (float)Math.sin((Math.PI * currentTimeMillis) / DURATION_MILLIS);
		if((float)DURATION_MILLIS / (float)currentTimeMillis < 0.5)
			alpha = (float)Math.sqrt(sinWave);
		else
			alpha = sinWave;

		
        gl.glDepthMask(false);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_BLEND);      
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		gl.glDisable(GL.GL_CULL_FACE);				// Rückseite sichtbar
		
		// Zur Schiffsposition bewegen
		gl.glTranslated(start.getX(), start.getY(), start.getZ());
		
		
		// Ausrichtung des Lasers bestimmen
		Vector dir = end.subtract(start);
		GLTools.doRotation(gl, dir, new Vector(dir.getY(), -dir.getX(), 0));		
		gl.glRotatef(90, 1, 0, 0);
		
		// Laser um die eigene Achse drehen
		rotate += 2f;
		gl.glRotatef(rotate, 1, 0, 0);
		
		double length = dir.getMagnitude();
		
		// Ineinander gefächerte Sprites zeichnen
		gl.glBindTexture(GL.GL_TEXTURE_2D, beamTexture);
		gl.glColor4f(.9f, .5f, .5f, alpha);
		for(int i = 0; i < 6; i++)
		{
			gl.glRotatef(30, 1, 0, 0);
			gl.glBegin(GL.GL_QUADS);			
				gl.glTexCoord2f(0.0f, 0.0f);					
				gl.glVertex3d(length, scale, 0);
				gl.glTexCoord2f(0.0f, 1.0f);
				gl.glVertex3d(0.0f,  scale, 0);
				gl.glTexCoord2f(1.0f, 1.0f);
				gl.glVertex3d(0.0f, -scale, 0);
				gl.glTexCoord2f(1.0f, 0.0f);
				gl.glVertex3d(length, -scale, 0);
			gl.glEnd();		
		}

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);		// Farbe zurücksetzen 
		gl.glEnable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_BLEND);                                   
        gl.glEnable(GL.GL_LIGHTING);
        gl.glDepthMask(true);
	}
	
	
	/**
	 * Lädt die Texturen der Explosion.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		Explosion.preLoad(gl);
		beamTexture = GLTools.loadTexture(gl, "models/sfx/laserbeam/beam1.gif");
	}
}
