package edu.fhm.cs.spaceracing.client.view.spaceFX;

import java.io.File;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.view.tools.GLTools;
import edu.fhm.cs.spaceracing.model.generic.Vector;


/**
 * Ein Partikel eines Antriebsschweifs.
 * @author Thomas "HUFFMAN" Brunner
 */
public class ShipTrailParticle
{
	private Vector startingPoint;
	private Vector endingPoint;
	private float scale = 0.02f;
	
	private long timeToLiveMillis = 600;
	private long spawningTimeMillis;
	private boolean isDead = false;
	
	private int displayList = -1;
	
	private static final String MODEL_DIR = "models/sfx/shiptrail";
	private static int particleTex = -1;
	private static int fadingParticleTex = -1;


	/**
	 * Erzeugt ein neues ShipTrailParticle von startingPoint nach endingPoint.
	 * @param startingPoint Startpunkt des Partikels
	 * @param endingPoint Endpunkt des Partikels
	 * @param scale der Skalierungsfaktor.
	 */
	public ShipTrailParticle(Vector startingPoint, Vector endingPoint, float scale)
	{
		this.startingPoint = startingPoint;
		this.endingPoint = endingPoint;
		this.scale *= scale;									// Kleinere scale = dünnerer Schweif
		timeToLiveMillis *= scale;								// Kleinere scale = kürzerer Schweif
		spawningTimeMillis = System.currentTimeMillis();
	}
	
	/**
	 * Erzeugt ein neues ShipTrailParticle von startingPoint nach endingPoint.
	 * @param startingPoint Startpunkt des Partikels
	 * @param endingPoint Endpunkt des Partikels
	 */
	public ShipTrailParticle(Vector start, Vector end)
	{
		this(start, end, 1.0f);
	}
	
	
	/**
	 * Zeichnet das ShipTrailParticle.
	 * @param gl das GL-Objekt.
	 * @param fade Gibt an, ob das Partikel mit Fade-in-Textur gezeichnet werden soll.
	 */
	public void draw(GL gl, boolean fade)
	{
		if(particleTex == -1 || fadingParticleTex == -1)
			preLoad(gl);

		if(displayList == -1)
			genList(gl);


		// Ist das Partikel tot?
		long timeLeftMillis = timeToLiveMillis - (System.currentTimeMillis() - spawningTimeMillis);
		if(timeLeftMillis <= 0)
		{
			isDead = true;
			return;
		}

		// Mit der Zeit durchsichtig werden
		float alpha = (float)timeLeftMillis / (float)timeToLiveMillis;

		if(fade)
			gl.glBindTexture(GL.GL_TEXTURE_2D, fadingParticleTex);
		else
			gl.glBindTexture(GL.GL_TEXTURE_2D, particleTex);
		
		gl.glColor4f(.7f, .8f, 1, alpha * alpha * 0.5f);
		gl.glCallList(displayList);
	}
	
	
	// Kompiliert das gesamte Partikel in eine DisplayList. Beschleunigt die Darstellung ein wenig.
	private void genList(GL gl)
	{
		displayList = gl.glGenLists(1);

		// Displaylist zusammenstellen
		gl.glNewList(displayList, GL.GL_COMPILE);
			gl.glTranslated(startingPoint.getX(), startingPoint.getY(), startingPoint.getZ());
			
			// Das Partikel zeigt von (start) nach (end)
			Vector direction = endingPoint.subtract(startingPoint);
			
			// Irgendeinen Up-Vector ausdenken
			Vector up = new Vector(direction.getZ(), 0, -direction.getX());
			
			// Dorthin rotieren
			GLTools.doRotation(gl, direction, up);
			gl.glRotatef(90, 1, 0, 0);
			
			double length = direction.getMagnitude();
			
			// Ineinander gefächerte Sprites zeichnen
			for(int i = 0; i < 3; i++)
			{
				gl.glRotatef(60, 1, 0, 0);
				gl.glBegin(GL.GL_QUADS);			
					gl.glTexCoord2f(0.0f, 0.0f);					
					gl.glVertex2d(-0.04f * length, scale);
					gl.glTexCoord2f(0.0f, 1.0f);
					gl.glVertex2d(length,  scale);
					gl.glTexCoord2f(1.0f, 1.0f);
					gl.glVertex2d(length, -scale);
					gl.glTexCoord2f(1.0f, 0.0f);
					gl.glVertex2d(-0.04f * length, -scale);
				gl.glEnd();		
			}
		gl.glEndList();


	}
	
	/**
	 * Gibt zurück, ob das Partikel tot (unsichtbar) ist.
	 * @return true, wenn das Partikel tot ist.
	 */
	public boolean isDead()
	{
		return isDead;
	}
	
	/**
	 * MUSS aufgerufen werden, damit die Displaylisten freigegeben werden.
	 * @param gl das GL-Objekt.
	 */
	public void kill(GL gl)
	{
		gl.glDeleteLists(displayList, 1);
	}
	
	/**
	 * Lädt die Texturen des ShipTrails.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		particleTex = GLTools.loadTexture(gl, MODEL_DIR + File.separator + "shiptrail-smooth2.gif");
		fadingParticleTex = GLTools.loadTexture(gl, MODEL_DIR + File.separator + "shiptrail-smooth2-fade.gif");
	}
}
