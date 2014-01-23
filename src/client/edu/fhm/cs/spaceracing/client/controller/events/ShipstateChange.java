package edu.fhm.cs.spaceracing.client.controller.events;

import edu.fhm.cs.spaceracing.controller.IShipstateChange;
import edu.fhm.cs.spaceracing.net.udp.UDPSerializeable;
import edu.fhm.cs.spaceracing.net.udp.UDPSerializer;
import edu.fhm.cs.spaceracing.net.udp.UDPUnserializer;
import edu.fhm.cs.spaceracing.net.udp.UDPObjectConverter.EventObjectIDs;

/**
 * Container fuer Aenderungen des Schiffszustands.
 * Kann an einen Server uebertragen werden.
 * 
 * @author Sebastian Gift
 */
public class ShipstateChange implements IShipstateChange, UDPSerializeable
{
	private int forwardThrustChange = 0;
	private int sidewaysThrustChange = 0;
	private boolean fired = false;
	private double xRotation = 0.0;
	private double yRotation = 0.0;
	private int rollingThrustChange = 0;
	private int playerId;

	public ShipstateChange(int playerId)
	{
		assert playerId > -1;
		this.playerId = playerId;
	}
	
	public int getForwardThrustChange()
	{
		return forwardThrustChange;
	}

	public int getSidewaysThrustChange()
	{
		return sidewaysThrustChange;
	}

	public boolean isFired()
	{
		return fired;
	}

	public void setFired(boolean fired)
	{
		this.fired = fired;
	}

	public void setForwardThrustChange(int forwardThrustChange)
	{
		this.forwardThrustChange = forwardThrustChange;
		
		if(this.forwardThrustChange > 30)
			this.forwardThrustChange = 30;
		if(this.forwardThrustChange < -30)
			this.forwardThrustChange = -30;
	}

	public void setSidewaysThrustChange(int sidewaysThrustChange)
	{
		this.sidewaysThrustChange = sidewaysThrustChange;
	}

	public double getXRotation()
	{
		return xRotation;
	}

	public void setXRotation(double xRotation)
	{

		this.xRotation = xRotation;
	}
	
	public double getYRotation()
	{
		return yRotation;
	}

	public void setYRotation(double yRotation)
	{

		this.yRotation = yRotation;
	}

	public int getRollingThrustChange()
	{
		return rollingThrustChange;
	}

	public void setRollingThrustChange(int rollingThrustChange)
	{
		this.rollingThrustChange = rollingThrustChange;
	}
	
	public EventObjectIDs getUDPEventObjectID() {
		return EventObjectIDs.UDPOBJ_SHIPSTATECHANGE;
	}

	public void udpSerialize(UDPSerializer serializer) 
	{
		// Ship serialisieren
		serializer.putInt(playerId);
		
		// Eigene Datenelemente Serialisieren
		serializer.putInt(forwardThrustChange);
		serializer.putInt(sidewaysThrustChange);
		serializer.put(fired ? (byte)1 : (byte)0);
		serializer.putDouble(xRotation);
		serializer.putDouble(yRotation);
		serializer.putInt(rollingThrustChange);
		serializer.putInt(playerId);
		
		
	}
	
	public static ShipstateChange udpUnserialize (UDPUnserializer unserializer) 
	{
		ShipstateChange robj = null;

		robj = new ShipstateChange ( unserializer.getInt() );
		
		robj.forwardThrustChange = unserializer.getInt();
		robj.sidewaysThrustChange = unserializer.getInt();
		robj.fired = (unserializer.get() != 0);
		robj.xRotation = unserializer.getDouble();
		robj.yRotation = unserializer.getDouble();
		robj.rollingThrustChange = unserializer.getInt();
		robj.playerId = unserializer.getInt();
		
		return robj;
	}
	
	public void setPlayerId(int playerId)
	{
		this.playerId = playerId;
	}

	public int getPlayerId()
	{
		return playerId;
	}

	public void reset()
	{
		forwardThrustChange = 0;
		sidewaysThrustChange = 0;
		fired = false;
		xRotation = 0.0;
		yRotation = 0.0;
		rollingThrustChange = 0;		
	}
}