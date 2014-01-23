package edu.fhm.cs.spaceracing.client.view.spaceobjects;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.view.tools.GLTools;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.space.SpaceObject;


/**
 * Zeichnet ein SpaceObject.
 * @author Thomas "HUFFMAN" Brunner
 */
public abstract class SpaceObjectView
{
	private final SpaceObject refersTo;
	
	// Position und Ausrichtung zwischenspeichern während eines Draw-Loops
	protected Vector tempPosition;
	protected Vector tempDirection;
	protected Vector tempUp;
	
	// Besondere Darstellung des Objekts, z.B. einfärben
	protected boolean isMarked;
	


	/**
	 * Erzeugt ein neues SpaceObjectView.
	 * @param refersTo das korrespondierende SpaceObject.
	 */
	protected SpaceObjectView(SpaceObject refersTo)
	{
		this.refersTo = refersTo;
		isMarked = false;
		updateVectors();
	}
	
	/**
	 * Zeichnet das SpaceObject.
	 * @param gl der OpenGL-Kontext
	 */
	public abstract void drawObject(GL gl);
		
	/**
	 * Dreht die Koordinatenachsen in Richtung des SpaceObjects
	 * @param gl der OpenGL-Kontext
	 */
	protected void doRotation(GL gl)
	{	
		GLTools.doRotation(gl, tempDirection.normalize(), tempUp.normalize());
	}
	
	/**
	 * Gibt das korrespondierende SpaceObject zurück.
	 * @return das korrespondierende SpaceObject.
	 */
	public SpaceObject getRefersTo()
	{
		return refersTo;
	}
	
	/**
	 * Gibt den zwischengespeicherten Richtungsvektor des SpaceObjectView zurück.
	 * @return der Richtungsvektor.
	 */
	public Vector getDirection()
	{
		return tempDirection;
	}

	/**
	 * Gibt die zwischengespeicherte Position des SpaceObjectView zurück.
	 * @return die Position.
	 */
	public Vector getPosition()
	{
		return tempPosition;
	}

	/**
	 * Gibt den zwischengespeicherten Up-Vektor des SpaceObjectView zurück.
	 * @return der Up-Vektor.
	 */
	public Vector getUp()
	{
		return tempUp;
	}

	/**
	 * Setzt die Markierung des SpaceObjectViews.
	 * @param isMarked die Markierung. true für markiert, false für unmarkiert.
	 */
	public void mark(boolean isMarked)
	{
		this.isMarked = isMarked;
	}
	
	/**
	 * Gibt zurück, ob das SpaceObjectView markiert ist.
	 * @return true für markiert.
	 */
	public boolean isMarked()
	{
		return isMarked;
	}
	
	/**
	 * Aktualisiert die zwischengespeicherten Vektoren des SpaceObjectViews.
	 */
	public void updateVectors()
	{
		tempPosition = refersTo.getPosition();
		tempDirection = refersTo.getDirection();
		tempUp = refersTo.getUp();
	}
}