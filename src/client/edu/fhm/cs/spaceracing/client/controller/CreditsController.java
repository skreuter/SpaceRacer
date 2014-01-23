package edu.fhm.cs.spaceracing.client.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import edu.fhm.cs.spaceracing.client.sound.LoopSoundPlayer;
import edu.fhm.cs.spaceracing.client.sound.SoundOrMusicSource;
import edu.fhm.cs.spaceracing.client.sound.SoundPlayer;
import edu.fhm.cs.spaceracing.client.view.CreditsView;
import edu.fhm.cs.spaceracing.client.view.ViewWindow;
import edu.fhm.cs.spaceracing.frames.listeners.GlMenuEventListener;
 
/**
 * Erzeugt ein Credits-Fenster und spielt die Musik ab.
 * @author Thomas "HUFFMAN" Brunner
 */
public class CreditsController implements Runnable
{
	private GlMenuEventListener menu;
	private ViewWindow window;
	private SoundPlayer introPlayer = new SoundPlayer(SoundOrMusicSource.CreditsIntro);
	private SoundPlayer loopPlayer = new LoopSoundPlayer(SoundOrMusicSource.CreditsLoop, 2);
	private SoundPlayer endingPlayer = new SoundPlayer(SoundOrMusicSource.CreditsEnding);
	private boolean stopped = false;

	/**
	 * Erzeugt einen neuen CreditsController.
	 * @param menu das aufrufende Menü, welches nach Ende der Credits reaktiviert wird.
	 */
	public CreditsController(GlMenuEventListener menu)
	{
		this.menu = menu;
	}
	
	/**
	 * Startet die Credits.
	 */
	public void run()
	{
		CreditsView credits = new CreditsView();
		window = new ViewWindow(credits, 1024, 768);

		WindowListener w = new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {endCredits();}
            public void windowClosed(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        };
        KeyListener k = new KeyListener() {
        	public void keyPressed(KeyEvent e) {}
        	public void keyReleased(KeyEvent e) {}
        	public void keyTyped(KeyEvent e) {
        		if(e.getKeyChar() == 27)				// Escape
        			endCredits();
        	}
        };
        
        window.addWindowListener(w);
        window.addKeyListener(k);
        window.getCanvas().addKeyListener(k);

        try
        {
	        while(!credits.isLoaded())
	        	Thread.sleep(20);
	
	        // Intro-Musik spielen
			introPlayer.play();
			Thread.sleep(2000);
			while(introPlayer.isPlaying())
				Thread.sleep(20);
			musicLoop();
        }
        catch(Exception e)
        {
        	throw new RuntimeException(e);
        }
	}
	
	// Loop-Musik abspielen
	private void musicLoop()
	{
		try 
		{
			loopPlayer.play();
			Thread.sleep(2000);				// Bug im MusikPlayer: kurz warten
			while(loopPlayer.isPlaying())
			{
				if(stopped)					// Nicht einen neuen Loop anfangen
					return;
				Thread.sleep(20);
			}
			
			endingPlayer.play();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	// Ende-Musik abspielen und Menü wieder anzeigen
	private void endCredits()
	{
		try
		{
			stopped = true;
			boolean wasPlaying = introPlayer.isPlaying() || loopPlayer.isPlaying();
			introPlayer.stop();
			loopPlayer.stop();
			if(wasPlaying)
			{
				endingPlayer.play();
	
				Thread.sleep(800);
				while(endingPlayer.isPlaying())
					Thread.sleep(20);
			}
			window.setVisible(false);
			window.dispose();
			menu.reActivate();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
