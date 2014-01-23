package edu.fhm.cs.spaceracing.client.view.models.milkshapeloader;

import java.io.DataInput;
import java.io.IOException;

import edu.fhm.cs.spaceracing.client.view.models.Material;

/**
 * Ein MilkShape3D Material.
 * @author Thomas "HUFFMAN" Brunner
 */
class MS3DMaterial extends Material
{
	private String name;
	private float transparency;                          // im SpaceRacer unbenutzt (0.0f - 1.0f)
	private int mode;                                    // im SpaceRacer unbenutzt (0, 1, 2)
	private String alphamapName;                         // im SpaceRacer unbenutzt                            

    MS3DMaterial(DataInput input) throws IOException
    {    	
        name = MS3DDecoder.decodeZeroTerminatedString(input, 32);
        ambient = new float[4];
        ambient[0] = input.readFloat();
        ambient[1] = input.readFloat();
        ambient[2] = input.readFloat();
        ambient[3] = input.readFloat();
        diffuse = new float[4];
        diffuse[0] = input.readFloat();
        diffuse[1] = input.readFloat();
        diffuse[2] = input.readFloat();
        diffuse[3] = input.readFloat();
        specular = new float[4];
        specular[0] = input.readFloat();
        specular[1] = input.readFloat();
        specular[2] = input.readFloat();
        specular[3] = input.readFloat();
        emissive = new float[4];
        emissive[0] = input.readFloat();
        emissive[1] = input.readFloat();
        emissive[2] = input.readFloat();
        emissive[3] = input.readFloat();
        shininess = input.readFloat();
        transparency = input.readFloat();
        mode = input.readUnsignedByte();
        texture = -1;
        textureFilename = MS3DDecoder.decodeZeroTerminatedString(input, 128).replace('\\', '/');
        alphamapName = MS3DDecoder.decodeZeroTerminatedString(input, 128).replace('\\', '/');
       
    }

	String getAlphamapName()
	{
		return alphamapName;
	}

	int getMode()
	{
		return mode;
	}

	String getName()
	{
		return name;
	}

	float getTransparency()
	{
		return transparency;
	}
    
}
