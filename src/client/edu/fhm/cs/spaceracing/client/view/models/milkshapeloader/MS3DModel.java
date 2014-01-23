package edu.fhm.cs.spaceracing.client.view.models.milkshapeloader;


import java.io.FileInputStream;
import java.io.IOException;

import edu.fhm.cs.spaceracing.client.view.models.Model;

/**
 * Ein MilkShape3D Model.
 * @author Thomas "HUFFMAN" Brunner
 */
@SuppressWarnings("unused")
public class MS3DModel extends Model
{
	/**
	 * Erzeugt ein neues MilkShape3D-Model.
	 * @param filePath das Verzeichnis, in dem sich das Model befindet.
	 */
    public MS3DModel(String filePath)
    {
    	super(filePath);
    }
    
	/**
	 * Laedt ein MilkShape3D-Model aus einer Datei. 
	 * @param filename der Dateiname des Models.
	 * @throws IOException wenn die Datei nicht geoeffnet werden kann oder ungueltig ist.
	 */
    @Override
	public void loadModelData(String filename) throws IOException
    {
    	FileInputStream is = new FileInputStream(directory + filename);
    	LittleEndianDataInputStream input = new LittleEndianDataInputStream(is);
    
    	// Milkshape Header (Muss aufgerufen werden, 
    	// da im Konstruktor vom InputStream gelesen wird.)
    	new MS3DHeader(input);
        
    	
    	// Milkshape Vertexe
    	int numVertices = input.readUnsignedShort();
    	vertices = new MS3DVertex[numVertices];
        for (int vc = 0; vc < numVertices; vc++) 
            vertices[vc] = new MS3DVertex(input);
        
    	
        
        // Milkshape Triangles
        int numTriangles = input.readUnsignedShort();
        triangles = new MS3DTriangle[numTriangles];
        for (int tc = 0; tc < numTriangles; tc++) 
            triangles[tc] = new MS3DTriangle(input);
        
        
        
        // Milkshape Groups/Meshes
        int numGroups = input.readUnsignedShort();
        meshes = new MS3DGroup[numGroups];
        for (int gc = 0; gc < numGroups; gc++) 
            meshes[gc] = new MS3DGroup(input);
        

        // Milskhape Materials
        int numMaterials = input.readUnsignedShort();
        materials = new MS3DMaterial[numMaterials];
        for (int mc = 0; mc < numMaterials; mc++) 
            materials[mc] = new MS3DMaterial(input);
        

        
        // Für Skelettanimation (unused)
//        float fAnimationFPS = input.readFloat();
//        float fCurrentTime = input.readFloat();
//        int iTotalFrames = input.readInt();
        

        // Milkshape Joints hier (unused)

    }
    
}
