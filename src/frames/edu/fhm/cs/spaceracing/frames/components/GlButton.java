package edu.fhm.cs.spaceracing.frames.components;

import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import edu.fhm.cs.spaceracing.frames.utils.ButtonResource;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;
import edu.fhm.cs.spaceracing.frames.utils.Texture;


/**
 * Grafikkomponente Button. <br>
 * 
 * Kann eine zusätzliche Textur aufnehmen, um sie anzuzeigen, wenn er vom Mauszeiger berührt wird.
 * 
 * @author Jan Bouillon
 *
 */
public class GlButton extends GlComponent
{
	
	private Texture textureOfSelectedButton;
    private boolean isSelected;

	/**
	 * Erzeugt ein neues Button-Objekt mit einer Texturressource und einer Position (x,y) (in Pixeln) im Fenster.   
	 * 
	 * @param resource Die Textur
	 * @param anchor Position im Fenster
	 */
	public GlButton(ImageResource resource, Point anchor)
    {
        super(resource, anchor);
        
        textureOfSelectedButton = ((ButtonResource) resource.getResource()).getHighlightedTexture();
    }
	
	public GlButton(ImageResource resource)
	{
		this(resource, new Point(0,0));
	}

    /**
     * @see edu.fhm.cs.spaceracing.frames.components.GlComponent#display(javax.media.opengl.GLAutoDrawable)
     */
    @Override
	public void display(GLAutoDrawable drawable)
	{
        GL gl = drawable.getGL();
        if (textureOfSelectedButton != null)
        {
        	gl.glBindTexture(GL.GL_TEXTURE_2D, isSelected ? textureOfSelectedButton.getTexturePointer() : texture.getTexturePointer());
        }
        else
        {
        	gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getTexturePointer());
        }
        
        super.display(drawable);
	}
	
    /**
     * @see edu.fhm.cs.spaceracing.frames.components.GlComponent#isTouched(int, int)
     */
    @Override
	public boolean isTouched(int x, int y)
    {
		setSelected(super.isTouched(x, y));
		return isSelected;
    }
    
    public void setSelected(boolean isSelected)
    {
    	this.isSelected = isSelected;
    }

	public Texture getTextureOfSelectedButton()
	{
		return textureOfSelectedButton;
	}

    /**
     * @see edu.fhm.cs.spaceracing.frames.components.GlComponent#isHighlighted()
     */
	@Override
    public boolean isHighlighted()
    {
        return isSelected;
    }
}
