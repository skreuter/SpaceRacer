package edu.fhm.cs.spaceracing.client.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import edu.fhm.cs.spaceracing.frames.components.GlButton;
import edu.fhm.cs.spaceracing.frames.components.GlComponent;
import edu.fhm.cs.spaceracing.frames.menus.GlMenu;

/**
 * Empfaengt und verarbeitet Befehle fuer das Menue.
 * 
 * @author Sebastian Gift / Jan Bouillon
 */
class MenuController implements IController, MouseListener, MouseMotionListener, KeyListener
{
	
	private GameManager gameManager;
		
    private int indexOfCurrentSelectedComponent = -1;
	
	private GlMenu currentMenu;
	private ArrayList<GlComponent> components;

	MenuController(GameManager gameManager)
	{
		this.gameManager = gameManager;
		initMenu();
	}
	
	private void initMenu()
	{
		if(currentMenu != null)
			components = currentMenu.getComponents();
	}

	/**
	 * Ruft das angeklickte Menue auf.
	 */
	public void mouseClicked(MouseEvent event)
	{
		int x = event.getX();
		int y = event.getY();
		
		for (GlComponent component : components)
		{
			if (component.isTouched(x, y))
			{
				startAction(component);
			}
		}
	}

	private void startAction(GlComponent component)
	{
		System.out.println(component.getName());
		if (component.getName().equals("PlayButton"))
		{
			gameManager.createGameScene();
		}
	}

	public void mouseEntered(MouseEvent arg0)
	{
		//Ich bin mir nicht sicher, ob diese Methode was bringt, wenn alle
		//Buttons nur Texturen sind
	}

	public void mouseExited(MouseEvent arg0)
	{
		//Unwichtig
	}

	public void mousePressed(MouseEvent arg0)
	{
		//Eventuell einen GlButton nochmal anders zeichnen, 
		//wenn gerade draufgeklickt wird?
	}

	public void mouseReleased(MouseEvent arg0)
	{
		//Unwichtig
	}

	public void mouseDragged(MouseEvent arg0)
	{
		//Unwichtig
	}

	public void mouseMoved(MouseEvent event)
	{
        int x = event.getX();
        int y = event.getY();
        
		for (GlComponent component : components)
        {
            component.isTouched(x, y);
        }
	}

	/**
	 * Erlaubt Navigation im Menue.
	 */
	public void keyPressed(KeyEvent event)
	{
        int keyCode = event.getKeyCode();
        
        int arrowUp = 38;
        int arrowDown = 40;
        
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

	public void keyReleased(KeyEvent arg0)
	{
		//Unwichtig
	}

	public void keyTyped(KeyEvent key)
	{
        int enter = 0;
        
		//Enter zum bestaetigen, Escape beendet das Menue/Spiel
		if(key.getKeyCode() == enter)
		{
			startAction(components.get(indexOfCurrentSelectedComponent));
		}
		
		if(key.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			gameManager.getWindow().dispose();
		}
	}
}
