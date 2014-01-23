package edu.fhm.cs.spaceracing.frames.menus;

import java.awt.Point;
import java.util.ArrayList;

import edu.fhm.cs.spaceracing.frames.components.GlButton;
import edu.fhm.cs.spaceracing.frames.components.GlLabel;
import edu.fhm.cs.spaceracing.frames.components.GlPanel;
import edu.fhm.cs.spaceracing.frames.layout.GlBorderLayout.BorderOrientation;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;

/**
 * Das Profilemenü im Spiel
 * 
 * @author Jan Bouillon
 *
 */
public class ProfilesMenu extends DefaultMenu
{
	
	private ArrayList<GlLabel> listItems;
	private GlPanel profileListFrame;

    public ProfilesMenu()
    {
    	listItems = new ArrayList<GlLabel>();
    	
    	profileListFrame = new GlPanel(ImageResource.ProfileListFrame);
		addComponent(profileListFrame, BorderOrientation.Center);
        
        // TODO_JAN hack durch viewports lösen... skaliert dann die "zu großen" Texturen runter... 
        Point a = profileListFrame.getAnchor();
        a.x -= 10;
        profileListFrame.setAnchor(a);
        addListItems();
        
        GlButton createButton = new GlButton(ImageResource.CreateButton);
        addComponent(createButton, BorderOrientation.East);
        
        GlButton editButton = new GlButton(ImageResource.EditButton);
        addComponent(editButton, BorderOrientation.East);
        
        GlButton deleteButton = new GlButton(ImageResource.DeleteButton);
        addComponent(deleteButton, BorderOrientation.East);
        
        // TODO_JAN Ersetzen: Back-Button nun unten rechts
//        GlButton backButton = new GlButton(ImageResource.BackButton);
//        addComponent(backButton, BorderOrientation.East);
    }

	private void addListItems()
	{
		for (GlLabel item : listItems)
		{
			profileListFrame.addComponent(item);//, BorderOrientation.North);
		}
	}

	public void addNewProfile(String newProfileName)
	{
		GlLabel newProfileLabel = new GlLabel(ImageResource.BlackLabel, newProfileName);
		newProfileLabel.setAnchor(new Point(18, 25));
		profileListFrame.addComponent(newProfileLabel);//, BorderOrientation.North);
	}

}
