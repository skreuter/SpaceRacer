package edu.fhm.cs.spaceracing.frames.menus;

import edu.fhm.cs.spaceracing.frames.components.GlButton;
import edu.fhm.cs.spaceracing.frames.layout.GlBorderLayout.BorderOrientation;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;

/**
 * Das Optionenmenü im Spiel
 * 
 * @author Jan Bouillon
 *
 */
public class OptionsMenu extends DefaultMenu
{

    public OptionsMenu()
    {
        
        // buttons (central)
        GlButton profileButton = new GlButton(ImageResource.ProfileButton);
        addComponent(profileButton, BorderOrientation.Center);
        
        GlButton controlsButton = new GlButton(ImageResource.ControlsButton);
        addComponent(controlsButton, BorderOrientation.Center);
        
        GlButton soundButton = new GlButton(ImageResource.SoundButton);
        addComponent(soundButton, BorderOrientation.Center);
        
        GlButton graphicsButton = new GlButton(ImageResource.GraphicsButton);
        addComponent(graphicsButton, BorderOrientation.Center);
        
    }

}
