package edu.fhm.cs.spaceracing.client.view.models;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

import edu.fhm.cs.spaceracing.client.view.tools.GLTools;
import edu.fhm.cs.spaceracing.client.view.tools.IPreloadable;

/**
 * Ein SpaceRacer-3D-Model.
 * Das Model muss nach dem Erzeugen noch explizit durch loadModelData geladen werden.
 * @author Thomas "HUFFMAN" Brunner
 */
public abstract class Model implements IPreloadable
{
	protected final String directory;
	
	// Bestandteile des Models
	// TODO: Auf Collections umschreiben
	protected Mesh[] meshes;
	protected Material[] materials;
	protected Triangle[] triangles;
	protected Vertex[] vertices;
	
	private int[] vboBuffer = null;
	private boolean isTexturesLoaded = false;


	/**
	 * Erzeugt ein neues Model.
	 * @param filePath das Verzeichnis, in dem sich das Model befindet.
	 */
	protected Model(String filePath)
	{
		meshes = null;
		materials = null;
		triangles = null;
		vertices = null;
		this.directory = filePath;
	}
	
	
	/**
	 * Zeichnet das Model.
	 * @param gl das GL-Objekt.
	 */
	public void draw(GL gl)
	{
		if(!isTexturesLoaded)
			loadTextures(gl);
	
		if(vboBuffer == null)
			buildVBOs(gl);

		// Nach Mesh (Gruppe) zeichnen. Jedes Mesh kann eine andere Textur haben.
		for(int i = 0; i < meshes.length; i++)
		{
			// Material und Texturen einstellen
			int materialIndex = meshes[i].getMaterialIndex();
			if(materialIndex >= 0)
			{
				gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, materials[materialIndex].getAmbient(), 0);
				gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, materials[materialIndex].getDiffuse(), 0);
				gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, materials[materialIndex].getSpecular(), 0);
				gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, materials[materialIndex].getEmissive(), 0);
				gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, materials[materialIndex].getShininess());

				if(materials[materialIndex].getTexture() > 0)
					gl.glBindTexture(GL.GL_TEXTURE_2D, materials[materialIndex].getTexture());
			}

			// Das Mesh aus dem VBO zeichnen.
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, vboBuffer[i]);
			gl.glInterleavedArrays(GL.GL_T2F_N3F_V3F, 0, 0);
            gl.glDrawArrays(GL.GL_TRIANGLES, 0, meshes[i].getNumTriangles() * 3);  
		}
	}
	
	

	// Lädt alle Texturen des Models.
	private void loadTextures(GL gl)
	{
		isTexturesLoaded = true;
        for (Material material : materials)
        {
            if(material.getTextureFilename().length() > 0)
                material.setTexture(GLTools.loadTexture(gl, directory + material.getTextureFilename()));
            else
                material.setTexture(0);
        }
    }

	// Schreibt die Vertexe in ein Vertex Buffer Object im Grafikspeicher.
	private void buildVBOs(GL gl) 
	{
		// Ein Dreieck besteht aus (2 texCoord + 3 Normal + 3 Vertex) * (x+y+z) floats
		final int SIZEOF_BUFFERED_TRIANGLE = 8 * 3 * BufferUtil.SIZEOF_FLOAT;
		
		boolean functionsOK = 
			gl.isFunctionAvailable("glGenBuffersARB") &&
			gl.isFunctionAvailable("glBindBufferARB") &&
			gl.isFunctionAvailable("glBufferDataARB") &&
			gl.isFunctionAvailable("glDeleteBuffersARB");
		if(!functionsOK)
			throw new RuntimeException("Your graphics adapter does not support Vertex Buffer objects!");
		
		// Für jeden Mesh ein neues VBO, da evtl. verschiedene Texturen verwendet werden
		vboBuffer = new int[meshes.length];
		
		for(int i = 0; i < meshes.length; i++)
		{	
			int numTriangles = meshes[i].getNumTriangles();
			FloatBuffer vertexBuffer = BufferUtil.newFloatBuffer(numTriangles * 8 * 3);		

			// Koordinaten in den FloatBuffer schreiben
			for(int j = 0; j < numTriangles; j++)
			{ 
				int triangleIndex = meshes[i].getTriangleIndices()[j];
				Triangle pTri = triangles[triangleIndex];
			
				for(int k = 0; k < 3; k++)
				{
					// 2x Texturkoordinaten
					vertexBuffer.put(pTri.getTexCoordS()[k]);
					vertexBuffer.put(pTri.getTexCoordT()[k]);
					
					// 3x Normalkoordinaten
					vertexBuffer.put(pTri.getVertexNormals()[k][0]);
					vertexBuffer.put(pTri.getVertexNormals()[k][1]);
					vertexBuffer.put(pTri.getVertexNormals()[k][2]);
					
					// 3x Vertexkoordinaten
					int index = pTri.getVertexIndices()[k];
					vertexBuffer.put(vertices[index].getLocation()[0]);
					vertexBuffer.put(vertices[index].getLocation()[1]);
					vertexBuffer.put(vertices[index].getLocation()[2]);
			
				}
			}						
			
			vertexBuffer.rewind();
			
			// Generiere und binde den Vertex Buffer  
			int[] tmpBuffer = new int[1];
			gl.glGenBuffersARB(1, tmpBuffer, 0);
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, tmpBuffer[0]);
			gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, numTriangles * SIZEOF_BUFFERED_TRIANGLE, vertexBuffer, GL.GL_STATIC_DRAW_ARB);
			vboBuffer[i] = tmpBuffer[0];
		}
	}
	
	/**
	 * Lädt das Model in den Grafikspeicher.
	 * @param gl das GL-Objekt.
	 */
	public void preLoad(GL gl)
	{
		buildVBOs(gl);
		loadTextures(gl);
	}
	
	/**
	 * Gibt den Vertex Buffer frei. 
	 * TODO: Sollte tunlichst vor der Garbage Collection aufgerufen werden.
	 */
	public void kill(GL gl)
	{
		if(vboBuffer != null)
			gl.glDeleteBuffersARB(vboBuffer.length, vboBuffer, 0);
	}
	
	/**
	 * Laedt ein 3D-Model aus einer Datei. 
	 * @param filename der Dateiname des Models.
	 * @throws IOException wenn die Datei nicht geoeffnet werden kann oder ungueltig ist.
	 */
	public abstract void loadModelData(String filename) throws IOException;
}
