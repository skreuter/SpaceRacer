package edu.fhm.cs.spaceracing.client.view.models;

/**
 * Ein Mesh (Gruppe aus Triangles) wie es in einem 3D-Model verwendet wird.
 * @author Thomas "HUFFMAN" Brunner
 */
public abstract class Mesh
{
	protected int materialIndex;
	protected int numTriangles;
	protected int[] triangleIndices;
	
	int getMaterialIndex()
	{
		return materialIndex;
	}

	int getNumTriangles()
	{
		return numTriangles;
	}

	int[] getTriangleIndices()
	{
		return triangleIndices;
	}
	
	
}
