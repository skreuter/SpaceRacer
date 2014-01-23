package edu.fhm.cs.spaceracing.client.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.Socket;
import java.net.SocketAddress;

import sun.misc.Timeable;
import sun.misc.Timer;
import edu.fhm.cs.spaceracing.client.controller.events.ShipstateChange;
import edu.fhm.cs.spaceracing.controller.ControllerConnection;
import edu.fhm.cs.spaceracing.controller.Physics;
import edu.fhm.cs.spaceracing.controller.UpdateModel;
import edu.fhm.cs.spaceracing.model.Game;
import edu.fhm.cs.spaceracing.model.config.ConfigurationManager;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.server.controller.events.NewShipState;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;
/**
 * Setzt Spielereingaben in interne Befehle um.
 * Die Befehle werden danach an die Physik und den Server weitergeleitet.
 * 
 * @author Sebastian Gift
 * @author Stefan Kreuter
 */
class ShipController implements KeyListener, MouseListener, MouseMotionListener, IController, Timeable
{
	private final static int THRUST_CHANGE_PER_EVENT = 1;
	private final int playerId;
	private final ShipstateChange shipstateChange;
	private final int width;
	private final int height;
	private final UpdateModel updateModel;
	private final Physics physics;
	private final GameLoop gameLoop;
	private final ControllerConnection<NewShipState, ShipstateChange> shipstateChangeConnector;
	private final Timer timer;
	
	ShipController(int playerId, Ship ship, int width, int height, Game game, Socket socket) throws UnableToCreateSocketException
	{
		assert playerId > -1;
		assert ship != null;
		assert width > 0;
		assert height > 0;
		assert game != null;
		assert socket != null;
		int port = socket.getLocalPort();
		SocketAddress serverAddress = socket.getRemoteSocketAddress();
		this.playerId = playerId;
		shipstateChange = new ShipstateChange(getPlayerId());
		this.width = width;
		this.height = height;
		physics = new Physics(ship, game);
		updateModel = new UpdateModel(shipstateChange, physics);
		//UDP-Pakete annehmen
		shipstateChangeConnector = new ControllerConnection<NewShipState, ShipstateChange>(port);
		shipstateChangeConnector.addClient(serverAddress);
		gameLoop = new GameLoop(game, physics, shipstateChangeConnector);
		new Thread(gameLoop).start();
		timer = new Timer(this, 100);
		timer.cont();
	}
	
	public void tick(Timer timer)
	{
		updateState();
	}
	
	public void keyPressed(KeyEvent key)
	{
		int keycode = key.getKeyCode();
		if(keycode == KeyEvent.VK_W)
		{
			shipstateChange.setForwardThrustChange(THRUST_CHANGE_PER_EVENT);
		}
		else if(keycode == KeyEvent.VK_S)
		{
			shipstateChange.setForwardThrustChange(-THRUST_CHANGE_PER_EVENT);
		}
		else if(keycode == KeyEvent.VK_A)
		{
			shipstateChange.setSidewaysThrustChange(-THRUST_CHANGE_PER_EVENT);
		}
		else if(keycode == KeyEvent.VK_D)
		{
			shipstateChange.setSidewaysThrustChange(+THRUST_CHANGE_PER_EVENT);			
		}
		updateState();
	}
	
	private void updateState()
	{
		if(!ConfigurationManager.get().isDebug())
		{
			getUpdateModel().updateChanges();
		}
		send(shipstateChange);
	}
	
	public void keyReleased(KeyEvent key)
	{
		int keycode = key.getKeyCode();
		if(keycode == KeyEvent.VK_W)
		{
			shipstateChange.setForwardThrustChange(0);
		}
		else if(keycode == KeyEvent.VK_S)
		{
			shipstateChange.setForwardThrustChange(0);
		}
		else if(keycode == KeyEvent.VK_A)
		{
			shipstateChange.setSidewaysThrustChange(0);
		}
		else if(keycode == KeyEvent.VK_D)
		{
			shipstateChange.setSidewaysThrustChange(0);			
		}
		else if(keycode == KeyEvent.VK_ESCAPE)
		{
			System.exit(1);
		}
	}

	public void keyTyped(KeyEvent key)
	{
		// Unbenutzt
	}

	/**
	 * Feuert eventuell vorhandene Waffen ab.
	 */
	public void mouseClicked(MouseEvent mouse)
	{
		if(mouse.getButton() == MouseEvent.BUTTON1)
		{
			shipstateChange.setFired(true);
			updateState();
		}
	}

	/**
	 * Dreht das Schiff in die richtige Richtung.
	 */
	public void mouseMoved(MouseEvent mouse)
	{
		//Wo sind wir?
		int x = mouse.getX();
        int y = mouse.getY();
        //Startpunkt ist immer der Mittelpunkt
        double mouseMotionStartX = getMouseMotionStartX();
        double mouseMotionStartY = getMouseMotionStartY();
        int width = getWidth();
        int height = getHeight();
        
        //Wohin wollen wir uns drehen
		double rotationY = (x - mouseMotionStartX)*180d/width;
        double rotationX = (y - mouseMotionStartY)*180d/height;
        
        shipstateChange.setXRotation(rotationX);
        shipstateChange.setYRotation(rotationY);
        
        updateState();
	}

	private double getMouseMotionStartX()
	{
		return getWidth() / 2d;
	}
	
	private double getMouseMotionStartY()
	{
		return getHeight() / 2d;
	}
	
	public void mouseEntered(MouseEvent arg0)
	{
		//Unbenutzt
	}

	public void mouseExited(MouseEvent arg0)
	{
		//Unbenutzt
	}

	public void mousePressed(MouseEvent arg0)
	{
		//Unbenutzt	
	}

	public void mouseReleased(MouseEvent arg0)
	{
		//Unbenutzt	
	}

	public void mouseDragged(MouseEvent arg0)
	{
		//Unbenutzt		
	}

	public int getPlayerId()
	{
		return playerId;
	}
	
	private int getWidth()
	{
		return width;
	}
	
	private int getHeight()
	{
		return height;
	}
	
	private UpdateModel getUpdateModel()
	{
		return updateModel;
	}
	
	private void send(ShipstateChange shipstateChange)
	{
		shipstateChangeConnector.put(shipstateChange);
	}
}