package edu.fhm.cs.spaceracing.client.view.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import edu.fhm.cs.spaceracing.client.view.GraphicsSettings;
import edu.fhm.cs.spaceracing.client.view.textureloader.Texture;
import edu.fhm.cs.spaceracing.client.view.textureloader.TextureReader;
import edu.fhm.cs.spaceracing.model.generic.Matrix;
import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Eine kleine Sammlung an Funktionen, die oft zum Rendern benutzt werden.
 * @author Thomas "HUFFMAN" Brunner
 */
public class GLTools
{
	/**
	 * Erstellt eine Drehmatrix, sodass ein zu zeichnendes Objekt in die richtige Richtung zeigt. 
	 * @param gl das GL-Objekt.
	 * @param dir der Richtungsvektor.
	 * @param up eine Normale, die auf dem Richtungsvektor steht (oben).
	 */
	public static void doRotation(GL gl, Vector dir, Vector up)
	{	
		double[] rotationMatrix = new double[16];
		
		// Richtungs- und Normalenvektor
		dir = dir.normalize();
		up = up.normalize();
		Vector right = dir.crossProduct(up);
		
		// Neue X-Achse (Richtungsvektor)
		rotationMatrix[0]  = dir.getX();
		rotationMatrix[1]  = dir.getY();
		rotationMatrix[2]  = dir.getZ();
		rotationMatrix[3]  = 0;
		
		// Neue Y-Achse (Normalenvektor)
		rotationMatrix[4]  = up.getX();
		rotationMatrix[5]  = up.getY();
		rotationMatrix[6]  = up.getZ();
		rotationMatrix[7]  = 0;
				
		// Neue Z-Achse (erzeugt)
		rotationMatrix[8]  = right.getX();
		rotationMatrix[9]  = right.getY();
		rotationMatrix[10] = right.getZ();
		rotationMatrix[11] = 0;
		
		// Die W-Achse bleibt unverändert
		rotationMatrix[12] = 0;
		rotationMatrix[13] = 0;
		rotationMatrix[14] = 0;
		rotationMatrix[15] = 1;
		
		// In die aktuelle OpenGL-Matrix hineinmultiplizieren 
		gl.glMultMatrixd(rotationMatrix, 0);
	}
	
	/**
	 * Erstellt eine inverse Drehmatrix, die das Objekt in die entgegengesetzte Richtung dreht.
	 * FIXME: Irgendwie dreht sich's noch wo anders hin.
	 * @param gl das GL-Objekt.
	 * @param dir der ursprüngliche Richtungsvektor
	 * @param up die ursprüngliche Normale.
	 */
	public static void doInvertedRotation(GL gl, Vector dir, Vector up)
	{
		// Richtungs- und Normalenvektor
		dir = dir.normalize();
		up = up.normalize();
		Vector right = dir.crossProduct(up);
		
		// Inverse Drehmatrix berechnen
		Matrix m = new Matrix(dir, up, right);
		Matrix inverse = m.invertedMatrix();
		
		Vector newDir = inverse.getRowVector(0);
		Vector newUp = inverse.getRowVector(1);
		
		doRotation(gl, newDir, newUp);
	}
	
	/**
	 * Macht alle Drehungen in der Modelview-Matrix rückgängig
	 * @param gl das GL-Objekt.
	 */
	public static void resetRotation(GL gl)
	{
		float modelview[] = new float[16];
		gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX , modelview, 0);
		
		// Die ersten drei Zeilen und Spalten mit der Einheitsmatrix ersetzen
		for(int i = 0; i < 3; i++) 
		{
			for(int j = 0; j < 3; j++)
			{
				if (i == j)
					modelview[i * 4 + j] = 1.0f;
				else
					modelview[i * 4 + j] = 0.0f;
			}
		}
		gl.glLoadMatrixf(modelview, 0);	
	}
	
	
	/**
	 * Erzeugt eine Textur mit Mipmaps im Grafikspeicher.
	 * @param gl das GL-Objekt.
	 * @param path der Dateiname und -pfad zur Texturdatei.
	 * @return die Texturnummer der erzeugten Textur.
	 */
	public static int loadSimpleTexture(GL gl, String path)
	{
		int[] textures = new int[1];
	    gl.glGenTextures(1, textures, 0)  ; 	
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
        Texture texture = null;
        
        
        // Datei laden
        try 
        {         
           	texture = TextureReader.readTexture(path);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        // Lineare Filterung -> schnell!
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, texture.getWidth(), texture.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.getPixels());
        
        // Farben in die Textur miteinrechnen
        gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        
        // Anisotrope Filterung abschalten
        if(gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic"))   
                gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, 0);
       
        return textures[0];
	}
	
	/**
	 * Erzeugt eine Textur ohne Mipmaps und sonstwas.
	 * @param gl das GL-Objekt.
	 * @param path der Dateiname und -pfad zur Texturdatei.
	 * @return die Texturnummer der erzeugten Textur.
	 */
	public static int loadTexture(GL gl, String path)
	{
		GLU glu = new GLU();
	
		int[] textures = new int[1];
	    gl.glGenTextures(1, textures, 0)  ; 	
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
        Texture texture = null;
        
        
        // Datei laden
        try 
        {         
           	texture = TextureReader.readTexture(path);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        // Mipmaps erzeugen 
        glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGB8, texture.getWidth(), texture.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.getPixels());
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        
        // Farben in die Textur miteinrechnen
        gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        
        // Anisotrope Filterung einschalten
        if(GraphicsSettings.getAnisotropicLevel() != 0 &&
           gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic"))   
        {
        	if(GraphicsSettings.getAnisotropicLevel() == -1)
        	{
                float max[] = new float[1];
                gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0 );
                gl.glTexParameterf(GL.GL_TEXTURE_2D, 
                                   GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, 
                                   max[0]);
        	}
        	else
                gl.glTexParameterf(GL.GL_TEXTURE_2D, 
                        GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, 
                        GraphicsSettings.getAnisotropicLevel());
        }
        
        return textures[0];
	}
	
	/**
	 * Liest und erzeugt einen Shader.
	 * @param gl das GL-Objekt.
	 * @param shadertype die Art des Shaders (nur GL.GL_VERTEX_SHADER oder GL.GL_FRAGMENT_SHADER)
	 * @param filename der Dateiname des GLSL-Shaderprogramms.
	 */
	public static int generateShader(GL gl, int shadertype, String filename)
	{
		if(shadertype != GL.GL_VERTEX_SHADER && shadertype != GL.GL_FRAGMENT_SHADER)
			throw new IllegalArgumentException("Can only create fragment and vertex shaders!");
		
		// Datei einlesen
		String[] shaderStrings;
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			LinkedList<String> strings = new LinkedList<String>();

			String temp;
			while((temp = br.readLine()) != null)
				strings.add(temp);
			br.close();

			shaderStrings = strings.toArray(new String[strings.size()]);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}


		int shader = gl.glCreateShader(shadertype);
		gl.glShaderSource(shader, shaderStrings.length, shaderStrings, null);
		gl.glCompileShader(shader);
		
		//TODO: Shader-Info-Log auswerten
	
		return shader;
	}


}