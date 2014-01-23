package edu.fhm.cs.spaceracing.frames.components;

import java.awt.Dimension;
import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import edu.fhm.cs.spaceracing.frames.utils.ImageResource;
import edu.fhm.cs.spaceracing.frames.utils.Texture;

/**
 * Die abstrakte Klasse für die Grafikkomponenten.<br>
 * 
 * Sie hält folgenden Daten:<br>
 * <ul>
 * 	<li> Position (x,y) (Pixel) im Fenster
 * 	<li> Größe der Komponente (in Abhängigkeit der Größe der Textur) in Pixel
 * 	<li> Die Textur(roh)daten
 * </ul><br>
 * Weiterhin ist diese Klasse für das Darstellen der jeweiligen Komponente im Fenster zuständig und überprüft,
 * ob die Position des Mauszeigers innerhalb der Komponente liegt. 
 * 
 * @author Jan Bouillon
 *
 */
public abstract class GlComponent
{
	
	/**
	 * Position im Fenster
	 */
	protected Point anchor;
	
	protected int width;
	protected int height;
    
    protected Texture texture;

	protected String name = this.getClass().getSimpleName();

	/**
	 * Wird von den erbenden Klassen aufgerufen, um die Textur und die Position zuzuweisen.
	 * 
	 * @param resource Die Textur
	 * @param anchor Die Position
	 */
	GlComponent(ImageResource resource, Point anchor)
	{
		this.anchor = anchor;
		
		if (resource != null)
		{
	        texture = resource.getResource().getTexture();
	        width = texture.getWidth();
	        height = texture.getHeight();
            
	        this.name = resource.toString();
		}
	}

	// TODO_JAN das binden der Textur hierher (was überlegen für GlComponents mit 2 oder mehr Texturen (für später) --> Zwischenschicht wäre nicht schlecht) 
	/**
	 * Zeichnen der Textur auf dem Bildschirm.
	 * 
	 * @param drawable Wird vom GLEventListener übergeben, um die Textur zu zeichnen
	 */
	public void display(GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();
        
        gl.glBegin(GL.GL_QUADS);
            //  1. Texturkoordinate (unten links)
            gl.glTexCoord2d(0.0, 0.0);
            gl.glVertex2d(anchor.x, anchor.y + height);
            
            // 2. Texturkoordinate (unten rechts)
            gl.glTexCoord2d(1.0, 0.0);
            gl.glVertex2d(anchor.x + width, anchor.y + height);
            
            // 3. Texturkoordinate (oben rechts) 
            gl.glTexCoord2d(1.0, 1.0);
            gl.glVertex2d(anchor.x + width, anchor.y);
            
            // 4. Texturkoordinate (oben links)
            gl.glTexCoord2d(0.0, 1.0);
            gl.glVertex2d(anchor.x, anchor.y);
       gl.glEnd();
    }
	
    /**
     * Überprüft, ob die Position in der Komponente liegt 
     * 
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @return Das Ergebnis. <code>true</code>, falls die Position innerhalb der Komponente liegt, ansonsten <code>false</code>
     */
    public boolean isTouched(int x, int y)
    {
		return (x > anchor.x && x < (anchor.x + width) && y > anchor.y && y < (anchor.y + height));
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
	
	public String getName()
	{
		return name;
	}

	public void setAnchor(Point anchor)
	{
		this.anchor = anchor;
	}

	public Point getAnchor()
	{
		return anchor;
	}
	
	public void setSize(Dimension dimension)
	{
		width = dimension.width;
		height = dimension.height;
	}

	public Texture getTexture()
	{
		return texture;
	}

	// TODO_JAN diese Methode in die Zwischenschicht verschieben, für selectable Komponenten 
    public boolean isHighlighted()
    {
        return false;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

	public String toString()
	{
		return getName() + " [Texture pointer = " + texture.getTexturePointer() + "; width=" + width + "; height=" + height + "; position = (" + anchor.x + ", " + anchor.y + ")]";
	}

	public boolean isVisible()
	{
		return texture != null;
	}
}
