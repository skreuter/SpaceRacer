package edu.fhm.cs.spaceracing.client.view.models;

/**
 * Ein Vertex wie es in einem 3D-Model verwendet wird.
 * @author Thomas "HUFFMAN" Brunner
 */
public abstract class Vertex
{
	protected int boneID;				// Im SpaceRacer unbenutzt (Skelettanimation)
	protected float location[];

	int getBoneID()
	{
		return boneID;
	}

	float[] getLocation()
	{
		return location;
	}
	
	
}
