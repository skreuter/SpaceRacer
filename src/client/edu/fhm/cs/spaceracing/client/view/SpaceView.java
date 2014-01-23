package edu.fhm.cs.spaceracing.client.view;

import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

import edu.fhm.cs.spaceracing.client.sound.LoopSoundPlayer;
import edu.fhm.cs.spaceracing.client.sound.SoundOrMusicSource;
import edu.fhm.cs.spaceracing.client.view.hud.GameHUD;
import edu.fhm.cs.spaceracing.client.view.hud.LoadingScreen;
import edu.fhm.cs.spaceracing.client.view.spaceFX.Explosion;
import edu.fhm.cs.spaceracing.client.view.spaceFX.ISFXContainer;
import edu.fhm.cs.spaceracing.client.view.spaceFX.LaserBeam;
import edu.fhm.cs.spaceracing.client.view.spaceFX.LensFlare;
import edu.fhm.cs.spaceracing.client.view.spaceFX.StarBox;
import edu.fhm.cs.spaceracing.client.view.spaceobjects.Arrow3D;
import edu.fhm.cs.spaceracing.client.view.spaceobjects.Camera;
import edu.fhm.cs.spaceracing.client.view.spaceobjects.Fighter1View;
import edu.fhm.cs.spaceracing.client.view.spaceobjects.RingView;
import edu.fhm.cs.spaceracing.client.view.spaceobjects.SpaceObjectView;
import edu.fhm.cs.spaceracing.client.view.tools.GLTools;
import edu.fhm.cs.spaceracing.model.generic.Sphere;
import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.model.space.Ring;
import edu.fhm.cs.spaceracing.model.space.Space;
import edu.fhm.cs.spaceracing.model.space.SpaceObject;

/**
 * SpaceView: Szenegraph des View.
 * Zeichnet ein Space-Objekt.
 * @author Thomas "HUFFMAN" Brunner
 */
public class SpaceView implements ISceneView
{
	private boolean isLoaded;
	final private Space refersTo;

	// Elemente der Szene
	private LinkedList<SpaceObjectView> objectViews;				// Feste Objekte im Raum
	private LinkedList<ISFXContainer> sfxObjects;					// An Objekte gebundene Spezialeffekte
	private StarBox starbox;										// Der Sternenhimmel
	private LensFlare flare;										// Die Sonne
	private Vector sunLightSource = new Vector(0.0, 0.0, 30.0);		// TODO: aus dem Model beziehen
									
	private Camera camera;
	private SpaceObject cameraTarget;								// Betrachtetes Objekt
	private SpaceObject playerTarget;								// Aktuelles Ziel (z.B. nächster Ring)
	private GameHUD hud;
	private Arrow3D arrow;											// TODO: in das HUD integrieren

	private int sphereDisplayList = -1;								// Displaylist für alle unbeweglichen Kollisionskugeln
	
	private int shaderProgram = -1;
	private LoopSoundPlayer engineLoop;
	
	private LoadingScreen loadingScreen = null;
	
	/**
	 * Erzeugt ein neues SpaceView.
	 * @param refersTo das Space-Objekt, welches dargestellt werden soll.
	 */
	public SpaceView(Space refersTo, SpaceObject lookingAt)
	{
		this.refersTo = refersTo;
		
		isLoaded = false;
		cameraTarget = lookingAt;
		playerTarget = null;
		starbox = new StarBox();
		flare = new LensFlare(sunLightSource);

		// SpaceObjectsViews erzeugen und Kamera einstellen
		update();
		
		// HUD erzeugen
		hud = new GameHUD();
		arrow = new Arrow3D();
		
		engineLoop = new LoopSoundPlayer(SoundOrMusicSource.SpaceshipEngineLoop, -1);
		engineLoop.setVolumeLevel(0.0f);
	}
	
	/**
	 * Zeichnet die Szene.
	 * @param autoDrawable der OpenGL-Kontext.
	 * @param resolutionX die Auflösung
	 * @param resolutionY die Auflösung
	 */
	public void drawScene(GLAutoDrawable autoDrawable, int resolutionX, int resolutionY)
	{
		GL gl = autoDrawable.getGL();
		
		if(!isLoaded)
		{
			preLoad(gl);
			return;
		}
		
		if(shaderProgram == -1)
			generateShaders(gl);
		
		/* Synchronisation: 
		 * Vektoren der SpaceObjectViews für diesen Loop zwischenspeichern
		 */		
		for(SpaceObjectView sObjView : objectViews)
			sObjView.updateVectors();
		
        // Bildschirm leeren
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);		
        
        // Projektion einstellen
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        GLU glu = new GLU();
        glu.gluPerspective(50.0, 1, 2.0, 3000.0);

        gl.glMatrixMode(GL.GL_MODELVIEW);    
        gl.glLoadIdentity();
        gl.glScalef(6, 6, 6);
        
        
        // Sichtfeld einstellen
        if(camera != null)
        	camera.updateCamera(gl);
		

		// Starbox mit der Kamera bewegen und zeichnen
        gl.glPushMatrix();	
		gl.glTranslated(camera.getPosition().getX(), 
						camera.getPosition().getY(), 
						camera.getPosition().getZ());
		starbox.draw(gl);
		setupLighting(gl);								// Lichtquelle mit der Starbox verschieben
		gl.glPopMatrix();

		
		// SpaceObjectViews zeichnen
		for(SpaceObjectView sObjView : objectViews)
		{			
			gl.glPushMatrix();			
			sObjView.drawObject(gl);
			gl.glPopMatrix();
		}
		
		
		// Sonnenlicht zeichnen
        gl.glPushMatrix();	
		gl.glTranslated(camera.getPosition().getX(), 
						camera.getPosition().getY(), 
						camera.getPosition().getZ());
		flare.draw(gl);
		gl.glPopMatrix();		

		
		// Objektgebundene Spezialeffekte zeichnen
		for(ISFXContainer s : sfxObjects)
		{
			gl.glPushMatrix();
			s.drawSFX(gl);
			gl.glPopMatrix();
		}

		// Kollisionskugeln zeichnen
		if(GraphicsSettings.isDrawCollisionSpheres())
		{
			if(sphereDisplayList == -1)
				genSphereDisplayList(gl);
			drawSpheres(gl);
		}
		
		// Pfeil zeichnen
        gl.glPushMatrix();	
		arrow.draw(gl, camera);
		gl.glPopMatrix();	
		
		// HUD zeichnen
		hud.setSpeed(((Ship) camera.getCameraLookingAt().getRefersTo()).getThrustLevel());
        hud.draw(autoDrawable, resolutionX, resolutionY);
        
        // Last-minute-hacks! Bitte weitergehen, es gibt nichts zu sehen.
        playerTargetHack();
        shipFiringHack();
        engineSoundHack();
	}
	
	
	// Beleuchtung einstellen
	private void setupLighting(GL gl)
	{
	    float[] lightAmbient = {0.8f, 0.8f, 0.8f, 1.0f};
	    float[] lightDiffuse = {10.0f, 10.0f, 10.0f, 1.0f};
	    float[] lightPosition = {(float)sunLightSource.getX(), 
	    						 (float)sunLightSource.getY(), 
	    						 (float)sunLightSource.getZ(), 
	    						 1.0f};
    	gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPosition, 0);

        gl.glEnable(GL.GL_LIGHT1);
        gl.glEnable(GL.GL_LIGHTING);
	}
	
	

	
	// Zeichnet die Kollisionskugeln der SpaceObjects.
	private void drawSpheres(GL gl)
	{
		GLUT glut = new GLUT();

		gl.glCallList(sphereDisplayList);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		gl.glColor4f(1, 1, 1, 1);
		
		// Alle beweglichen Kugeln müssen werden neu errechnet.
		for(SpaceObjectView sObj : objectViews)
		{
			if(!(sObj.getRefersTo() instanceof Ship))
				continue;
			for(Sphere sphere : sObj.getRefersTo().getSpheres())
			{
				gl.glPushMatrix();
				gl.glTranslated(sphere.getPosition().getX(), sphere.getPosition().getY(), sphere.getPosition().getZ());

				glut.glutWireSphere(sphere.getRadius(), 15, 15);
				gl.glPopMatrix();
			}
		}
		gl.glEnable(GL.GL_LIGHTING);
	}
	
	// Generiert eine DisplayList, in welcher alle festen enthalten sind.
	// Die GLUT-Kugelberechnung ist teuer.
	private void genSphereDisplayList(GL gl)
	{	
		GLUT glut = new GLUT();
		sphereDisplayList = gl.glGenLists(1);
		
		gl.glNewList(sphereDisplayList, GL.GL_COMPILE);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
			for(SpaceObject sObj : refersTo.getObjects())
			{
				if(sObj instanceof Ship)
					continue;
				for(Sphere sphere : sObj.getSpheres())
				{
					gl.glPushMatrix();
					gl.glTranslated(sphere.getPosition().getX(), sphere.getPosition().getY(), sphere.getPosition().getZ());
					glut.glutWireSphere(sphere.getRadius(), 15, 15);
					gl.glPopMatrix();
				}
			}
		gl.glEndList();
	}
	
	
	/**
	 * Aktualisiert den SpaceView aus dem Model.
	 */
	public void update()
	{
		objectViews = new LinkedList<SpaceObjectView>();
		sfxObjects = new LinkedList<ISFXContainer>();
		
		/* Durch den Space iterieren und ein SpaceObjectView
		 * für jedes SpaceObject erzeugen
		 */
		for(SpaceObject sObj : refersTo.getObjects())
		{
			SpaceObjectView s = null;
			
			if(sObj instanceof Ring)
			{
				s = new RingView(sObj);
				objectViews.add(s);
			}
			else if(sObj instanceof Ship)
			{
				Fighter1View shipView = new Fighter1View(sObj);
				objectViews.add(shipView);
				sfxObjects.add(shipView);			// An dem Schiff hängen Spezialeffekte
				s = shipView;
			}
			if(sObj == cameraTarget)
				camera = new Camera(s);					// Kamera erzeugen
			if(sObj == playerTarget)
			{
				arrow.setCurrentTarget(s);
				s.mark(true);
			}
		}
		
		// Kollisionskugel-Liste neu erzeugen
		sphereDisplayList = -1;
		
		// TODO: Ballernde und explodierende Schiffe erkennen
	}
	
	/**
	 * Lädt alle in der Szene verwendeten Models und Texturen.
	 * @param gl das GL-Objekt.
	 */
	private void preLoad(GL gl)
	{
		if(loadingScreen == null)
		{
			System.out.print("Loading models...");
			loadingScreen = new LoadingScreen();
			loadingScreen.add(Fighter1View.class);		// Lädt ShipTrail mit
			loadingScreen.add(RingView.class);
			loadingScreen.add(StarBox.class);
			loadingScreen.add(Explosion.class);
			loadingScreen.add(LensFlare.class);
			loadingScreen.add(LaserBeam.class);			// Lädt Explosion mit
		}
		
		loadingScreen.draw(gl);
		
		if(loadingScreen.isFinished())
		{
			if(GraphicsSettings.isDrawCollisionSpheres())
				genSphereDisplayList(gl);
			
			System.out.println("done.");
			engineLoop.play();
			isLoaded = true;
		}
	}

	
	/**
	 * Gibt das von der Kamera betrachtete SpaceObject zurück.
	 * @return das Kameraziel.
	 */
	public SpaceObject getCameraLookingAt()
	{
		return camera.getCameraLookingAt().getRefersTo();
	}

	
	/**
	 * Setzt das von der Kamera zu betrachtende SpaceObject.
	 * @param cameraLookingAt das Kameraziel.
	 */
	public void setCameraLookingAt(SpaceObject cameraLookingAt)
	{
		cameraTarget = cameraLookingAt;
		update();
	}

	/**
	 * Gibt das aktuelle Zielobjekt des Spielers (z.B. nächster Ring) zurück.
	 * @return das aktuelle Ziel des Spielers.
	 */
	public SpaceObject getPlayerTarget()
	{
		return playerTarget;
	}
	
	/**
	 * Setzt das aktuelle Zielobjekt des Spielers (z.B. nächster Ring).
	 * @param playerTarget das Ziel.
	 */
	public void setPlayerTarget(SpaceObject playerTarget)
	{
		this.playerTarget = playerTarget;
		for(SpaceObjectView sObj : objectViews)
		{
			sObj.mark(false);
			if(sObj.getRefersTo() == playerTarget)
			{
				arrow.setCurrentTarget(sObj);
				sObj.mark(true);
			}
		}
		if(playerTarget == null)
			arrow.setCurrentTarget(null);
	}
	
	/**
	 * Gibt zurück, ob die 3D-Models und Texturen bereits geladen wurden.
	 * @return true, wenn der Ladevorgang beendet wurde.
	 */
	public boolean isLoaded()
	{
		return isLoaded;
	}

	
	/* Böser Hack: holt sich den nächsten Ring des Players, welcher das Kameraziel steuert.
	 * Nur weil der Controller das nicht gebacken kriegt.
	 * TODO: gescheite Lösung!
	 */
	private void playerTargetHack()
	{
		SpaceObject camTarget = camera.getCameraLookingAt().getRefersTo();
		if(!(camTarget instanceof Ship))
			return;
		
		Ship s = (Ship)camTarget;
		int checkpointIndex = s.getPlayer().getLastCheckpointIndex();
		
		// Nächsten Ring markieren
		if(refersTo.getObstacles().size() > checkpointIndex + 1)
		{
			SpaceObject newTarget = refersTo.getObstacles().get(checkpointIndex + 1);
			if(playerTarget != newTarget)
				this.setPlayerTarget(newTarget);
		}
		// Ersten Ring markieren
		else if(refersTo.getObstacles().size() == checkpointIndex + 1)
		{
			SpaceObject newTarget = refersTo.getObstacles().get(0);
			if(playerTarget != newTarget)
				this.setPlayerTarget(newTarget);
		}
		// Markierung entfernen
		else
			if(playerTarget != null)
			{
				this.setPlayerTarget(null);
				hud.gameWon();
			}
	}
	
	
	/* Hack: überprüft ob ein Ship feuert.
	 * TODO: irgendwie mit Events handeln
	 */
	private void shipFiringHack()
	{
		if(camera.getCameraLookingAt().getRefersTo() instanceof Ship
			&& ((Ship)(camera.getCameraLookingAt().getRefersTo())).hasFired()
			&& camera.getCameraLookingAt() instanceof Fighter1View)
		{
			// EVIL HACK: einfach 50 nach vorne ballern.
			((Ship)(camera.getCameraLookingAt().getRefersTo())).setFired(false);
			((Fighter1View)(camera.getCameraLookingAt())).fireLaser(camera.getCameraLookingAt().getPosition().add(camera.getCameraLookingAt().getDirection().multiplyWithScalar(50)), true);
		}
		
	}
	
	/*
	 * ebil hack: antriebsgeräusch machen
	 */
	private int frameSkip = 5;
	private void engineSoundHack()
	{
		if(++frameSkip < 5)
			return;
		frameSkip = 0;
		
		SpaceObject camTarget = camera.getCameraLookingAt().getRefersTo();
		if(!(camTarget instanceof Ship))
			return;
		
		Ship s = (Ship)camTarget;
		engineLoop.setVolumeLevel(Math.abs(s.getThrustLevel()) / 100.0f);
	}
	
	private void generateShaders(GL gl)
	{
		int vertexShader = GLTools.generateShader(gl, GL.GL_VERTEX_SHADER, "models/sfx/shaders/testToonShader.vert");
		int fragmentShader = GLTools.generateShader(gl, GL.GL_FRAGMENT_SHADER, "models/sfx/shaders/testToonShader.frag");
		
		shaderProgram = gl.glCreateProgram();

		gl.glAttachShader(shaderProgram, vertexShader);
		gl.glAttachShader(shaderProgram, fragmentShader);
		gl.glLinkProgram(shaderProgram);
		gl.glValidateProgram(shaderProgram);

		//gl.glUseProgram(shaderProgram); 
		
	}
}