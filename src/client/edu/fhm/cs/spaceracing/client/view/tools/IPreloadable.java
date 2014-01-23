package edu.fhm.cs.spaceracing.client.view.tools;

import javax.media.opengl.GL;

/**
 * View-Objekte, die einen Preloader beinhalten.
 * @author Thomas "HUFFMAN" Brunner
 */
public interface IPreloadable
{
	/**
	 * Lädt das View-Objekt.
	 * @param gl das GL-Objekt.
	 */
	public void preLoad(GL gl);
}
