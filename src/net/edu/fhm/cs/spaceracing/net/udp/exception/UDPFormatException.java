
package edu.fhm.cs.spaceracing.net.udp.exception;

public class UDPFormatException extends Exception
{
	private final static long serialVersionUID = 20070905;

	public UDPFormatException(String reason)
	{
		super(reason);
	}
}
