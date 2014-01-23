package edu.fhm.cs.spaceracing.client.view.spaceFX;

import javax.media.opengl.GL;

import edu.fhm.cs.spaceracing.client.view.GraphicsSettings;
import edu.fhm.cs.spaceracing.client.view.tools.GLTools;
import edu.fhm.cs.spaceracing.model.generic.Vector;

/**
 * Ein Sonnenlicht-Lensflare für die Sonne.
 * @author Thomas "HUFFMAN" Brunner
 */
public class LensFlare
{
    private Vector lightSourcePos;
    private int maxVisiblePixels = 1;								
    
    private static int texBigGlow = -1;
    private static int texStreak = -1;
    private static int texGlow = -1;
	private static final String MODEL_DIR = "models/lensflare";
    
	
	/**
	 * Erzeugt ein neues Lensflare.
	 * @param lightSourcePos die Position der Sonne.
	 */
	public LensFlare(Vector lightSourcePos)
	{
		this.lightSourcePos = lightSourcePos;
	}
	
	/**
	 * Zeichnet das Lensflare.
	 * @param gl das GL-Objekt.
	 */
	public void draw(GL gl)
	{
		// Texturen initialisieren
		if(texBigGlow == -1 || texStreak == -1 || texGlow == -1)
			preLoad(gl);
		
		
		// Überprüfen, ob das LensFlare verdeckt wird
		float scale = 1.0f;
		if(GraphicsSettings.isDoOcclusionTest())
		{
	        int pixels = visiblePixels(gl, lightSourcePos, 0.25f);
	        if(pixels > maxVisiblePixels)
	        	maxVisiblePixels = pixels;
	        scale = (float)pixels / (float)maxVisiblePixels;
		}
		
		// Wenn sichtbar, dann zeichnen
        if(scale > 0.1f)
        {
            gl.glEnable(GL.GL_BLEND);                                  
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
            gl.glDisable(GL.GL_DEPTH_TEST); 			        // Immer im Vordergrund
            gl.glDisable(GL.GL_LIGHTING);						// Die Sonne wird nicht beleuchtet

            // Den großen Glow-Effekt rendern
    		renderSprite(gl, texBigGlow, 0.60f, 0.60f, 0.8f, 0.5f, 15.0f * scale);
            renderSprite(gl, texBigGlow, 0.60f, 0.60f, 0.8f, 0.8f, 6.0f * scale);
            renderSprite(gl, texBigGlow, 0.60f, 0.60f, 0.8f, 0.5f, 3.0f * scale);
            
            // Die Streifen rendern: Nur wenn das Flare teilweise verdeckt wird
            float streakScale = 0.0f;
            if(scale < 0.65)
            {
            	// TODO: sinus loswerden
            	streakScale = (float)Math.sin((1/0.65) * scale * Math.PI);
            	renderSprite(gl, texStreak, 0.60f, 0.60f, 0.8f, 0.8f, 5.5f * streakScale);
            }
			
            // Kleiner Glow-Effekt
            renderSprite(gl, texGlow, 0.8f, 0.8f, 1.0f, 0.3f, 0.8f * scale);
            
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);		// Farbe zurücksetzen
            gl.glDisable(GL.GL_BLEND);
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glEnable(GL.GL_LIGHTING);
        }
    }
	
	private void renderSprite(GL gl, int texture, float r, float g, float b, float a, float scale)
	{		
		gl.glPushMatrix();
		gl.glTranslated(lightSourcePos.getX(), lightSourcePos.getY(), lightSourcePos.getZ());
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);	
		gl.glColor4f(r, g, b, a);				
		
		gl.glBegin(GL.GL_TRIANGLE_STRIP);			
			gl.glTexCoord2f(0.0f, 0.0f);					
			gl.glVertex2d(-scale, -scale);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex2d(-scale,  scale);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex2d( scale, -scale);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex2d( scale,  scale);
		gl.glEnd();										
		gl.glPopMatrix();					
	}
	
	
	/**
	 * Führt einen Occlusion-Test durch und prüft, ob ein Polygon verdeckt wird.
	 * @param gl das GL-Objekt
	 * @param center das Zentrum des zu testenden Polygon
	 * @param radius der Radius des zu testenden Polygons
	 * @return die Anzahl der sichtbaren Pixel
	 */
    private int visiblePixels(GL gl, Vector center, float radius)
    {	
    	boolean functionsOK = 
		gl.isFunctionAvailable("glGenQueriesARB") &&
		gl.isFunctionAvailable("glBeginQueryARB") &&
		gl.isFunctionAvailable("glGetQueryObjectivARB") &&
		gl.isFunctionAvailable("glDeleteQueriesARB");
		if(!functionsOK)
		{
			System.out.println("WARNING: Your graphics adapter does not support hardware Occlusion Queries.");
			System.out.println("Auf Deutsch: Kaufen Sie sich eine Grafikkarte noch aus diesem Jahrtausend!");
			GraphicsSettings.setDoOcclusionTest(false);
		}
		
    	// Tiefen- und Farbupdates deaktivieren (werden nicht gebraucht)
        gl.glDepthMask(false);
        gl.glColorMask(false, false, false, false);
               
        // Occlusion-Query starten
        int[] queries = new int[1];
        gl.glGenQueriesARB(1, queries, 0);
        gl.glBeginQueryARB(GL.GL_SAMPLES_PASSED_ARB, queries[0]);

        /* Ein der Kugel ähnelndes Dreieck zeichnen.
         * TODO: zumindest mal ne Kugel oder so!
         */
        gl.glBegin(GL.GL_TRIANGLES);
        	gl.glVertex3d(center.getX()-radius, center.getY()-radius, center.getZ());
        	gl.glVertex3d(center.getX(), center.getY()+radius, center.getZ());
        	gl.glVertex3d(center.getX()+radius, center.getY()-radius, center.getZ());
       	gl.glEnd();    	
       	
       	// Occlusion-Query beenden
        gl.glEndQueryARB(GL.GL_SAMPLES_PASSED_ARB);

        // Hier CPU-Zeit sinnvoll verbraten, bis die Query fertig is!
        Thread.yield();
        
        // Ergebnis holen
        int[] pixels = new int[1];
        gl.glGetQueryObjectivARB(queries[0], GL.GL_QUERY_RESULT_ARB, pixels, 0);
        gl.glDeleteQueriesARB(1, queries, 0);
       	
        // Updates wieder einschalten
        gl.glDepthMask(true);
        gl.glColorMask(true, true, true, true);

        return pixels[0];
    }
    
	/**
	 * Lädt die Texturen des LensFlares.
	 * @param gl das GL-Objekt.
	 */
	public static void preLoad(GL gl)
	{
		texBigGlow = GLTools.loadTexture(gl, MODEL_DIR + "/a_0.bmp");
		texGlow = GLTools.loadTexture(gl, MODEL_DIR + "/a_2.bmp");
		texStreak = GLTools.loadTexture(gl, MODEL_DIR + "/a_3.bmp");			
	}
}