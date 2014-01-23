package edu.fhm.cs.spaceracing.client.view.models.milkshapeloader;

import java.io.DataInput;
import java.io.IOException;

import edu.fhm.cs.spaceracing.client.view.models.Triangle;

/**
 * Ein MilkShape3D Dreieck
 * @author Thomas "HUFFMAN" Brunner
 */
class MS3DTriangle extends Triangle
{
    private int flags;                                   // im SpaceRacer unbenutzt
    private int smoothingGroup;                          // im SpaceRacer unbenutzt (1 - 32)
    private int groupIndex;

    MS3DTriangle(DataInput input) throws IOException
    {    	
        flags = input.readUnsignedShort();
        vertexIndices = new int[3];
        vertexIndices[0] = input.readUnsignedShort();
        vertexIndices[1] = input.readUnsignedShort();
        vertexIndices[2] = input.readUnsignedShort();
        vertexNormals = new float[3][3];
        vertexNormals[0][0] = input.readFloat();
        vertexNormals[0][1] = input.readFloat();
        vertexNormals[0][2] = input.readFloat();
        vertexNormals[1][0] = input.readFloat();
        vertexNormals[1][1] = input.readFloat();
        vertexNormals[1][2] = input.readFloat();
        vertexNormals[2][0] = input.readFloat();
        vertexNormals[2][1] = input.readFloat();
        vertexNormals[2][2] = input.readFloat();
        texCoordS = new float[3];
        texCoordS[0] = input.readFloat();
        texCoordS[1] = input.readFloat();
        texCoordS[2] = input.readFloat();
        texCoordT = new float[3];
        texCoordT[0] = 1.0f - input.readFloat();
        texCoordT[1] = 1.0f - input.readFloat();
        texCoordT[2] = 1.0f - input.readFloat();
        smoothingGroup = input.readUnsignedByte();
        groupIndex = input.readUnsignedByte();
    	
    }

	int getFlags()
	{
		return flags;
	}

	int getGroupIndex()
	{
		return groupIndex;
	}

	int getSmoothingGroup()
	{
		return smoothingGroup;
	}
}