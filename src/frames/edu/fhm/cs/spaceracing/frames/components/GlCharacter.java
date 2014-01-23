package edu.fhm.cs.spaceracing.frames.components;

import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import edu.fhm.cs.spaceracing.frames.utils.ImageResource;
import edu.fhm.cs.spaceracing.frames.utils.Texture;

/**
 * Grafikkomponente Character.<br> 
 * 
 * @author Jan Bouillon
 *
 */
public class GlCharacter extends GlComponent
{

	public GlCharacter(ImageResource resource)
	{
		super(resource, new Point(0,0));
	}
	
    /**
     *  Erzeugt ein leeres Character-Objekt
     */
    public GlCharacter()
	{
    	this(null);
	}
    
	/**
	 * @see edu.fhm.cs.spaceracing.frames.components.GlComponent#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
    public void display(GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getTexturePointer());
        
        super.display(drawable);
    }

	public void setTexture(Texture texture)
	{
		this.texture = texture;
		width = texture.getWidth();
		height = texture.getHeight();
	}
}
