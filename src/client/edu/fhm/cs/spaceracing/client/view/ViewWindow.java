package edu.fhm.cs.spaceracing.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

/**
 * ViewWindow: Erzeugt ein Fenster mit einer Spaceracer-Szene
 * @author Thomas "HUFFMAN" Brunner
 */
@SuppressWarnings("serial")
public class ViewWindow extends JFrame implements GLEventListener
{
	private final GLCanvas canvas;
	private Animator animator;
	private GraphicsDevice graphicsDevice;
	private ISceneView scene;
	private int resolutionX, resolutionY;
	private String windowTitle = "The incredible Space Racer";
	
	
	// FPS-Zähler
	private int fpsFrames = 0;
	private long fpsTime = 0;
	
	
	/**
	 * Erzeugt ein neues ViewWindow.
	 * @param scene die Spaceracer-Szene, die dargestellt werden soll.
	 * @param resolutionX die X-Auflösung
	 * @param resolutionY die Y-Auflösung
	 */
    public ViewWindow(ISceneView scene, int resolutionX, int resolutionY)
    {       	    	
    	this.scene = scene;
    	this.resolutionX = resolutionX;
    	this.resolutionY = resolutionY;
    	
    	// Stellt das Fenstericon ein
    	ImageIcon img = new ImageIcon("images/icons/huffman.gif");
    	setIconImage(img.getImage());
    	
    	setSize(new Dimension(resolutionX, resolutionY));
        setTitle(windowTitle);
        setResizable(false);
             
        if(GraphicsSettings.isFullscreen())
        	switchToFullscreenMode();
        
        // Full Screen Antialiasing einschalten
        GLCapabilities glCaps = new GLCapabilities();
        if(GraphicsSettings.getMultisamplingLevel() > 0)
        {
        	glCaps.setSampleBuffers(true);  
        	glCaps.setNumSamples(GraphicsSettings.getMultisamplingLevel());
        }
        
        canvas = new GLCanvas(glCaps);   
		canvas.addGLEventListener(this);
		
		// Animator-Thread starten
		if(GraphicsSettings.isLimitFPS())
			animator = new FPSAnimator(canvas, 100);
		else
			animator = new Animator(canvas);
		animator.start();
		
        getContentPane().add(canvas,BorderLayout.CENTER);
        setVisible(true);
    }
 	
    
    /**
     * Initialisiert den GLEventListener.
     * @param autoDraw enthält den GL Rendering Context.
     */
    public void init(GLAutoDrawable autoDraw)
    {
        GL gl = autoDraw.getGL();

        if(GraphicsSettings.getMultisamplingLevel() > 0)
        	gl.glEnable(GL.GL_MULTISAMPLE);

        gl.glShadeModel(GL.GL_SMOOTH);				// Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);   	// Hintergrundfarbe schwarz     
        
        // VSync einstellen
		if(GraphicsSettings.isLimitFPS())
			gl.setSwapInterval(1);
		else
			gl.setSwapInterval(0);
        
        // Tiefenpuffer einschalten
        gl.glClearDepth(1.0f);                      
        gl.glEnable(GL.GL_DEPTH_TEST);				
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_NORMALIZE);				// Vertex-Normalen normalisieren
        gl.glEnable(GL.GL_CULL_FACE);				// Polygonrueckseiten nicht zeichnen

        // Der Viewport reicht über den ganzen Bildschirm
        gl.glViewport(0, 0, resolutionX, resolutionY);
    }

    
    
    /**
     * Zeichnet die Szene.
     * @param autoDrawable das GLAutoDrawable-Objekt.
     */
    public void display(GLAutoDrawable autoDrawable)
    {
        countFPS();
    	if(scene == null)
        	return;
    	
    	scene.drawScene(autoDrawable, resolutionX, resolutionY);
    }

    // Die Fenstergröße wird nicht geändert
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4)
    {
    }

    // Unbenutzt von JOGL
    public void displayChanged(GLAutoDrawable glDrawable, boolean modeChanged, boolean deviceChanged)
    {
    }
    
    
	// Zählt die FPS und zeigt sie im Fenstertitel an. 
	private void countFPS()
	{
		fpsFrames++;
		if(System.currentTimeMillis() > fpsTime + 2000)
		{
			fpsTime = System.currentTimeMillis();
			setTitle(windowTitle + " @ " + fpsFrames / 2 + " FPS");
			fpsFrames = 0;
		}
	}
    
	// Aktiviert den Fullscreen-Modus
    private void switchToFullscreenMode()
    {
		System.out.println("Fullscreen an!");
    	graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    	// Fensterrahmen entfernen
		setUndecorated(true);
		
        graphicsDevice.setFullScreenWindow(this);
        
        // Fullscreen beim Beenden abschalten
        addWindowListener(new WindowAdapter() 
        {
        	@Override
			public void windowClosing(WindowEvent e) 
        	  {
        		System.out.println("Fullscreen aus!");
        		graphicsDevice.setFullScreenWindow(null);
        	  } 
        });

        // Passenden DisplayMode finden und setzen
        DisplayMode displayMode = catchDisplayMode(
            graphicsDevice.getDisplayModes(), 
            resolutionX,               
            resolutionY,
            graphicsDevice.getDisplayMode().getBitDepth(), 
            graphicsDevice.getDisplayMode().getRefreshRate());
        graphicsDevice.setDisplayMode(displayMode);      
    }

    
    // Geeigneten DisplayMode ermitteln
    private DisplayMode catchDisplayMode(DisplayMode[] displayModes, int requestedWidth, int requestedHeight,
            int requestedDepth, int requestedRefreshRate)
    {
        // Nach genauer Übereinstimmung suchen
        DisplayMode displayMode = searchDisplayMode(displayModes, requestedWidth, requestedHeight,
                                                    requestedDepth, requestedRefreshRate);
        if(displayMode == null)
        {
            // Bits und Refreshrate ignorieren
            displayMode = searchDisplayMode(displayModes, requestedWidth, requestedHeight,
                                            -1, -1);
            
            if(displayMode == null)
            {
            	// Zusätzlich Höhe ignorieren
                displayMode = searchDisplayMode(displayModes, requestedWidth,
                                                -1, -1, -1);
               
                // Ansonsten, einfach irgendwas nehmen
                if(displayMode == null)
                    displayMode = searchDisplayMode(displayModes, -1,
                                                    -1, -1, -1);
            }
        }
        else
        {
          //  System.out.println("Genaue Übereinstimmung!");
        }
       
        return displayMode;
    }
   
    
    // Verfügbare DisplayModes durchsuchen
    private DisplayMode searchDisplayMode(DisplayMode[] displayModes, int requestedWidth, int requestedHeight,
            int requestedDepth, int requestedRefreshRate)
    {
        DisplayMode displayModeToUse = null;
       

        for(int i = 0; i < displayModes.length; i++)
        {
            DisplayMode displayMode = displayModes[i];
           
            if((requestedWidth == -1 || displayMode.getWidth() == requestedWidth) &&
                (requestedHeight == -1 || displayMode.getHeight() == requestedHeight) &&
                (requestedHeight == -1 || displayMode.getRefreshRate() == requestedRefreshRate) &&
                (requestedDepth == -1 || displayMode.getBitDepth() == requestedDepth))
                    displayModeToUse = displayMode;
        }
       
        return displayModeToUse;
    }
	
    /**
     * Setzt die darzustellende SpaceRacer-Szene.
     * @param scene die im Fenster darzustellende Szene.
     */
    public void setScene(ISceneView scene)
    {
    	this.scene = scene;
    }
    
    /**
     * Gibt das GLCanvas des Fensters zurück (z.B. um Listener zu registrieren).
     * @return das GLCanvas des Fensters.
     */
    public GLCanvas getCanvas()
    {
    	return canvas;
    }
    
   
    /**
     * Macht das Fenster sichtbar, bzw. unsichtbar.
     * Startet, bzw. stoppt außerdem den Animator des Fensters.
     * @param isVisible true für ein sichtbares Fenster und einen laufenden Animator. 
     * @see java.awt.Window#setVisible(boolean)
     */
   @Override
   public void setVisible(boolean isVisible)
   {
   	super.setVisible(isVisible);
   	
   	if(animator == null)
   		return;
   	
   	if (isVisible && !animator.isAnimating())
   		animator.start();
   	if (!isVisible && animator.isAnimating())
   		animator.stop();
   }
   
}