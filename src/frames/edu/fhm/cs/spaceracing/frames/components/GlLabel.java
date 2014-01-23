package edu.fhm.cs.spaceracing.frames.components;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import edu.fhm.cs.spaceracing.frames.utils.GlFont;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;
import edu.fhm.cs.spaceracing.frames.utils.Texture;

/**
 * Die Grafikkomponente Label.<br>
 * 
 * Sie besitzt eine Textur und hat die Möglichkeit, Text darzustellen.
 * 
 * @author Jan Bouillon
 *
 */
public class GlLabel extends GlComponent
{

    private String text;
    
    /**
     * Der Text als eine Liste aus Grafikkomponenten des Typs {@link GlCharacter}
     */
    private ArrayList<GlCharacter> textAsCharacters;
    
    /**
     * Die Anfangsposition des Textes 
     */
    private Point textAnchor;
    
	/**
	 * Das Positions-Offset für den Text.
	 */
	private int textOffset;
	
	/**
	 * Der Abstand zwischen zwei Buchstaben
	 */
	private int vGap;

	/**
	 * Erzeugt ein Label mit einer bestimmten Textur, der Position im Fenster, einem darzustellenden Text und der Position des Textes im Label.
	 * 
	 * @param resource Die Textur
	 * @param point Die Position im Fenster
	 * @param text Der darzustellende Text
	 * @param textOffset Die Anfangsposition des Textes
	 */
	public GlLabel(ImageResource resource, Point point, String text, int textOffset)
    {
        super(resource, point);
		this.text = text.toUpperCase();
		
		setTextOffset(textOffset);
		vGap = 2;
    }

	/**
	 * Instanziiert ein Label mit einer bestimmten Textur und einem darzustellenden Text.<br>
	 * Die Position im Fenster ist (0,0)  und die Position des Textes im Label ist zentriert und 18 Pixel vom linken Rand entfernt. 
	 * 
	 * @param resource Die Textur
	 * @param text Der darzustellende Text
	 */
	public GlLabel(ImageResource resource, String text)
	{
    	this(resource, new Point(0,0), text, -1);
	}

    /**
     * Instanziiert ein Label mit einer bestimmten Textur. <br>
     * Der Text ist leer und die Position im Fenster ist (0,0). 
     * 
     * @param resource
     */
    public GlLabel(ImageResource resource)
	{
    	this(resource, "");
	}

    // TODO_JAN Maximale Darstellung des Textes festlegen, damit er nicht außerhalb des Labels angezeigt wird.
	/**
	 * Erzeugt aus der Zeichenkette eine Liste von Grafikkomponenten des Typs {@link GlCharacter}.
	 */
	private void defineText()
	{
    	char[] characters = text.toCharArray();
    	textAsCharacters = new ArrayList<GlCharacter>(characters.length);
    	
    	GlFont glFont = GlFont.getInstance();
    	
		textOffset += anchor.x;
		vGap = 2;
		
		textAnchor = new Point(textOffset, anchor.y + (height/vGap - glFont.getCharacterHeight()/vGap));
    	for (char character : characters)
		{
			GlCharacter glCharacter = new GlCharacter();
			Texture characterTexture = glFont.getCharacterTexture(character);
			
			if (characterTexture == null)
			{
				// Quasi-Leerzeichen
				textOffset += 25;
			}
			else 
			{
				glCharacter.setTexture(characterTexture);
				
				// Anker setzen
				glCharacter.setAnchor(new Point(textOffset, textAnchor.y));
				textOffset += glCharacter.width + vGap;
				
				textAsCharacters.add(glCharacter);
			}
		}
	}

	/**
	 * Zeichnen des Labels und des Textes
	 * 
	 * @see edu.fhm.cs.spaceracing.frames.components.GlComponent#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
    public void display(GLAutoDrawable drawable)
    {
        GL gl = drawable.getGL();
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getTexturePointer());
        
        super.display(drawable);
        
        if (textAsCharacters == null)
        {
        	defineText();
        }
        for (GlCharacter character : textAsCharacters)
        {
        	character.display(drawable);
        }
    }

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
		setTextOffset(-1);
		defineText();
	}
	
	public void setText(String text, int offset)
	{
		this.text = text;
		setTextOffset(offset);
		defineText();
	}
	
	/**
	 * Setzt das Positions-Offset für den Text (ausgehend von der x-Koordinate der Position des Labels.)
	 * Wenn das Textoffset negativ ist, wird es auf 18 Pixel eingestellt (default-Wert)
	 * 
	 * @param textOffset Positions-Offset für den Text
	 */
	public void setTextOffset(int textOffset)
	{
		if (textOffset < 0)
		{
			this.textOffset = 18;
		}
		else 
		{
			this.textOffset = textOffset;
		}
	}

	@Override
	public boolean isVisible()
	{
		return super.isVisible() || text.length() > 0;
	}

	public void append(char character)
	{
		StringBuffer currentText = new StringBuffer(text);
		currentText.append(character);
		
		setText(currentText.toString());
	}
}
