package edu.fhm.cs.spaceracing.model.config;

import java.io.File;

import edu.fhm.cs.spaceracing.model.XML;

/**
 * Static class to manage the singleton Configuration object. Provides
 * functionaliyt for loading and saving the configuration.
 * 
 * The configuration can be accesed anywhere with the {@link #get()} method.
 * 
 * @author christian.knuechel@gmx.de
 */
public class ConfigurationManager
{
	private static final String CONFIGURATION_FILE = "config.xml";
	
	// singleton
	private static Configuration configuration = null;
	
	public static Configuration load()
	{
		File file = new File(CONFIGURATION_FILE);
		
		if(file.exists())
		{
			try 
			{
				configuration = new XML<Configuration>(Configuration.class).fromXML(file);
			}
			catch(RuntimeException ex)
			{
				configuration = null;
			}
		}
		
		if(configuration == null)
			configuration = new Configuration();
		
		return configuration;
	}
	
	public static void save()
	{
		File file = new File(CONFIGURATION_FILE);
		
		new XML<Configuration>(Configuration.class).toXMLFile(file, configuration);
	}
	
	public static Configuration get()
	{
		return configuration;
	}
}
