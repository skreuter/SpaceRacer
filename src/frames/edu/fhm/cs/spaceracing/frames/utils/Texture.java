package edu.fhm.cs.spaceracing.frames.utils;

import java.nio.ByteBuffer;

/**
 * Wrapperklasse für ein aufbereitetes Image.<br>
 * Sie hält sich nur noch die 
 * 
 * @author Jan Bouillon
 *
 */
public class Texture
{

    private ByteBuffer byteBuffer;
    private int width;
    private int height;
    
    private String name;
	private int texturePointer = -1;

    public Texture(ByteBuffer byteBuffer, int width, int height)
    {
        this.byteBuffer = byteBuffer;
        this.width = width;
        this.height = height;
        
        name = byteBuffer.capacity() + "B x " + width + " x " + height + " px";
    }

	public ByteBuffer getByteBuffer()
    {
        return byteBuffer;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String identifier)
    {
        name = identifier;
    }

    @Override
    public String toString()
    {
        return name;
    }

	public void setTexturePointer(int texturePointer)
	{
		this.texturePointer = texturePointer;
	}

	public int getTexturePointer()
	{
		return texturePointer;
	}
}
