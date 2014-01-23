package edu.fhm.cs.spaceracing.client.view.textureloader;

import java.nio.ByteBuffer;

/**
 * Enthält Abmessungen und Daten eines Bildes.
 * @author Thomas "HUFFMAN" Brunner
 */
public class Texture 
{
    private ByteBuffer pixels;
    private int width;
    private int height;

    /**
     * Erzeugt eine neue Textur.
     * @param pixels die Pixel des Bildes.
     * @param width die Breite des Bildes.
     * @param height die Höhe des Bildes.
     */
    Texture(ByteBuffer pixels, int width, int height) 
    {
        this.height = height;
        this.pixels = pixels;
        this.width = width;
    }

    /**
     * Gibt die Breite der Textur zurück.
     * @return die Breite der Textur.
     */
    public int getWidth() 
    {
        return width;
    }
    
    /**
     * Gibt die Höhe der Textur zurück.
     * @return die Höhe der Textur.
     */
    public int getHeight() 
    {
        return height;
    }

    /**
     * Gibt den Inhalt der Textur zurück.
     * @return die Pixel der Textur.
     */
    public ByteBuffer getPixels() 
    {
        return pixels;
    }
}
