package edu.fhm.cs.spaceracing.client.view.spaceobjects;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Eine 3rd-Person-Kamera, die einem SpaceObjectView hinterherwackelt.
 * @author Thomas "HUFFMAN" Brunner
 */
public class Camera
{
	private SpaceObjectView cameraLookingAt;
	private Vector lastTargetPos;
	private Vector lastTargetDir;
	private Vector lastTargetUp;

	/*
	 * Position und Ausrichtung der Kamera werden auf das SpaceObject
	 * hin interpoliert. 0.0 < INTERPOLATION_FACTOR <= 1.0, wobei
	 * die Kamera bei 1.0 gar nicht wackelt.
	 */
	public static final double INTERPOLATION_FACTOR = 0.5;
	private Vector cameraPosition;
	private Vector cameraDirection;
	private Vector cameraUpVector;

	/**
	 * Erzeugt eine neue 3rd-Person-Camera.
	 * @param cameraLookingAt das zu betrachtende SpaceObject.
	 */
	public Camera(SpaceObjectView cameraLookingAt)
	{
		if(cameraLookingAt == null)
			throw new IllegalArgumentException("Kamera nicht eingestellt: nichts zum anschaun!");
		
		this.cameraLookingAt = cameraLookingAt;
		cameraPosition = cameraLookingAt.getPosition();
		cameraDirection = cameraLookingAt.getDirection();		
		cameraUpVector = cameraLookingAt.getUp();
		lastTargetPos = cameraPosition;
		lastTargetDir = cameraDirection;
		lastTargetUp = cameraUpVector;
	}
	
	/**
	 * Aktualisiert die Camera.
	 * @param gl das GL-Objekt.
	 */
	public void updateCamera(GL gl)
	{
		interpolateVectors();
    	Vector cameraUp = cameraUpVector;
		
    	// Kamera ist hinter dem Objekt - Richtungsvektor abziehen
    	Vector cameraPos = cameraPosition.subtract(cameraDirection.multiplyWithScalar(2.5));

    	// Kamera ein wenig nach oben schieben
    	cameraPos = cameraPos.add(cameraUp.multiplyWithScalar(1));
    	
    	// Kamera schaut in die gleiche Richtung
    	Vector lookAt = cameraPos.add(cameraDirection.multiplyWithScalar(20));
    	
        GLU glu = new GLU();
    	glu.gluLookAt(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ(), 
    				  lookAt.getX(), lookAt.getY(), lookAt.getZ(), 
    				  cameraUpVector.getX(), cameraUpVector.getY(), cameraUpVector.getZ());
	}
	
	/**
	 * Gibt das Kameraziel zurück.
	 * @return das betrachtete SpaceObjectView.
	 */
	public SpaceObjectView getCameraLookingAt()
	{
		return cameraLookingAt;
	}

	/**
	 * Setzt das Kameraziel.
	 * @param cameraLookingAt das zu betrachtende SpaceObjectView
	 */
	public void setCameraLookingAt(SpaceObjectView cameraLookingAt)
	{
		this.cameraLookingAt = cameraLookingAt;
	}

	/** 
	 * Gibt die aktuelle Kamerarichtung zurück.
	 * @return die aktuelle Blickrichtung der Kamera.
	 */
	public Vector getDirection()
	{
		return cameraDirection;
	}

	/**
	 * Gibt die aktuelle Kameraposition zurück.
	 * @return die aktuelle Position der Kamera.
	 */
	public Vector getPosition()
	{
		return cameraPosition;
	}

	/**
	 * Gibt den aktuellen Up-Vektor der Kamera zurück.
	 * @return der aktuelle Up-Vektor der Kamera.
	 */
	public Vector getUpVector()
	{
		return cameraUpVector;
	}
	
	// Langsam an die tatsächliche Position annähern.
	private void interpolateVectors()
	{	
		// Hat sich die Position des Objekts geändert? Kamera annähern!
		if(!lastTargetPos.equals(cameraLookingAt.getPosition()))
		{
			lastTargetPos = cameraLookingAt.getPosition();
			Vector positionDiff = cameraLookingAt.getPosition().subtract(cameraPosition);
			cameraPosition = cameraPosition.add(positionDiff.multiplyWithScalar(INTERPOLATION_FACTOR));
		}
		
		// Richtungsvektor
		if(!lastTargetDir.equals(cameraLookingAt.getDirection()))
		{
			lastTargetDir = cameraLookingAt.getDirection();
			Vector directionDiff = cameraLookingAt.getDirection().subtract(cameraDirection);
			cameraDirection = cameraDirection.add(directionDiff.multiplyWithScalar(INTERPOLATION_FACTOR / 2.5));
		}
		
		// Normalenvektor
		if(!lastTargetUp.equals(cameraLookingAt.getUp()))
		{
			lastTargetUp = cameraLookingAt.getUp();
			Vector upDiff = cameraLookingAt.getUp().subtract(cameraUpVector);
			cameraUpVector = cameraUpVector.add(upDiff.multiplyWithScalar(INTERPOLATION_FACTOR / 2.5));
		}
	}
}
