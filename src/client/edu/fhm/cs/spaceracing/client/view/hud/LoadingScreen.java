package edu.fhm.cs.spaceracing.client.view.hud;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import edu.fhm.cs.spaceracing.client.view.tools.GLTexture;
import edu.fhm.cs.spaceracing.client.view.tools.IPreloadable;

/**
 * Ein Ladebildschirm, welcher Classen und Objekte mit preLoad(GL)-Methoden lädt.
 * @author Thomas "HUFFMAN" Brunner
 */
public class LoadingScreen
{
	private final LinkedList<Class> preloadClasses;
	private final LinkedList<IPreloadable> preloadObjects;
	private boolean hasLoadingStarted = false;
	private int numElements = 0;
	
	private GLTexture progressBar = null;
	private GLTexture progressBarFrame = null;
	
	private boolean firstTimeRun = true;
	
	/**
	 * Erzeugt einen neuen Ladebildschirm.
	 */
	public LoadingScreen()
	{
		preloadClasses = new LinkedList<Class>();
		preloadObjects = new LinkedList<IPreloadable>();
	}
	
	/**
	 * Zeichnet den Ladebildschirm und lädt das nächste Element.
	 * @param gl das GL-Objekt.
	 */
	public void draw(GL gl)
	{
		// Ein Frame auslassen, damit schon einmal der schwarze Hintergrund gezeichnet wird
		if(firstTimeRun)
		{
			firstTimeRun = false;
			return;
		}
		
		if(!hasLoadingStarted)
		{
			hasLoadingStarted = true;
			numElements = preloadClasses.size() + preloadObjects.size();
		}
		int doneElements = numElements - (preloadClasses.size() + preloadObjects.size());
		float done = (float)doneElements / (float)numElements;
		
		drawPicture(gl, done);
		loadNextElement(gl);
	}
	
	/* Zeichnet die Grafik.
	 * TODO: eigene Klasse machen, und vielleicht bissl schöner
	 */
	private void drawPicture(GL gl, float done)
	{
		if(progressBar == null || progressBarFrame == null)
			initLoadingScreenTextures(gl);
		
		GLU glu = new GLU();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0.0, 0.0, 1.0, 1.0); 		// 2D-Projektion
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_CULL_FACE);	
		gl.glDisable(GL.GL_LIGHTING);	
		
		// Inhalt
		progressBar.bind(gl);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(1, 1);  gl.glVertex2d(  0.47f, -0.32f);
			gl.glTexCoord2f(0, 1);	gl.glVertex2d( -0.47f, -0.32f);
			gl.glTexCoord2f(0, 0);	gl.glVertex2d( -0.47f, -0.40f);
			gl.glTexCoord2f(1, 0);	gl.glVertex2d(  0.47f, -0.40f);
		gl.glEnd();

		// Schwarze Maske
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(1, 1);  gl.glVertex2d(  0.47f, -0.32f);
			gl.glTexCoord2f(0, 1);	gl.glVertex2d( -0.47f + 0.94f*done, -0.32f);
			gl.glTexCoord2f(0, 0);	gl.glVertex2d( -0.47f + 0.94f*done, -0.40f);
			gl.glTexCoord2f(1, 0);	gl.glVertex2d(  0.47f, -0.40f);
		gl.glEnd();
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		// Rahmen
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		progressBarFrame.bind(gl);
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(1, 1);  gl.glVertex2d(  0.47f, -0.32f);
			gl.glTexCoord2f(0, 1);	gl.glVertex2d( -0.47f, -0.32f);
			gl.glTexCoord2f(0, 0);	gl.glVertex2d( -0.47f, -0.40f);
			gl.glTexCoord2f(1, 0);	gl.glVertex2d(  0.47f, -0.40f);
		gl.glEnd();
		gl.glDisable(GL.GL_BLEND);
	}
	
	private void initLoadingScreenTextures(GL gl)
	{
		progressBar = new GLTexture("images/loadingscreen/progressbarcontent.gif", false);
		progressBar.preLoad(gl);
		progressBarFrame = new GLTexture("images/loadingscreen/progressbarframe.gif", false);
		progressBarFrame.preLoad(gl);
	}
	
	/**
	 * Fügt ein neues Preloadable-Objekt hinzu. Muss vor dem Zeichnen geschehen!
	 * @param p das Objekt, das geladen werden soll.
	 */
	public void add(IPreloadable p)
	{
		if(hasLoadingStarted)
			throw new IllegalArgumentException("Cannot add new element: Loading is already in progress!");
		preloadObjects.add(p);
	}
	
	/**
	 * Fügt eine neue Klasse mit static-preLoad(GL)-Methode hinzu. Muss vor dem Zeichnen geschehen!
	 * @param c eine Klasse, welche eine Methode nach dem Format "static void preLoad(GL)" besitzt.
	 */
	public void add(Class c)
	{
		if(hasLoadingStarted)
			throw new IllegalArgumentException("Cannot add new element: Loading is already in progress!");

		// Überprüfen, ob eine statische preLoad(GL)-Methode vorhanden ist
		for(Method method : c.getMethods())
		{
			if( method.getName().equals("preLoad")
			 &&	method.getParameterTypes().length == 1
			 && method.getParameterTypes()[0] == GL.class
			 && Modifier.isStatic(method.getModifiers()))
			{
				preloadClasses.add(c);
				return;
			}
		}
		throw new IllegalArgumentException("Cannot add " + c.getName() + ": Has no static preLoad(GL) method!");
		
	}
	
	/**
	 * Gibt zurück, ob der Ladevorgang beendet ist.
	 * @return true, wenn der Ladevorgang beendet ist, false falls nicht.
	 */
	public boolean isFinished()
	{
		return preloadClasses.size() + preloadObjects.size() == 0;
	}
	
	
	private void loadNextElement(GL gl)
	{
		// Den static-Preloader einer Klasse aufrufen
		if(preloadClasses.size() > 0)
		{
			for(Method method : preloadClasses.removeFirst().getMethods())
				if( method.getName().equals("preLoad")
				 &&	method.getParameterTypes().length == 1
				 && method.getParameterTypes()[0] == GL.class
				 && Modifier.isStatic(method.getModifiers()))
				{
					try
					{
						method.invoke(null, gl);
					}
					catch(Exception e)
					{
						throw new RuntimeException(e);
					}
				}
		}
		else if(preloadObjects.size() > 0)
			preloadObjects.removeFirst().preLoad(gl);
	}
	
	
}
