package edu.fhm.cs.spaceracing.client.view.spaceFX;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.model.generic.Vector;

@SuppressWarnings("unused")
public class StarFieldStar
{
	private Vector position;
	private float scale;
	private float alpha;
	private float r, g, b;
	
	StarFieldStar(Vector position, float scale, float alpha)
	{
		this.position = position;
		this.scale = scale;
		this.alpha = alpha;
		r = 1.0f;
		g = 1.0f;
		b = 1.0f;
	}
	
	void writeToBuffer(FloatBuffer buffer)
	{
		// Farb-Floats
		buffer.put(r);
		buffer.put(g);
		buffer.put(b);
		buffer.put(alpha);
		
		// Normalen-Floats
		buffer.put(0.0f);
		buffer.put(0.0f);
		buffer.put(0.0f);
		
		// Vertex-Floats
		buffer.put((float)position.getX());
		buffer.put((float)position.getY());
		buffer.put((float)position.getZ());
	}
}
