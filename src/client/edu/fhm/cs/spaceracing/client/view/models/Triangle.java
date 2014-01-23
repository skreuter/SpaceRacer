package edu.fhm.cs.spaceracing.client.view.models;

/**
 * Ein Triangle wie es in einem 3D-Model verwendet wird.
 * @author Thomas "HUFFMAN" Brunner
 */
public abstract class Triangle
{
	protected float[][] vertexNormals;
	protected float[] texCoordS, texCoordT;
	protected int[] vertexIndices;
	
	float[] getTexCoordS()
	{
		return texCoordS;
	}

	float[] getTexCoordT()
	{
		return texCoordT;
	}

	int[] getVertexIndices()
	{
		return vertexIndices;
	}

	float[][] getVertexNormals()
	{
		return vertexNormals;
	}
}
