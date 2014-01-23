package edu.fhm.cs.spaceracing.frames;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.media.opengl.GLCanvas;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.sun.opengl.util.FPSAnimator;

import edu.fhm.cs.spaceracing.frames.listeners.GlMenuEventListener;
import edu.fhm.cs.spaceracing.frames.menus.GlMenu;

/**
 * Das Menüfenster. <br>
 * Erzeugt die OpenGl-Anbindung und setzt die einzelnen Menüs.
 * 
 * @author Jan Bouillon
 *
 */
public class MenuFrame extends JFrame
{
	
    public static Dimension resolution = new Dimension(1024, 768);
    
    private FPSAnimator animator;
	private GLCanvas canvas;
	private GlMenuEventListener glMenuEventListener;
	
    /**
     * Das Anzeigemedium für die Menüs. <br>
     * Beim Instanziieren wird die Fenstergröße und das Icon gesetzt. Anschließend erfolgt die Anbindung an OpenGl mittels JOGL
     * und die Anmeldung des Mouse-, MouseMotion- und KeyListeners für die Interaktion des Benutzers und des GlEventListeners
     * für die 3D-Ausgabe.
     */
    public MenuFrame()
    {
        setSize(resolution);
    	setIconImage(new ImageIcon("images/icons/huffman.gif").getImage());
    	setResizable(false);
        
        getContentPane().add(build());
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                shutDown();
            }
            
        });
        
    }

    /**
     * Canvas erzeugen, Listener anmelden, Animator starten
     * 
     * @return Die Canvas für die Anbindung an OpenGl (JOGL)
     */
    private Component build()
    {
    	glMenuEventListener = new GlMenuEventListener(this);
    	
        canvas = new GLCanvas();
		canvas.addGLEventListener(glMenuEventListener);
        canvas.requestFocus();
        canvas.addMouseMotionListener(glMenuEventListener);
        canvas.addMouseListener(glMenuEventListener);
        // muß zweimal angemeldet werden, damit sofort auf keys reagiert werden kann (wegen unterschiedlichen ebenen)
        canvas.addKeyListener(glMenuEventListener);
        addKeyListener(glMenuEventListener);
        
        addPropertyChangeListener(new PropertyChangeListener()
        {

			public void propertyChange(PropertyChangeEvent evt)
			{
                if (evt.getPropertyName().equals("Menu changed"))
                {
                    canvas.repaint();   
                    resolution = canvas.getSize();
                }
			}
        	
        });
        
        animator = new FPSAnimator(canvas, 60);
        
        return canvas;
    }

    /**
     * Animator starten und beenden, in Abh�ngigkeit der Sichtbarkeit des Fensters
     * 
     * @see java.awt.Window#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean isVisible)
    {
    	super.setVisible(isVisible);
    	
    	if (isVisible && !animator.isAnimating())
    	{
    		animator.start();
    	}
    	if (!isVisible)
    	{
    		animator.stop();
    	}
    }

	/**
	 * Neues Menu für das Fenster setzen, das angezeigt werden soll. 
	 * 
	 * @param newMenu Das Menü, das angezeigt werden soll.
	 */
	public void setMenu(GlMenu newMenu)
	{
		GlMenu oldMenu = glMenuEventListener.getMenu();
		glMenuEventListener.setMenu(newMenu);
		firePropertyChange("Menu changed", oldMenu, newMenu);
	}
    
    /**
     * Beenden der Sounds, Animator stoppen, Fenster "entsorgen" und Anwendung beenden.
     */
    public void shutDown()
    {
        System.out.println("Finished Game!");
        glMenuEventListener.stopSounds();
        setVisible(false);
        dispose();
        // TODO: Solange nicht irgendwas noch im SpaceRacerGame aufzuräumen ist, wie zum Beispiel Profil speichern oder was auch immer,
        // und dort dann ne "shutDown()" aufgerufen wird, bleibt hier n System.exit(0)...
        System.exit(0);
    }
}
