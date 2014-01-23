package edu.fhm.cs.spaceracing.frames.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import edu.fhm.cs.spaceracing.client.controller.BeforeGame;
import edu.fhm.cs.spaceracing.client.controller.CreditsController;
import edu.fhm.cs.spaceracing.client.controller.events.UnableToStartGameException;
import edu.fhm.cs.spaceracing.client.sound.LoopSoundPlayer;
import edu.fhm.cs.spaceracing.client.sound.SoundOrMusicSource;
import edu.fhm.cs.spaceracing.client.sound.SoundPlayer;
import edu.fhm.cs.spaceracing.frames.MenuFrame;
import edu.fhm.cs.spaceracing.frames.components.GlButton;
import edu.fhm.cs.spaceracing.frames.components.GlCharacter;
import edu.fhm.cs.spaceracing.frames.components.GlComponent;
import edu.fhm.cs.spaceracing.frames.components.GlContainer;
import edu.fhm.cs.spaceracing.frames.menus.GlMenu;
import edu.fhm.cs.spaceracing.frames.menus.InputMenu;
import edu.fhm.cs.spaceracing.frames.menus.MainMenu;
import edu.fhm.cs.spaceracing.frames.menus.OptionsMenu;
import edu.fhm.cs.spaceracing.frames.menus.ProfilesMenu;
import edu.fhm.cs.spaceracing.frames.utils.GlFont;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;
import edu.fhm.cs.spaceracing.frames.utils.Texture;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;

public class GlMenuEventListener implements GLEventListener, MouseMotionListener, MouseListener, KeyListener
{

	private int width;
	private int height;

	private GlMenu menu;

	private ArrayList<GlComponent> components;
	private int numberOfTextures;
    private int indexOfCurrentSelectedComponent = -1;
	
	private boolean menuChanged;
	private int[] textures;
    
    // TODO_JAN für die Framerate
    private int fps = 0;
    private long time = System.currentTimeMillis();
    
    private final MenuFrame owner;
    private HashMap<SoundOrMusicSource, SoundPlayer> sounds;

	public GlMenuEventListener(MenuFrame owner)
	{
		this.owner = owner;
		
		sounds = new HashMap<SoundOrMusicSource, SoundPlayer>();
		sounds.put(SoundOrMusicSource.MenuMusic, new LoopSoundPlayer(SoundOrMusicSource.MenuMusic));
		sounds.put(SoundOrMusicSource.MenuHighlight, new SoundPlayer(SoundOrMusicSource.MenuHighlight));
		playSound(SoundOrMusicSource.MenuMusic);
        
        width = MenuFrame.resolution.width;
		height = MenuFrame.resolution.height;
		
		menu = new GlMenu();
		components = menu.getComponents();
		numberOfTextures = components.size();
		menuChanged = false;
	}

    public void setMenu(GlMenu newMenu)
	{
		this.menu = newMenu;
		menuChanged = true;
	}

	public GlMenu getMenu()
	{
		return menu;
	}
	
    public void stopSounds()
	{
    	for (SoundPlayer player : sounds.values())
		{
    		if (player.isPlaying())
    		{
    			player.stop();
    		}
		}
	}
    
    private void playSound(SoundOrMusicSource source)
    {
    	SoundPlayer player = sounds.get(source);
    	if (player != null)
    	{
    		player.play();
    	}
    }

	private ArrayList<GlComponent> getAllComponents(ArrayList<GlComponent> components)
	{
		ArrayList<GlComponent> allComponents = new ArrayList<GlComponent>();
		
		for (GlComponent component : components)
		{
			if (component instanceof GlContainer)
			{
				allComponents.addAll(getAllComponents(((GlContainer) component).getComponents()));
			}
			if (component.isVisible())
			{
				allComponents.add(component);
				numberOfTextures++;
				if (component instanceof GlButton)
				{
					numberOfTextures++;
				}
			}
		}
		
		return allComponents;
	}
	
	private ArrayList<GlComponent> getAllComponents()
	{
		numberOfTextures = 0;
		return getAllComponents(menu.getComponents());
	}

	private void printGlInfo(GL gl)
	{
		// Info
		System.out.println("Gl-Info:");
		System.out.println("\tGl vendor = " + gl.glGetString(GL.GL_VENDOR));
		System.out.println("\tGl renderer = " + gl.glGetString(GL.GL_RENDER));
		System.out.println("\tGl version = " + gl.glGetString(GL.GL_VERSION));
		System.out.println();
	}

	private void initTextures(GL gl)
	{
		components = getAllComponents();
		if (components.size() > 0)
		{
			textures = new int[numberOfTextures+36];
			gl.glGenTextures(textures.length, textures, 0);
			
			int textureIndex = 0;
			for (GlComponent component : components)
			{
				Texture texture = component.getTexture();
				bindTexture(gl, texture, textureIndex++);
				
				if (component instanceof GlButton)
				{
					Texture highlightedTexture = ((GlButton) component).getTextureOfSelectedButton();
					bindTexture(gl, highlightedTexture, textureIndex++);
				}
                
//                System.out.println(component + " loaded.");
			}
			
			initFont(gl, textureIndex);
			gl.glEnable(GL.GL_TEXTURE_2D);
		}
	}

	private void bindTexture(GL gl, Texture texture, int indexOfCurrentComponent)
	{
		int texturePointer = textures[indexOfCurrentComponent];
		gl.glBindTexture(GL.GL_TEXTURE_2D, texturePointer);
		
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, texture.getByteBuffer());
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        
        // Muß bei ATI Grafikkarten nachgeladen werden...
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		
		texture.setTexturePointer(texturePointer);
	}

	private void initFont(GL gl, int textureIndex)
	{
		GlFont glFont = GlFont.getInstance();
		
		if (!glFont.size())
		{
			ImageResource[] values = ImageResource.values();
			
			for (ImageResource resource : values)
			{
				String letter = resource.name();
				if (letter.startsWith("Letter"))
				{
					Texture texture = new GlCharacter(resource).getTexture();
					bindTexture(gl, texture, textureIndex++);
					glFont.addCharacter(texture);
				}
			}
		}
	}

	/** ---------- BEGINN: GLEventListener Implementierungen ----------- */
	
	public void display(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
        
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
        gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable (GL.GL_BLEND);
        
		if (menuChanged)
		{
			initTextures(gl);
			menuChanged = false;
		}
        
		for (GlComponent component : components)
		{
			component.display(drawable);
		}
        
		fps++;
		if (System.currentTimeMillis() > time + 1000)
		{
		    time = System.currentTimeMillis();
            owner.setTitle("TestMenü [" + fps + " fps]");
		    fps = 0;
		}
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
		System.out.println("GlMenuEventListener.displayChanged()");
	}

	public void init(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		
		// TODO_JAN nur zum Testen der Gl
		printGlInfo(gl);

		// L�schfarbe
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glShadeModel(GL.GL_SMOOTH);

		// Depth Buffer Setup
		gl.glClearDepth(1.0f);
		// Enables Depth Testing
		gl.glEnable(GL.GL_DEPTH_TEST);
		// The Type Of Depth Testing To Do
		gl.glDepthFunc(GL.GL_LEQUAL);
		// Really Nice Perspective Calculations
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		initTextures(gl);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		GL gl = drawable.getGL();
		GLU glu = new GLU();

		this.width = width;
		this.height = height;

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		// Urprung oben links
        glu.gluOrtho2D(0, this.width, this.height, 0);
	}
	
	/** ---------- ENDE: GLEventListener Implementierungen ----------- */
	
	/** ---------- BEGINN: MouseMotionListener Implementierungen ----------- */
	
	public void mouseDragged(MouseEvent e)
	{
	}
	
    public void mouseMoved(MouseEvent event)
    {
        int x = event.getX();
        int y = event.getY();
        
        for (GlComponent component : components)
        {
        	// TODO_JAN Vllt. später anders lösen... 
            boolean highlighted = component.isHighlighted();
            boolean touched = component.isTouched(x, y);
            
            if (touched && !highlighted && component instanceof GlButton)
            {
            	playSound(SoundOrMusicSource.MenuHighlight);
            } 
        }
    }
    
	/** ---------- ENDE: MouseMotionListener Implementierungen ----------- */
    
    /** ---------- BEGINN: MouseListener Implementierungen ----------- */
    
    public void mouseClicked(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        
        for (GlComponent component : components)
        {
            if (component.isTouched(x, y))
            {
                startAction(component);
            }
        }
    }

	public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }
    
    /** ---------- ENDE: MouseListener Implementierungen ----------- */
    
    /** ---------- ENDE: KeyListener Implementierungen ----------- */
    
    public void keyPressed(KeyEvent event)
    {
        int keyCode = event.getKeyCode();
        
        int arrowUp = 38;
        int arrowDown = 40;
        int enter = 10;
        int backSpace = 8;
        int space = 32;
        
        boolean isNumber = (keyCode >= 48 && keyCode <= 57);
        boolean isNumPadNumber = keyCode >= 96 && keyCode <= 105;
        boolean isLetter = keyCode >= 65 && keyCode <= 90;
        boolean isSpecialKey = keyCode == space || keyCode == backSpace;
        
		boolean isWritingAction = isNumber || isNumPadNumber || isLetter || isSpecialKey;
        
//        System.out.println(keyCode);
        if (isWritingAction && menu instanceof InputMenu)
        {
       		((InputMenu) menu).processUserInput(keyCode);
        }
        else if (keyCode == enter && menu instanceof InputMenu)
        {
        	String text = ((InputMenu) menu).getText();
        	ProfilesMenu profilesMenu = new ProfilesMenu();
			owner.setMenu(profilesMenu);
			profilesMenu.addNewProfile(text);
        }
        else if (keyCode == enter && indexOfCurrentSelectedComponent > 0)
        {
            startAction(components.get(indexOfCurrentSelectedComponent));
        }
        else
        {
            int oldIndex = indexOfCurrentSelectedComponent;
            if (keyCode == arrowDown)
            {
                indexOfCurrentSelectedComponent++;
            }
            if (keyCode == arrowUp)
            {
                indexOfCurrentSelectedComponent--;
            }
            
            while (oldIndex != indexOfCurrentSelectedComponent)
            {
                if (indexOfCurrentSelectedComponent > components.size() - 1)
                {
                    indexOfCurrentSelectedComponent = 0;
                }
                else if (indexOfCurrentSelectedComponent < 0)
                {
                    indexOfCurrentSelectedComponent = components.size() - 1;
                }
                
                GlComponent component = components.get(indexOfCurrentSelectedComponent);
                if (component instanceof GlButton)
                {
                    ((GlButton) component).setSelected(true);
                    playSound(SoundOrMusicSource.MenuHighlight);
                    break;
                } 
                else if (keyCode == arrowDown)
                {
                    indexOfCurrentSelectedComponent++;
                } 
                else if (keyCode == arrowUp)
                {
                    indexOfCurrentSelectedComponent--;
                }
                
            }
            
            if (oldIndex != -1 && components.get(oldIndex) instanceof GlButton)
            {
                ((GlButton) components.get(oldIndex)).setSelected(false);
            }
        }
    }

    public void keyReleased(KeyEvent e)
    {
    }

    public void keyTyped(KeyEvent e)
    {
    }
    
    /** ---------- ENDE: KeyListener Implementierungen ----------- */
    
    private void startAction(GlComponent component)
    {
        String name = component.getName();
        
//        System.out.println(name);
        if (name.equals("PlayButton"))
        {
            // TODO Muß ja sicherlich noch ne Menge vorher passieren
            System.out.println("Start game:");
            owner.setVisible(false);
            // TODO_JULIUS Loopt der die ganze Zeit (also der Music-Player oder was kann man da noch lustiges machen...)
            stopSounds();
            // TODO_JULIUS Vllt. in BeforeGame verfrachten, damit die Musik startet, wenn alles fertig ist (siehe auch SpaceRacerGame)
            SoundPlayer inGameMusicPlayer = new LoopSoundPlayer(SoundOrMusicSource.IngameMusic);
            inGameMusicPlayer.play();
            
            try
            {
                BeforeGame bG =	new BeforeGame(7777, "Alice");
                new Thread(bG).start();
                bG.startGame();
            } catch (UnknownHostException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (UnableToCreateSocketException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (ClassNotFoundException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (UnableToStartGameException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
        }
        else if (name.equals("OptionButton"))
        {
            owner.setMenu(new OptionsMenu());
        }
        else if (name.equals("ProfileButton"))
        {
            owner.setMenu(new ProfilesMenu());
        }
        else if (name.equals("BackButton"))
        {
            if (menu instanceof OptionsMenu)
            {
                owner.setMenu(new MainMenu());
            } else
            {
                // TODO_JAN Menu-Baum aufspannen und dann klappts auch mitm Back-Button ;)  
                owner.setMenu(new OptionsMenu());
            }
        }
        else if (name.equals("CreditsButton"))
        {
        	/* HUFF-HACK:
        	 * CreditsController anwerfen
             */
            System.out.println("Starting credits:");
            owner.setVisible(false);
            
            stopSounds();
            
            CreditsController controller = new CreditsController(this);
            new Thread(controller).start();
            
            // Die Credits laufen nicht los, solange dieser Thread noch läuft.
            // Deshalb muss mit reActivate() wieder ins Menü zurückgekehrt werden.
        }
        else if (name.equals("CreateButton"))
        {
        	owner.setMenu(new InputMenu());
        }
        else if (name.equals("ExitButton"))
        {
        	stopSounds();
        	owner.shutDown();
        }
    }

    /* HUFF-HACK: 
     * Menü wieder in Gang bringen
     */
    public void reActivate()
    {
        // Sounds neu erzeugen und Menü wieder anzeigen
        sounds = new HashMap<SoundOrMusicSource, SoundPlayer>();
		sounds.put(SoundOrMusicSource.MenuMusic, new LoopSoundPlayer(SoundOrMusicSource.MenuMusic));
		sounds.put(SoundOrMusicSource.MenuHighlight, new SoundPlayer(SoundOrMusicSource.MenuHighlight));
        playSound(SoundOrMusicSource.MenuMusic);
        owner.setVisible(true);
    }
}
