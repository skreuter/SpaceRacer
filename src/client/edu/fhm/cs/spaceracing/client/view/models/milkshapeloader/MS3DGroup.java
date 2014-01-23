package edu.fhm.cs.spaceracing.client.view.models.milkshapeloader;

import java.io.DataInput;
import java.io.IOException;

import edu.fhm.cs.spaceracing.client.view.models.Mesh;

/**
 * Ein MilkShape3D Mesh (auch Group genannt).
 * @author Thomas "HUFFMAN" Brunner
 */
class MS3DGroup extends Mesh
{
    private int flags;                                   // MilkShape-Flags: nicht benutzt
    private String name;                                 

    // 
    MS3DGroup(DataInput input) throws IOException
    {   	
        flags = input.readUnsignedByte();
        name = MS3DDecoder.decodeZeroTerminatedString(input, 32);
        numTriangles = input.readUnsignedShort();
        triangleIndices = new int[numTriangles];
        
        for(int t = 0; t < numTriangles; t++)
            triangleIndices[t] = input.readUnsignedShort();
        
        materialIndex = input.readUnsignedByte();
    }

	public int getFlags()
	{
		return flags;
	}

	public String getName()
	{
		return name;
	}
}