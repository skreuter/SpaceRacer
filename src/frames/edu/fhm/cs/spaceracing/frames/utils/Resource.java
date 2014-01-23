package edu.fhm.cs.spaceracing.frames.utils;

/**
 * Die Basisklasse für Ressourcen, also Texturen.
 * 
 * @author Jan Bouillon
 *
 */
public class Resource
{
    
    protected Texture texture;

    public Resource(Texture texture)
    {
        this.texture = texture;
        
    }
    
    public Texture getTexture()
    {
        return texture;
    }

}
