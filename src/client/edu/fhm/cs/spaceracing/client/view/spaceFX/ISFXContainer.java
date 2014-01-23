package edu.fhm.cs.spaceracing.client.view.spaceFX;

import javax.media.opengl.GL;

/**
 * Inferface für SpaceObjectViews, die Spezialeffekte enthalten (Antriebsspuren, Blinklichter, etc).
 * @author Thomas "HUFFMAN" Brunner
 */
public interface ISFXContainer
{
	/**
	 * Zeichnet die Spezialeffekte des Objekts.
	 * @param gl das GL-Objekt.
	 */
	public void drawSFX(GL gl);
}
