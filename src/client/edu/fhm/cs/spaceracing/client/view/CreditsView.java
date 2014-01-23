package edu.fhm.cs.spaceracing.client.view;

import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import edu.fhm.cs.spaceracing.client.view.hud.LoadingScreen;
import edu.fhm.cs.spaceracing.client.view.spaceFX.ISFXContainer;
import edu.fhm.cs.spaceracing.client.view.spaceobjects.Fighter1View;
import edu.fhm.cs.spaceracing.client.view.spaceobjects.RingView;
import edu.fhm.cs.spaceracing.client.view.spaceobjects.SpaceObjectView;
import edu.fhm.cs.spaceracing.client.view.tools.GLTexture;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.model.space.Ring;

/**
 * Wir sind die Größten!
 * @author Thomas "HUFFMAN" Brunner
 */
public class CreditsView  implements ISceneView
{
	private Ship creditsShip;
	private Ring creditsRing;
	private SpaceObjectView shipView;
	private SpaceObjectView ringView;
	private ISFXContainer shipSFX;
	
	private float rotate = 0.0f;
	private boolean isLoaded = false;
	
	private GLTexture huff1tex = null;
	private GLTexture huff2tex = null;
	private GLTexture huffmantex = null;
	private GLTexture huffmanmasktex = null;
	private LinkedList<GLTexture> creditScreenTextures;

	// TIMELINE
	private int stage = 0;
	private int numStages = 17;
	private long startingTimeMs = 0;
	private float fadeAlpha = 1.0f;
	
	private static final long INTRO_LENGTH = 31140;
	private static final long LOOP_LENGTH = 24552;
	private long[] stageMillis = {2850, 3220, 4300, 6700, 13700, 18750, 25666, INTRO_LENGTH, 
								 INTRO_LENGTH + 7000, INTRO_LENGTH + 12175, INTRO_LENGTH + 19030, INTRO_LENGTH+LOOP_LENGTH,
								 INTRO_LENGTH+LOOP_LENGTH + 7000, INTRO_LENGTH+LOOP_LENGTH + 12175, INTRO_LENGTH+LOOP_LENGTH + 19030, INTRO_LENGTH+2*LOOP_LENGTH,
								 INTRO_LENGTH+2*LOOP_LENGTH + 2500};
	
	private LoadingScreen loadingScreen = null;

	
	/** 
	 * Erzeugt ein neues CreditsView.
	 */
	public CreditsView()
	{
		creditsShip = new Ship(null);
		creditsRing = new Ring(new Vector(0,0,0));
		creditsShip.setForwardThrust(15);

		Fighter1View ship = new Fighter1View(creditsShip);
		shipView = ship;
		shipSFX = ship;
		ringView = new RingView(creditsRing);
		creditScreenTextures = new LinkedList<GLTexture>();
	}
	
	/**
	 * Zeichnet die Credits.
	 */
	public void drawScene(GLAutoDrawable auto, int resolutionX, int resolutionY)
	{
		GL gl = auto.getGL();
		if(!isLoaded)
		{
			preLoad(gl);
			return;
		}

		calculateMovieStage();
		
        drawBackground(gl);					
        drawOverlay(auto);
	}

	// Errechnet die momentane Position in der Timeline.
	private void calculateMovieStage()
	{
		long timeElapsed = System.currentTimeMillis() - startingTimeMs;
		while(stage < numStages && timeElapsed > stageMillis[stage])
			stage++;
	}
	
	// Zeichnet Ring und Raumschiff.
	private void drawBackground(GL gl)
	{
		// Bildschirm leeren
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);		
        
        // Projektion einstellen
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        GLU glu = new GLU();
        glu.gluPerspective(50.0, 1, 2.0, 1500.0);

        gl.glMatrixMode(GL.GL_MODELVIEW);    
        gl.glLoadIdentity();

        gl.glTranslatef(0, 0, -7.0f);
        setupLighting(gl);
        
        rotate += 1f;
        setShipPosition();
        
        // Ring blinken lassen
        if((long)rotate % 40 == 0)
        	ringView.mark(!ringView.isMarked());
        
        // Ring
        gl.glPushMatrix();
        gl.glRotatef(-rotate, 0, 1, 0);
        gl.glScalef(0.5f, 0.5f, 0.5f);
        ringView.drawObject(gl);
        gl.glPopMatrix();
        
        // Raumschiff
        gl.glPushMatrix();
       	shipView.drawObject(gl);
        gl.glPopMatrix();
        
        // Raumschiff-SFX
       	gl.glPushMatrix();
       	shipSFX.drawSFX(gl);
        gl.glPopMatrix();
	}
	
	// Zeichnet Credits-Bilder und -Texte.
	private void drawOverlay(GLAutoDrawable auto)
	{
		GL gl = auto.getGL();
		GLU glu = new GLU();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0.0, 0.0, 1.0, 1.0); 		// 2D-Projektion
		
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_CULL_FACE);	
		gl.glDisable(GL.GL_LIGHTING);	
		

		// Fade-In-Blende zeichnen
		doFade(gl);

		// Bilder und Text zeichnen
		drawCreditsScreen(gl);
		
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
	}
	
	// Zeichnet die Credits-Bildschirme.
	private void drawCreditsScreen(GL gl)
	{
		if(stage < 4 || stage - 3 > creditScreenTextures.size())
			return;

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		gl.glColor4f(1, 1, 1, 1);
		creditScreenTextures.get(stage - 4).bind(gl);
		drawQuad(gl);
	}

	
	// Texturen blenden
	private void doFade(GL gl)
	{
		// In Stage 3 die Szene einblenden
		if(stage >= 3 && stage < 16 && fadeAlpha > 0.00f)
			fadeAlpha -= 0.007f;
		// In Stage 15 die Szene ausblenden
		if(stage >= 16 && fadeAlpha < 1.00f)
			fadeAlpha += 0.01f;
			
		gl.glEnable(GL.GL_BLEND);

		
		// Schwarze Maske
		gl.glColor4f(1f -(fadeAlpha / 1.5f + 0.33f), 1f -(fadeAlpha / 1.5f + 0.33f), 1f -(fadeAlpha / 1.5f + 0.33f), 1);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_ZERO);
		drawQuad(gl);
		
		

		
		// HUFF-MAN schriftzug
		if(stage == 0)
			gl.glColor4f(0f, 0f, 0f, 1);
		else if(stage < 15)
			gl.glColor4f(1f, 1f, 1f, fadeAlpha);
		else
			gl.glColor4f(0f, 0f, 0f, 1);
		
		if(stage == 0)
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		else if(stage == 1)
			huff1tex.bind(gl);
		else if(stage >= 2)
			huff2tex.bind(gl);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		drawQuad(gl);
		
		
		
		// Huffman-Bild-Maske
		if(stage > 0)
			gl.glColor4f(1f, 1f, 1f, 1f);
		huffmanmasktex.bind(gl);
		gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_ZERO);
		drawQuad(gl);
		
		
		// Huffman-Bild
		if(stage > 0)
			gl.glColor4f(1f, 1f, 1f, fadeAlpha/2 + 0.5f);
		if(stage >= 16)
			gl.glColor4f(1f, 1f, 1f, (1 - fadeAlpha) / 2);
		huffmantex.bind(gl);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		drawQuad(gl);
		
		gl.glDisable(GL.GL_BLEND);
	}


	// Zeichnet ein Quad über den ganzen Bildschirm.
	private void drawQuad(GL gl)
	{
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(1, 1);  gl.glVertex2d(  1.00f, 1.00f);
			gl.glTexCoord2f(0, 1);	gl.glVertex2d( -1.00f, 1.00f);
			gl.glTexCoord2f(0, 0);	gl.glVertex2d( -1.00f, -1.00f);
			gl.glTexCoord2f(1, 0);	gl.glVertex2d(  1.00f, -1.00f);
		gl.glEnd();
	}
	
	// Das Schiff um den Ring rumdrehen.
	private void setShipPosition()
	{
		
		double x = 2 * Math.sin(rotate / 20);
		double y = 2 * Math.cos(rotate / 35);
		double z = 2 * Math.cos(rotate / 20);

		Vector newPos = new Vector(x,y,z);
		Vector oldPos = creditsShip.getPosition();
		
		double dx = x - oldPos.getX();
		double dy = y - oldPos.getY();
		double dz = z - oldPos.getZ();
		
		creditsShip.setPosition(newPos);
		creditsShip.setDirection(new Vector(dx, dy, dz).normalize());
		creditsShip.setUp(new Vector(-dz, 0, dx).normalize());
		shipView.updateVectors();
	}
	
	
	// Beleuchtung einstellen.
	private void setupLighting(GL gl)
	{
	    float[] lightAmbient = {0.2f, 0.2f, 0.2f, 1.0f};
	    float[] lightDiffuse = {10.0f, 10.0f, 10.0f, 1.0f};
	    float[] lightPosition = {5, 0, 5,  1.0f};
    	gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPosition, 0);

        gl.glEnable(GL.GL_LIGHT1);
        gl.glEnable(GL.GL_LIGHTING);
	}
	
	private void preLoad(GL gl)
	{
		if(loadingScreen == null)
		{
			System.out.print("Loading credits...");
			
			// Texturen initialisieren
			huff1tex = new GLTexture("images/credits/huff1.jpg", false); 
			huff2tex = new GLTexture("images/credits/huff2.jpg", false);
			huffmantex = new GLTexture("images/credits/huffman.jpg", false);
			huffmanmasktex = new GLTexture("images/credits/huffman-mask.gif", false);
			for(int i = 0; i < 14; i++)
				creditScreenTextures.add(new GLTexture("images/credits/screen"+i+".gif", false));
	
			// Ladebildschirm erzeugen
			loadingScreen = new LoadingScreen();
			loadingScreen.add(Fighter1View.class);
			loadingScreen.add(RingView.class);
			loadingScreen.add(huff1tex);
			loadingScreen.add(huff2tex);
			loadingScreen.add(huffmantex);
			loadingScreen.add(huffmanmasktex);
			for(GLTexture texture : creditScreenTextures)
				loadingScreen.add(texture);
		}
		
		// Ladevorgang fortsetzen
		loadingScreen.draw(gl);

		if(loadingScreen.isFinished())
		{
			System.out.println("done.");
			isLoaded = true;
			startingTimeMs = System.currentTimeMillis();
		}
	}
	
	
	public boolean isLoaded()
	{
		return isLoaded;
	}

}
