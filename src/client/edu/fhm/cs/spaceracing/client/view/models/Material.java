package edu.fhm.cs.spaceracing.client.view.models;

/**
 * Ein Material wie es in einem 3D-Model verwendet wird.
 * @author Thomas "HUFFMAN" Brunner
 */
public abstract class Material
{
	protected float ambient[], diffuse[], specular[], emissive[];
	protected float shininess;
	protected int texture;
	protected String textureFilename;

	float[] getAmbient()
	{
		return ambient;
	}

	float[] getDiffuse()
	{
		return diffuse;
	}

	float[] getEmissive()
	{
		return emissive;
	}

	float getShininess()
	{
		return shininess;
	}

	float[] getSpecular()
	{
		return specular;
	}

	int getTexture()
	{
		return texture;
	}

	String getTextureFilename()
	{
		return textureFilename;
	}

	void setTexture(int texture)
	{
		this.texture = texture;
	}
}
