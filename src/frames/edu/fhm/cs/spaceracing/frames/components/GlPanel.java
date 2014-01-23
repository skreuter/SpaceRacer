package edu.fhm.cs.spaceracing.frames.components;

import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import edu.fhm.cs.spaceracing.frames.utils.ImageResource;

/**
 * Eine Spezialform von {@link GlContainer}
 * 
 * @author Jan Bouillon
 *
 */
public class GlPanel extends GlContainer
{
	
	/**
	 * @see edu.fhm.cs.spaceracing.frames.components.GlContainer#GlContainer(ImageResource, Point)
	 */
	public GlPanel(ImageResource resource, Point anchor)
	{
		super(resource, anchor);
	}

	/**
	 * Erzeugt ein nicht sichtbares Panel an der Position (0,0) im Fenster
	 */
	public GlPanel()
	{
		this(null, new Point(0,0));
	}

    public GlPanel(ImageResource resource)
    {
        this(resource, new Point(0,0));
    }

    public GlPanel(Point anchor)
    {
        this(null, anchor);
    }
    
    // TODO_JAN paintable GlPanels (vllt. ja GlFrame) definieren
    
    /**
     * @see edu.fhm.cs.spaceracing.frames.components.GlContainer#display(javax.media.opengl.GLAutoDrawable)
     */
    @Override
    public void display(GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getTexturePointer());
        
        super.display(drawable);
    }
}
