package edu.fhm.cs.spaceracing.frames.menus;

import java.awt.Dimension;

import edu.fhm.cs.spaceracing.frames.components.GlButton;
import edu.fhm.cs.spaceracing.frames.components.GlLabel;
import edu.fhm.cs.spaceracing.frames.components.GlPanel;
import edu.fhm.cs.spaceracing.frames.layout.GlSimpleLayout;
import edu.fhm.cs.spaceracing.frames.layout.GlBorderLayout.BorderOrientation;
import edu.fhm.cs.spaceracing.frames.layout.GlSimpleLayout.SimpleOrientation;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;

/**
 * Das Standardmenü im Spiel. <br>
 * Es wird eine Kopfzeile erzeugt, die oben links den Profilnamen und rechts (noch) nichts anzeigt.<br>
 * In der Fußzeile erscheint links der CreditsButton und rechts entweder ein BackButton, um zum vorhergehenden Menu zu wechseln,
 * oder eine ExitButton, um das Spiel zu verlassen. 
 * 
 * @author Jan Bouillon
 *
 */
public class DefaultMenu extends GlMenu
{
    
	/**
	 * Ein Menu mit Kopf- und Fußzeile und einem Back-Button in der Fußzeile
	 */
	public DefaultMenu()
    {
		this(true);
    }
	
	/**
	 * Ein Menu mit Kopf- und Fußzeile. Weiterhin kann angegeben werden, unten rechts ein Back- oder ExitButton angezeigt werden soll.
	 * 
	 * @param backButtonAvailable
	 */
	public DefaultMenu(boolean backButtonAvailable)
	{
        // Menu-header
        GlPanel header = new GlPanel();
        header.setSize(new Dimension(width, height/4));
        header.setName("Header");
        header.setLayout(new GlSimpleLayout());
        
        // TODO_JAN hier den Namen aus dem aktuellen Profil ziehen!!!
        GlLabel profileLabel = new GlLabel(ImageResource.ProfileLabel, "Player");
        header.addComponent(profileLabel, SimpleOrientation.leftTop);
        GlLabel emptyLabel = new GlLabel(ImageResource.EmptyLabel);
        
        // TODO_JAN manuelles TextOffset in Rechts-/Linksbündig oder Zentriert kapseln. 
        emptyLabel.setTextOffset(90);
        header.addComponent(emptyLabel, SimpleOrientation.rightTop);
        addComponent(header, BorderOrientation.North);
        
        
        // Menu-footer
        GlPanel footer = new GlPanel();
        footer.setSize(new Dimension(width, height/4));
        footer.setName("Footer");
        footer.setLayout(new GlSimpleLayout());
        
        GlButton creditsButton = new GlButton(ImageResource.CreditsButton);
        footer.addComponent(creditsButton, SimpleOrientation.leftBottom);
        
        GlButton exitButton;
        if (backButtonAvailable)
        {
        	exitButton = new GlButton(ImageResource.BackButton);
        }
        else
        {
        	exitButton = new GlButton(ImageResource.ExitButton);
        }
        
		footer.addComponent(exitButton, SimpleOrientation.rightBottom);
        addComponent(footer, BorderOrientation.South);
	}
}
