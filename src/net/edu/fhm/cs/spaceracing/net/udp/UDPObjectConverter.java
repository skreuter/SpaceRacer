package edu.fhm.cs.spaceracing.net.udp;

import edu.fhm.cs.spaceracing.client.controller.events.ShipstateChange;
import edu.fhm.cs.spaceracing.net.udp.exception.UnknownUDPEventObjectException;
import edu.fhm.cs.spaceracing.server.controller.events.NewShipState;

/**
 * Convert serializeable objects. <br>
 * The most intressting parts here are the event objects ids and the unserialize method.
 * 
 * @author bernhard
 *
 */
public class UDPObjectConverter
{

	private final static EventObjectIDs ids[] = EventObjectIDs.values();

	/**
	 * IDs of valid event-objects
	 * 
	 */
	public enum EventObjectIDs
	{
		UDPOBJ_SHIPSTATECHANGE,
		UDPOBJ_NEWSHIPSTATE
	};

	public static void serialize(UDPSerializeable obj, UDPSerializer ser)
	{
		ser.putInt(obj.getUDPEventObjectID().ordinal());
		
		obj.udpSerialize(ser);
	}

	public static Object unserialize(UDPUnserializer unser)
			throws UnknownUDPEventObjectException
	{
		int objid = unser.getInt();
		
		if (objid < 0 || objid >= ids.length)
		{
			throw new UnknownUDPEventObjectException("EventObject ID out of range!");
		}

		switch (ids[objid])
		{
		case UDPOBJ_SHIPSTATECHANGE:
		{
			// Hier muss nun das Object instanziert werden und die Datenelemente
			// initialisiert werden
			return ShipstateChange.udpUnserialize(unser);
		}
		case UDPOBJ_NEWSHIPSTATE:
			return NewShipState.udpUnserialize(unser);
		}
		throw new UnknownUDPEventObjectException("Unhandled EventObject Case!");
	}
	
}
