package edu.fhm.cs.spaceracing.client.view.spaceFX;

import java.io.File;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.view.tools.GLTexture;

/**
 * Eine Starbox ist ein Würfel, in den verzerrte Texturen so gezeichnet werden,
 * dass es aussieht als würde man sich im Inneren einer Kuppel befinden (Sternenhimmel).
 * @author Thomas "HUFFMAN" Brunner
 */
public class StarBox
{
	private StarField starfield;
	
	private static GLTexture[] textures = null;
	private static final String MODEL_DIR = "models/starbox";
	
	// Alle Texturkoordinaten werden um einen Pixel verschoben, damit man keine Kanten in der Skybox sieht
	private static float[] texturePixelOffset = null;
	
	
	public StarBox()
	{
		starfield = new StarField(10000);
	}
	
	/**
	 * Zeichnet die StarBox.
	 * @param gl das GL-Objekt.
	 */
	public void draw(GL gl)
	{
		if(textures == null)
			preLoad(gl);
		
		gl.glPushMatrix();
		gl.glScalef(240f, 240f, 240f);
		gl.glDisable(GL.GL_LIGHTING);						// Nicht den Sternenhimmel beleuchten
        gl.glColor4f(.9f, .9f, 1, 0.8f);
        

		// Vorne
        textures[0].bind(gl);
        float pOff = texturePixelOffset[0];
        gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(1-pOff, pOff); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
		gl.glTexCoord2f(1-pOff, 1-pOff); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
		gl.glTexCoord2f(pOff, 1-pOff); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
		gl.glTexCoord2f(pOff, pOff); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
		gl.glEnd();
		
		// Hinten
		textures[1].bind(gl);
        pOff = texturePixelOffset[1];
        gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(pOff, pOff); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1-pOff, pOff); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1-pOff, 1-pOff); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
		gl.glTexCoord2f(pOff, 1-pOff); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
		gl.glEnd();
		
		// Oben
		textures[2].bind(gl);
        pOff = texturePixelOffset[2];
        gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(1-pOff, 1-pOff); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
		gl.glTexCoord2f(pOff, 1-pOff); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
		gl.glTexCoord2f(pOff, pOff); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
		gl.glTexCoord2f(1-pOff, pOff); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
		gl.glEnd();
		
		// Unten
		textures[3].bind(gl);
        pOff = texturePixelOffset[3];
        gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(1-pOff, pOff); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1-pOff, 1-pOff); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
		gl.glTexCoord2f(pOff, 1-pOff); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
		gl.glTexCoord2f(pOff, pOff); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
		gl.glEnd();
		
		// Rechts
		textures[4].bind(gl);
        pOff = texturePixelOffset[4];
        gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(pOff, pOff); gl.glVertex3f( 1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1-pOff, pOff); gl.glVertex3f( 1.0f, -1.0f,  1.0f);
		gl.glTexCoord2f(1-pOff, 1-pOff); gl.glVertex3f( 1.0f,  1.0f,  1.0f);
		gl.glTexCoord2f(pOff, 1-pOff); gl.glVertex3f( 1.0f,  1.0f, -1.0f);
		gl.glEnd();
		
		// Links
		textures[5].bind(gl);
        pOff = texturePixelOffset[5];
        gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(1-pOff, pOff); gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1-pOff, 1-pOff); gl.glVertex3f(-1.0f,  1.0f, -1.0f);
		gl.glTexCoord2f(pOff, 1-pOff); gl.glVertex3f(-1.0f,  1.0f,  1.0f);
		gl.glTexCoord2f(pOff, pOff); gl.glVertex3f(-1.0f, -1.0f,  1.0f);
		gl.glEnd();
		gl.glEnable(GL.GL_LIGHTING);
		gl.glColor4f(1, 1, 1, 1);
		
		gl.glPopMatrix();
		gl.glPushMatrix();
		starfield.draw(gl);					// Sterne zeichnen
		gl.glPopMatrix();
	}
	
	/**
	 * Lädt die Texturen der StarBox.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{		
		textures = new GLTexture[6];
		// Jede Textur wird um einen Pixel vergrößert, damit die Kanten unsichtbar werden
		texturePixelOffset = new float[6];
        
		
        for(int i = 0; i < 6; i++)
        {       
	       	textures[i] = new GLTexture(MODEL_DIR + File.separator + "b_"+i+".jpg", true);
	       	textures[i].preLoad(gl);
	        texturePixelOffset[i] = 1.0f / textures[i].getWidth();
        }
	}
}
