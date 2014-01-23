
package edu.fhm.cs.spaceracing.net.udp;

import edu.fhm.cs.spaceracing.net.udp.UDPObjectConverter.EventObjectIDs;

/**
 * Interface a serializeable udp event object must implement!
 * 
 * @author bernhard
 *
 */
public interface UDPSerializeable
{

	EventObjectIDs getUDPEventObjectID();

	void udpSerialize(UDPSerializer serializer);

}
