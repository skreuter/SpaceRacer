package edu.fhm.cs.spaceracing.client.view.spaceFX;

import java.util.LinkedList;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.view.GraphicsSettings;
import edu.fhm.cs.spaceracing.client.view.spaceobjects.SpaceObjectView;
import edu.fhm.cs.spaceracing.client.view.tools.GLTools;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.ship.Ship;

/**
 * Eine Abgasspur aus Partikeln, die ein Schiff hinter sich herzieht.
 * @author Thomas "HUFFMAN" Brunner
 */
public class ShipTrail
{
	private SpaceObjectView owner;
	private LinkedList<ShipTrailParticle> particles;
	
	private long spawnIntervalMillis;
	private long lastSpawnedMillis;
		
	private float scale = 1.0f;
	private Vector spawningPointOffset;
	private Vector lastSpawningPoint;

	private static int thrusterTexture = -1;

	/**
	 * Erzeugt ein neues ShipTrail.
	 * @param owner der Besitzer der Abgasspur. Muss ein Ship als SpaceObject haben!
	 * @param spawningPointOffset ein Offset vom Schiffsmittelpunkt, an welchem gezeichnet wird.
	 */
	public ShipTrail(SpaceObjectView owner, Vector spawningPointOffset)
	{
		if(!(owner.getRefersTo() instanceof Ship))
			throw new IllegalArgumentException("Only Ships can have ShipTrails!");

		this.owner = owner;
		this.spawningPointOffset = spawningPointOffset;
		lastSpawnedMillis = 0;
		lastSpawningPoint = owner.getPosition();
		particles = new LinkedList<ShipTrailParticle>();

		spawnIntervalMillis = (long)(35 / GraphicsSettings.getParticleDensityFactor());
	}
	
	/**
	 * Erzeugt ein neues ShipTrail.
	 * @param owner der Besitzer der Abgasspur. Muss ein Ship als SpaceObject haben!
	 * @param spawningPointOffset ein Offset vom Schiffsmittelpunkt, an welchem gezeichnet wird.
	 * @param scale der Skalierungsfaktor.
	 */
	public ShipTrail(SpaceObjectView owner, Vector spawningPointOffset, float scale)
	{
		this(owner, spawningPointOffset);
		this.scale = scale;
	}

	/**
	 * Zeichnet das ShipTrail.
	 * @param gl das GL-Objekt.
	 */
	public void draw(GL gl)
	{
		if(thrusterTexture == -1)
			preLoad(gl);
		
		// Ohne Schub keine Abgasspur
		if(calculateThrust() == 0 && particles.size() == 0)
			return;
		
		// Neues Partikel erzeugen
		if(System.currentTimeMillis() - lastSpawnedMillis >= spawnIntervalMillis)
			spawnParticle();


        gl.glDepthMask(false);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_BLEND);      
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		gl.glDisable(GL.GL_CULL_FACE);				// Rückseite sichtbar
		
		// Die Abgaspartikel zeichnen
		ShipTrailParticle markedForKilling = null;
		for(ShipTrailParticle particle : particles)
		{
			if(particle.isDead())
				markedForKilling = particle;
			else
			{
				gl.glPushMatrix();
				particle.draw(gl, false);
				gl.glPopMatrix();
			}
		}
		if(markedForKilling != null)
		{
			particles.remove(markedForKilling);	      // "totes" Partikel entfernen
			markedForKilling.kill(gl);				  // Aufräumen
		}
		
		// Einen "Übergangspartikel" direkt am Schiff zeichnen
		Vector newSpawningPoint = owner.getPosition();	
		newSpawningPoint = newSpawningPoint.add(calculateRotationOffset());
		ShipTrailParticle lastParticle = new ShipTrailParticle(lastSpawningPoint, newSpawningPoint);
		gl.glPushMatrix();
		lastParticle.draw(gl, true);
		gl.glPopMatrix();
		lastParticle.kill(gl);
		
		// Den Triebwerksausstoß zeichnen
		gl.glPushMatrix();
		drawThruster(gl);
		gl.glPopMatrix();
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);		// Farbe zurücksetzen 
		gl.glEnable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_BLEND);                                   
        gl.glEnable(GL.GL_LIGHTING);
        gl.glDepthMask(true);
	}
	
	
	// Zeichnet einen Triebwerksausstoß auf der Partikelquelle.
	private void drawThruster(GL gl)
	{	
		Vector pos = owner.getPosition();
		Vector dir = owner.getDirection();
		Vector up = owner.getUp();
		
		// Zur Schiffsposition bewegen
		gl.glTranslated(pos.getX(), pos.getY(), pos.getZ());
		
		// Offset zusammen mit dem Schiff rotieren
		Vector rotationOffset = calculateRotationOffset();
		gl.glTranslated(rotationOffset.getX(), rotationOffset.getY(), rotationOffset.getZ());

		// Ausrichtung des Thrusters mit Schiff drehen
		GLTools.doRotation(gl, dir.multiplyWithScalar(-1), up);		
		
		
		float thrusterScale = 0.2f * calculateThrust() * scale;
		float length = 2.0f * thrusterScale;
		
		// Ineinander gefächerte Sprites zeichnen
		gl.glBindTexture(GL.GL_TEXTURE_2D, thrusterTexture);
		gl.glColor4f(.7f, .8f, 1, .7f);
		for(int i = 0; i < 3; i++)
		{
			gl.glRotatef(60, 1, 0, 0);
			gl.glBegin(GL.GL_QUADS);			
				gl.glTexCoord2f(0.0f, 0.0f);					
				gl.glVertex2d(0.0f, thrusterScale);
				gl.glTexCoord2f(0.0f, 1.0f);
				gl.glVertex2d(length,  thrusterScale);
				gl.glTexCoord2f(1.0f, 1.0f);
				gl.glVertex2d(length, -thrusterScale);
				gl.glTexCoord2f(1.0f, 0.0f);
				gl.glVertex2d(0.0f, -thrusterScale);
			gl.glEnd();		
		}
	}
	
	
	// Erzeugt einen neuen Abgaspartikel am Schiff.
	private void spawnParticle()
	{
		// Zeitabstand messen
		lastSpawnedMillis = System.currentTimeMillis();
		
		// Spawnpunkt ist Position des Schiffes + (gedrehtes) Offset
		Vector newSpawningPoint = owner.getPosition();	
		newSpawningPoint = newSpawningPoint.add(calculateRotationOffset());
		
		particles.add(new ShipTrailParticle(lastSpawningPoint, newSpawningPoint, calculateThrust() * scale));
		lastSpawningPoint = newSpawningPoint;
	}
	
	
	// Rotiert das Offset zusammen mit dem owner-Schiff.
	private Vector calculateRotationOffset()
	{
		Vector dir = owner.getDirection();
		Vector up = owner.getUp();
		Vector right = dir.crossProduct(up);
		
		return dir.multiplyWithScalar(spawningPointOffset.getX())
		  .add(up.multiplyWithScalar(spawningPointOffset.getY())
		  .add(right.multiplyWithScalar(spawningPointOffset.getZ())));
	}
	

	// Rechnet den momentanen Schub des Schiffes aus.
	// TODO: Thruster von Schub abhängig machen, Spur von Geschwindigkeit 
	private float calculateThrust()
	{
		Ship s = (Ship)owner.getRefersTo();

		float thrustLevel = 4 * Math.abs(s.getThrustLevel()) / 100.0f;
		return thrustLevel;
	}
	
	/**
	 * Lädt die Texturen des ShipTrails.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		thrusterTexture = GLTools.loadTexture(gl, "models/sfx/shiptrail/thruster.gif");
		ShipTrailParticle.preLoad(gl);
	}
}

