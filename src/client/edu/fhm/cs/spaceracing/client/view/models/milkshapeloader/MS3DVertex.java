package edu.fhm.cs.spaceracing.client.view.models.milkshapeloader;

import java.io.DataInput;
import java.io.IOException;

import edu.fhm.cs.spaceracing.client.view.models.Vertex;

/**
 * Ein MilkShape3D Vertex 
 * @author HUFFMAN
 */
class MS3DVertex extends Vertex
{
    public static final int SIZE = 15;					 // Größe in Bytes

    private int flags;                                   // Im SpaceRacer unbenutzt
    private int referenceCount;							 // Im SpaceRacer unbenutzt


    MS3DVertex(DataInput input) throws IOException
    {
        flags = input.readUnsignedByte();
        location = new float[3];
        location[0] = input.readFloat();
        location[1] = input.readFloat();
        location[2] = input.readFloat();
        referenceCount = input.readUnsignedByte();
        boneID = input.readUnsignedByte();
    }

	int getFlags()
	{
		return flags;
	}

	int getReferenceCount()
	{
		return referenceCount;
	}
	
    /**
     * @see Object#toString()
     */
    @Override
	public String toString()
    {
        return "Vertex["+location[0]+","+location[1]+","+location[2]+"]";
    }
}
