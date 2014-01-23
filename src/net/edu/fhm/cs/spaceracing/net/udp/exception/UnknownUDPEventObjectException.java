
package edu.fhm.cs.spaceracing.net.udp.exception;


public class UnknownUDPEventObjectException extends UDPFormatException
{
	private final static long serialVersionUID = 20070905;

	public UnknownUDPEventObjectException(String reason)
	{
		super(reason);
	}

}
