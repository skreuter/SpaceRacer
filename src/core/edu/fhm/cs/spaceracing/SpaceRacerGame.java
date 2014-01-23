/**
 * Projekt:    Space-Racer
 * FWP-Fach:   Programmieren eines Computerspiels
 * Hochschule: Fachhochschule München 2007
 * 
 * Released under the terms of the GNU GPL v2.0.
 */
package edu.fhm.cs.spaceracing;

import javax.swing.SwingUtilities;

import edu.fhm.cs.spaceracing.client.controller.BeforeGame;
import edu.fhm.cs.spaceracing.client.sound.LoopSoundPlayer;
import edu.fhm.cs.spaceracing.client.sound.SoundOrMusicSource;
import edu.fhm.cs.spaceracing.client.sound.SoundPlayer;
import edu.fhm.cs.spaceracing.frames.MenuFrame;
import edu.fhm.cs.spaceracing.frames.menus.MainMenu;
import edu.fhm.cs.spaceracing.model.config.Configuration;
import edu.fhm.cs.spaceracing.model.config.ConfigurationManager;
import edu.fhm.cs.spaceracing.model.context.Context;

/**
 * Startklasse des Spiels: main(...)
 * @author Stefan Kreuter
 */
public class SpaceRacerGame implements StartGameListener
{
    
	private SoundPlayer ingameMusicPlayer = new LoopSoundPlayer(SoundOrMusicSource.IngameMusic);

    private Configuration configuration;
    private Context context = new Context();
	
	/**
	 * Hier wird das Spiel gestartet.
	 * - Profil auswählen, erstellen
	 * - Server/Client starten...
	 */
	public SpaceRacerGame()
	{
		configuration = ConfigurationManager.load();
		context.load();
        
        // TODO entweder MainMenu oder ProfileMenu, falls absoluter Erststart
        if(configuration.isShowMenu())
        {
            final MenuFrame mainFrame = new MenuFrame();
	        
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					mainFrame.setVisible(true);
				}
			});
			
			MainMenu mainMenu = new MainMenu();
	        mainFrame.setMenu(mainMenu);
		}
		else
        {
            startGame();
        }

        // Profil auswählen, erstellen
		// Server/Client starten...
        
        context.save();
        ConfigurationManager.save();
	}
	
	/**
	 * @see edu.fhm.cs.spaceracing.StartGameListener#startGame()
	 */
	public void startGame()
	{
		try
		{
			// TODO selbe Überlegung machen wie in GlMenuEventListener.startAction (beim PlayButton)
            ingameMusicPlayer.play();
            BeforeGame bG =	new BeforeGame(7777, "Alice");
            new Thread(bG).start();
            bG.startGame();
        }
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args bisher keine Funktion!
	 */
	public static void main(String[] args)
	{
		new SpaceRacerGame();
	}
	
}
