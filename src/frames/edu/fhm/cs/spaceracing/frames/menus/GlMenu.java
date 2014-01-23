package edu.fhm.cs.spaceracing.frames.menus;

import java.awt.Dimension;
import java.awt.Point;

import edu.fhm.cs.spaceracing.frames.components.GlContainer;

public class GlMenu extends GlContainer
{
    
	public GlMenu()
	{
		super(null, new Point(0,0));
        
        // TODO_JAN beseitigen: böser hack wegen kleinerer canvas
        setSize(new Dimension(1016, 741));
//		setSize(MenuFrame.resolution);
	}
}
