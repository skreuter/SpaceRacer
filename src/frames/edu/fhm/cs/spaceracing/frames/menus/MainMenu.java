package edu.fhm.cs.spaceracing.frames.menus;

import edu.fhm.cs.spaceracing.frames.components.GlButton;
import edu.fhm.cs.spaceracing.frames.layout.GlBorderLayout.BorderOrientation;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;

/**
 * Das Hauptmenü im Spiel
 * 
 * @author Jan Bouillon
 *
 */
public class MainMenu extends DefaultMenu
{

    public MainMenu()
	{
    	super(false);
    	
        // buttons (central)
		GlButton playButton = new GlButton(ImageResource.PlayButton);
		addComponent(playButton, BorderOrientation.Center);
		
		GlButton optionButton = new GlButton(ImageResource.OptionButton);
		addComponent(optionButton, BorderOrientation.Center);
		
	}
}
