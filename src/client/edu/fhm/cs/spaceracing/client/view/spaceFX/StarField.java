package edu.fhm.cs.spaceracing.client.view.spaceFX;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

import edu.fhm.cs.spaceracing.client.view.tools.GLTexture;
import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Die Sterne in einer Starbox.
 * @author Thomas "HUFFMAN" Brunner
 */
public class StarField
{
	private float starScale = 0.1f;
	private ArrayList<StarFieldStar> stars;
	
	private static float maxPointSize = -1;
	private static int vboBuffer = -1;
    private static GLTexture texture = null;
    
	
	/**
	 * Erzeugt ein neues StarField.
	 * @param numStars die Anzahl der Sterne.
	 */
	public StarField(int numStars)
	{
		Random rnd = new Random();
		stars = new ArrayList<StarFieldStar>();
		for(int i = 0; i < numStars; i++)
		{
			// Zufällige Richtung
			Vector position  = new Vector(1.0 - 2 * rnd.nextDouble(),
										  1.0 - 2 * rnd.nextDouble(),
										  1.0 - 2 * rnd.nextDouble());
			position.normalize();			// Abstand 1.0 vom Ursprung
			float scale = rnd.nextFloat();
			float alpha = rnd.nextFloat();
			stars.add(new StarFieldStar(position, scale, alpha*alpha));
		}
	}
	
	/**
	 * Zeichnet das StarField.
	 * @param gl das GL-Objekt.
	 */
	public void draw(GL gl)
	{
		// Texturen initialisieren
		if(texture == null)
			preLoad(gl);
		
		if(vboBuffer == -1)
			buildVBO(gl);

		gl.glScalef(240f, 240f, 240f);
		
        gl.glEnable(GL.GL_BLEND);                                  
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        gl.glDisable(GL.GL_DEPTH_TEST); 			        // Immer im Vordergrund
        gl.glDisable(GL.GL_LIGHTING);						// Die Sonne wird nicht beleuchtet
		texture.bind(gl);
		
		gl.glPointSize(maxPointSize * starScale);

	 	gl.glPointParameterfARB(GL.GL_POINT_FADE_THRESHOLD_SIZE_ARB, 60.0f);
		gl.glPointParameterfARB(GL.GL_POINT_SIZE_MIN_ARB, 0);
		gl.glPointParameterfARB(GL.GL_POINT_SIZE_MAX_ARB, maxPointSize);
		gl.glTexEnvf(GL.GL_POINT_SPRITE_ARB, GL.GL_COORD_REPLACE_ARB, GL.GL_TRUE);
		gl.glEnable(GL.GL_POINT_SPRITE_ARB);

		// Das Mesh aus dem VBO zeichnen.
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vboBuffer);
		gl.glInterleavedArrays(GL.GL_C4F_N3F_V3F, 0, 0);
		
        gl.glDrawArrays(GL.GL_POINTS, 0, stars.size() * 3);  

		gl.glDisable( GL.GL_POINT_SPRITE_ARB );

		
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);		// Farbe zurücksetzen
        gl.glDisable(GL.GL_BLEND);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_LIGHTING);

    }
	
	private void buildVBO(GL gl)
	{
		// Ein Sprite besteht aus (4 Color + 3 Normal + 3 Vertex) * (x+y+z) floats
		final int SIZEOF_BUFFERED_SPRITE = 10 * 3 * BufferUtil.SIZEOF_FLOAT;
		
		boolean functionsOK = 
			gl.isFunctionAvailable("glGenBuffersARB") &&
			gl.isFunctionAvailable("glBindBufferARB") &&
			gl.isFunctionAvailable("glBufferDataARB") &&
			gl.isFunctionAvailable("glDeleteBuffersARB");
		if(!functionsOK)
			throw new RuntimeException("Your graphics adapter does not support Vertex Buffer objects!");
		
		FloatBuffer vertexBuffer = BufferUtil.newFloatBuffer(stars.size() * 10 * 3);		

		for(StarFieldStar star : stars)
			star.writeToBuffer(vertexBuffer);	
		vertexBuffer.rewind();
		
		// Generiere und binde den Vertex Buffer  
		int[] tmpBuffer = new int[1];
		gl.glGenBuffersARB(1, tmpBuffer, 0);
		gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, tmpBuffer[0]);
		gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, stars.size() * SIZEOF_BUFFERED_SPRITE, vertexBuffer, GL.GL_STATIC_DRAW_ARB);
		vboBuffer = tmpBuffer[0];
		
		// Punktgröße herausfinden
		float[] maxSize = new float[1];
		gl.glGetFloatv(GL.GL_POINT_SIZE_MAX_ARB, maxSize, 0);
		if(maxSize[0] > 100.0f)
			maxSize[0] = 100.0f;
		maxPointSize = maxSize[0];
	}
	
	
    
	/**
	 * Lädt die Texturen des LensFlares.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		texture = new GLTexture("models/starfield/star.bmp", false);	
		texture.preLoad(gl);
	}
}