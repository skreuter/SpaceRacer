package edu.fhm.cs.spaceracing.client.view;

import javax.media.opengl.GLAutoDrawable;
/**
 * SceneView: Interface für Spaceracer-Spielszenen
 * @author Thomas "HUFFMAN" Brunner
 */
public interface ISceneView
{
	void drawScene(GLAutoDrawable autoDrawable, int resolutionX, int resolutionY);
	
}