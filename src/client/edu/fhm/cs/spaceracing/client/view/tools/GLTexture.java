package edu.fhm.cs.spaceracing.client.view.tools;

import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import edu.fhm.cs.spaceracing.client.view.GraphicsSettings;
import edu.fhm.cs.spaceracing.client.view.textureloader.Texture;
import edu.fhm.cs.spaceracing.client.view.textureloader.TextureReader;

/**
 * Eine Textur im SpaceRacer. Selbstgemacht, da Probleme mit der JOGL-Texture-Klasse auftreten.
 * TODO: loadTexture-Funktionalität aus GLTools übernehmen.
 * @author Thomas "HUFFMAN" Brunner
 */
public class GLTexture implements IPreloadable
{
	private String path;
	private boolean mipmaps;
	private int glTarget;
	private boolean isLoaded = false;
	
	private int width;
	private int height;
	
	/**
	 * Erzeugt eine neue Textur. Achtung: Die Textur wird noch nicht geladen!
	 * @param path der Pfad zur Textur-Datei.
	 * @param mipmaps gibt an, ob die Textur mit MipMaps erstellt werden soll.
	 */
	public GLTexture(String path, boolean mipmaps)
	{
		this.path = path;
		this.mipmaps = mipmaps;
	}
	
	/**
	 * Lädt die Textur in den Grafikspeicher und erzeugt ggf. die MipMaps.
	 * @param gl das GL-Objekt.
	 */
	public void preLoad(GL gl)
	{
		GLU glu = new GLU();
		
		int[] textures = new int[1];
	    gl.glGenTextures(1, textures, 0); 	
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
        
        if(mipmaps)
        {
	        // Mipmaps erzeugen
	        glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGB8, texture.getWidth(), texture.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.getPixels());
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        }
        else
        {
            // Lineare Filterung -> schnell!
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, texture.getWidth(), texture.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.getPixels());
        }
        
        // Farben in die Textur miteinrechnen
        gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        
        
        if(mipmaps)
        {
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
        }
        else
            if(gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic"))   
                gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, 0);

        
        width = texture.getWidth();
        height = texture.getHeight();
        glTarget = textures[0];		
		isLoaded = true;
	}
	
	/**
	 * Gibt zurück, ob die Textur bereits in den Grafikspeicher geladen wurde.
	 * @return true für ja.
	 */
	public boolean isLoaded()
	{
		return isLoaded;
	}
	
	/**
	 * Bindet die Textur an den GL-Kontext.
	 * @param gl das GL-Objekt.
	 */
	public void bind(GL gl)
	{
		if(!isLoaded)
			preLoad(gl);
		gl.glBindTexture(GL.GL_TEXTURE_2D, glTarget);
	}
	
	/**
	 * Setzt einen Texturparameter.
	 * @param gl das GL-Objekt.
	 * @param parameter der Name des Parameters (eine symbolische Konstante von GL).
	 * @param value der Wert des Parameters.
	 */
	public void setParameter(GL gl, int parameter, int value)
	{
		if(!isLoaded)
			preLoad(gl);
		bind(gl);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, parameter, value);
	}
	
	/**
	 * Gibt die OpenGL-Texturnummer zurück.
	 * @return die OpenGL-Texturnummer.
	 */
	public int getGLTarget()
	{
		if(!isLoaded)
			throw new NullPointerException("Texture isn't loaded!");
		return glTarget;
	}
	
	public int getWidth()
	{
		if(!isLoaded)
			throw new NullPointerException("Texture isn't loaded!");
		return width;
	}
	
	public int getHeight()
	{
		if(!isLoaded)
			throw new NullPointerException("Texture isn't loaded!");
		return height;
	}
}
