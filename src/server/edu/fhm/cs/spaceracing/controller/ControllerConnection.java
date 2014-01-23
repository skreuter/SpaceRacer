package edu.fhm.cs.spaceracing.controller;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

import edu.fhm.cs.spaceracing.model.config.ConfigurationManager;
import edu.fhm.cs.spaceracing.net.tcp.SynFIFO;
import edu.fhm.cs.spaceracing.net.udp.UDPPacketManager;
import edu.fhm.cs.spaceracing.net.udp.UDPSerializeable;
import edu.fhm.cs.spaceracing.net.udp.exception.UDPFormatException;
import edu.fhm.cs.spaceracing.server.controller.events.UnableToCreateSocketException;

/**
 * Empfaenger und Versender fuer alle Pakete des Controllers.
 * 
 * @author Sebastian Gift
 *
 * @param <R> Objekte, die der Controller empfaengt
 * @param <S> Objekte, die der Controller versendet
 */
public final class ControllerConnection<R, S extends UDPSerializeable>
{
	private final UDPPacketManager udpManager;
	private final SynFIFO<R> receiveFifo = new SynFIFO<R>();
	private final SynFIFO<S> sendFifo = new SynFIFO<S>();
	private final Set<SocketAddress> socketAddresses = new HashSet<SocketAddress>();
	private static final int MAX_PACKAGES_PER_RUN = 10;
	
	public ControllerConnection(int port) throws UnableToCreateSocketException
	{
		try
		{
			this.udpManager = new UDPPacketManager(port);
		} 
		catch (IOException e)
		{
			throw new UnableToCreateSocketException();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void run()
	{
		//Pakete empfangen und versenden
		for(int i = 0; i < ControllerConnection.MAX_PACKAGES_PER_RUN; i++)
		{
			boolean nothingReceived = false;
			boolean nothingSend = false;
			Object receivedObject = null;
			try
			{
				receivedObject = getUdpManager().getNextEventObject();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			} 
			catch (UDPFormatException e)
			{
				e.printStackTrace();
			}
			
			if(receivedObject != null)
			{
				//Verlaeszt sich darauf, dass die Gegenseite und die drunter liegende
				//Implementierung des Netzwerks nur korrekte Objekte verschicken
				R object = (R) receivedObject;
				add(object);
				
				if(ConfigurationManager.get().isLogging())
					System.out.println(object);
			}
			else
			{
				nothingReceived = true;
			}
			
			//Pro Runde ein Objekt raushauen an alle Clients
			UDPPacketManager udpManager = getUdpManager();
			UDPSerializeable object = this.sendFifo.take();
			if(object != null)
			{
				for(SocketAddress socketAddress: this.socketAddresses)
				{
					udpManager.addEventObject(socketAddress, object);
				}
				
				try
				{
					udpManager.flushEventObjectQue();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				nothingSend = true;
			}
			
			if(nothingReceived && nothingSend)
			{
				return;
			}
		}
	}
	
	private UDPPacketManager getUdpManager()
	{
		return udpManager;
	}
	
	public void addClient(SocketAddress socketAddress)
	{
		getUdpManager().registerNewClient(socketAddress);
		//Wird zum verschicken von Paketen gebraucht
		this.socketAddresses.add(socketAddress);
	}
	
	private void add(R object)
	{
		this.receiveFifo.add(object);
	}
	
	public R take()
	{
		return this.receiveFifo.take();
	}
	
	public void put(S object)
	{
		this.sendFifo.add(object);
	}
}
