package edu.fhm.cs.spaceracing.frames.menus;

import edu.fhm.cs.spaceracing.frames.components.GlLabel;
import edu.fhm.cs.spaceracing.frames.layout.GlBorderLayout.BorderOrientation;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;

public class FinishScreen extends GlMenu
{

	public FinishScreen(String winner)
	{
		addComponent(new GlLabel(ImageResource.BlackLabel, "Spieler "+ winner +" hat gewonnen"), BorderOrientation.Center);
	}

}
