package edu.fhm.cs.spaceracing.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

/**
 * Klasse zum laden und speichern von XML-Dateien.
 * 
 * @author Stefan Kreuter
 */
public class XML<T>
{
	private Class<T> clazz;
	
	public XML(Class<T> clazz)
	{
		this.clazz = clazz;
	}
	
	/**
	 * Gibt ein Objekt als XML-String zurück.
	 * @param object das Objekt
	 * @return XML-String des Objekts
	 */
	public String toXML(T object)
	{
		XStream xstream = new XStream();
		
		Annotations.configureAliases(xstream, clazz);
		
		return xstream.toXML(object);
	}
	
	/**
	 * Speichert ein Objekt als XML-String ein eine Datei.
	 * @param file die Datei
	 * @param object das Objekt
	 */
	public void toXMLFile(File file, T object)
	{
		try
		{
			XStream xstream = new XStream();
			
			Annotations.configureAliases(xstream, clazz);
			
			xstream.toXML(object, new FileWriter(file));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Lädt ein Objekt aus einem XML-String.
	 * @param xml der XML-String
	 * @return Objekt aus XML-String
	 */
	@SuppressWarnings("unchecked")
	public T fromXML(String xml)
	{
		XStream xstream = new XStream();
		
		Annotations.configureAliases(xstream, clazz);
		
		return (T) xstream.fromXML(xml);
	}
	
	/**
	 * Lädt ein Objekt aus einem XML-String aus einer Datei.
	 * @param file die Datei
	 * @return Objekt aus XML-String
	 */
	@SuppressWarnings("unchecked")
	public T fromXML(File file)
	{
		try 
		{
			XStream xstream = new XStream();
			
			Annotations.configureAliases(xstream, clazz);
			
			return (T) xstream.fromXML(new FileReader(file));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
