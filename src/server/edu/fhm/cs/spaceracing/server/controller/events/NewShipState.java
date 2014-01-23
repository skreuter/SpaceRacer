package edu.fhm.cs.spaceracing.server.controller.events;

import edu.fhm.cs.spaceracing.model.generic.Vector;
import edu.fhm.cs.spaceracing.model.ship.Ship;
import edu.fhm.cs.spaceracing.net.udp.UDPSerializeable;
import edu.fhm.cs.spaceracing.net.udp.UDPSerializer;
import edu.fhm.cs.spaceracing.net.udp.UDPUnserializer;
import edu.fhm.cs.spaceracing.net.udp.UDPObjectConverter.EventObjectIDs;

/** Diese Klasse holt Aenderung aus einem Ship-Objekt, speichert sie
 * und traegt sie in ein uebergebenes Ship-Objekt ein.
 * Die Klasse ist zusaetzlich UDP-uebertragbar.
 * 
 * @author Sebastian Gift
 */
public class NewShipState implements UDPSerializeable
{
	/** Id des Spielers um dessen Schiff es geht */
	private int playerId;
	
	/** Ring durch den der Spieler als naechstes muss */
	private int lastRing;
	
	/** Position serialisierbar */
	private double positionX;
	private double positionY;
	private double positionZ;
	/** Up-Vektor serialisierbar */
	private double upX;
	private double upY;
	private double upZ;
	/** Blickrichtung serialisierbar */
	private double directionX;
	private double directionY;
	private double directionZ;
	
	/** Flugrichtung serialisierbar */
	private double speedX;
	private double speedY;
	private double speedZ;
	
	/** Ship-Objekt aus dem Daten geholt oder in das sie transferiert werden */
	private Ship ship;
	
	public EventObjectIDs getUDPEventObjectID()
	{
		return EventObjectIDs.UDPOBJ_NEWSHIPSTATE;
	}
	
	public void udpSerialize(UDPSerializer serializer)
	{
		serializer.putInt(getPlayerId());
		serializer.putInt(getLastRing());
		
		serializer.putDouble(getPositionX());
		serializer.putDouble(getPositionY());
		serializer.putDouble(getPositionZ());
		
		serializer.putDouble(getUpX());
		serializer.putDouble(getUpY());
		serializer.putDouble(getUpZ());
		
		serializer.putDouble(getDirectionX());
		serializer.putDouble(getDirectionY());
		serializer.putDouble(getDirectionZ());
		
		serializer.putDouble(getSpeedX());
		serializer.putDouble(getSpeedY());
		serializer.putDouble(getSpeedZ());
	}
	
	public void getStateFromShip()
	{
		if(ship == null)
		{
			return;
		}
		
		setPlayerId(ship.getPlayer().getId());
		setLastRing(ship.getPlayer().getLastCheckpointIndex());
		
		Vector position = ship.getPosition();
		Vector up = ship.getUp();
		Vector direction = ship.getDirection();
		Vector speed = ship.getSpeed();
		
		setPositionX(position.getX());
		setPositionY(position.getY());
		setPositionZ(position.getZ());
		
		setUpX(up.getX());
		setUpY(up.getY());
		setUpZ(up.getZ());
		
		setDirectionX(direction.getX());
		setDirectionY(direction.getY());
		setDirectionZ(direction.getZ());
		
		setSpeedX(speed.getX());
		setSpeedY(speed.getY());
		setSpeedZ(speed.getY());
	}
	
	public void insertStateIntoShip()
	{
		Ship ship = getShip();
		
		if(ship == null)
		{
			return;
		}
		
		if(!(ship.getPlayer().getId() == getPlayerId()))
		{
			return;
		}
		
		ship.getPlayer().setLastCheckpointIndex(getLastRing());
		
		Vector position = new Vector(getPositionX(), getPositionY(), getPositionZ());
		Vector up = new Vector(getUpX(), getUpY(), getUpZ());
		Vector direction = new Vector(getDirectionX(), getDirectionY(), getDirectionZ());
		Vector speed = new Vector(getSpeedX(), getSpeedY(), getSpeedZ());
		
		ship.setPosition(position);
		ship.setUp(up);
		ship.setDirection(direction);
		ship.setSpeed(speed);
	}
	
	public static NewShipState udpUnserialize (UDPUnserializer unserializer) 
	{
		NewShipState newShipState = new NewShipState();
	
		
		newShipState.setPlayerId(unserializer.getInt());
		newShipState.setLastRing(unserializer.getInt());
		
		newShipState.setPositionX(unserializer.getDouble());
		newShipState.setPositionY(unserializer.getDouble());
		newShipState.setPositionZ(unserializer.getDouble());
		
		newShipState.setUpX(unserializer.getDouble());
		newShipState.setUpY(unserializer.getDouble());
		newShipState.setUpZ(unserializer.getDouble());
		
		newShipState.setDirectionX(unserializer.getDouble());
		newShipState.setDirectionY(unserializer.getDouble());
		newShipState.setDirectionZ(unserializer.getDouble());
		
		newShipState.setSpeedX(unserializer.getDouble());
		newShipState.setSpeedY(unserializer.getDouble());
		newShipState.setSpeedZ(unserializer.getDouble());
		
		return newShipState;
	}

	public void setDirectionX(double directionX)
	{
		this.directionX = directionX;
	}

	public void setDirectionY(double directionY)
	{
		this.directionY = directionY;
	}

	public void setDirectionZ(double directionZ)
	{
		this.directionZ = directionZ;
	}

	public void setPlayerId(int playerId)
	{
		this.playerId = playerId;
	}

	public void setPositionX(double positionX)
	{
		this.positionX = positionX;
	}

	public void setPositionY(double positionY)
	{
		this.positionY = positionY;
	}

	public void setPositionZ(double positionZ)
	{
		this.positionZ = positionZ;
	}

	public void setShip(Ship ship)
	{
		this.ship = ship;
	}

	public void setUpX(double upX)
	{
		this.upX = upX;
	}

	public void setUpY(double upY)
	{
		this.upY = upY;
	}

	public void setUpZ(double upZ)
	{
		this.upZ = upZ;
	}

	private double getDirectionX()
	{
		return directionX;
	}

	private double getDirectionY()
	{
		return directionY;
	}

	private double getDirectionZ()
	{
		return directionZ;
	}

	public int getPlayerId()
	{
		return playerId;
	}

	private double getPositionX()
	{
		return positionX;
	}

	private double getPositionY()
	{
		return positionY;
	}

	private double getPositionZ()
	{
		return positionZ;
	}

	private Ship getShip()
	{
		return ship;
	}

	private double getUpX()
	{
		return upX;
	}

	private double getUpY()
	{
		return upY;
	}

	private double getUpZ()
	{
		return upZ;
	}

	private double getSpeedX()
	{
		return speedX;
	}

	private void setSpeedX(double speedX)
	{
		this.speedX = speedX;
	}

	private double getSpeedY()
	{
		return speedY;
	}

	private void setSpeedY(double speedY)
	{
		this.speedY = speedY;
	}

	private double getSpeedZ()
	{
		return speedZ;
	}

	private void setSpeedZ(double speedZ)
	{
		this.speedZ = speedZ;
	}

	private int getLastRing()
	{
		return lastRing;
	}

	private void setLastRing(int nextRing)
	{
		this.lastRing = nextRing;
	}
}