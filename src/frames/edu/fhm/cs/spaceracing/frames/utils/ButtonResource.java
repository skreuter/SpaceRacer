package edu.fhm.cs.spaceracing.frames.utils;

/**
 * Spezielle Ressource für die Buttons. Kann zwei Texturen aufnehmen.
 * 
 * @author Jan Bouillon
 *
 */
public class ButtonResource extends Resource
{

    private Texture highlightedTexture;

    public ButtonResource(Texture texture, Texture highlightedTexture)
    {
        super(texture);
        
        this.highlightedTexture = highlightedTexture;
    }

    public Texture getHighlightedTexture()
    {
        return highlightedTexture;
    }
}
