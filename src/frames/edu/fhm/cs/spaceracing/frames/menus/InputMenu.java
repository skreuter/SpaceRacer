package edu.fhm.cs.spaceracing.frames.menus;

import edu.fhm.cs.spaceracing.frames.components.GlLabel;
import edu.fhm.cs.spaceracing.frames.layout.GlBorderLayout.BorderOrientation;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;

public class InputMenu extends GlMenu
{
	

	private GlLabel inputLabel;

	public InputMenu()
	{
		GlLabel infoLabel = new GlLabel(ImageResource.BlackLabel, "Enter a name");
		addComponent(infoLabel, BorderOrientation.Center);
		inputLabel = new GlLabel(ImageResource.InputLabel);
		addComponent(inputLabel, BorderOrientation.Center);
		
		
	}

	public void processUserInput(int keyCode)
	{
        int backSpace = 8;
		
        if (keyCode == backSpace)
        {
        	deleteLastUserInput();
        }
        else 
        {
        	inputLabel.append((char) keyCode);
        }
	}

	private void deleteLastUserInput()
	{
		StringBuffer currentText = new StringBuffer(inputLabel.getText());
		int characterIndex = currentText.length() - 1;
		if (characterIndex >= 0)
		{
			currentText.deleteCharAt(characterIndex);
		}
		inputLabel.setText(currentText.toString());
	}

	public String getText()
	{
		return inputLabel.getText();
	}
}
