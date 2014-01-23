package edu.fhm.cs.spaceracing.model.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.fhm.cs.spaceracing.model.XML;
import edu.fhm.cs.spaceracing.model.level.CircleLevel;
import edu.fhm.cs.spaceracing.model.level.EightLevel;

/**
 * Central class for all data that needs to be stored before the game actually
 * runs.
 * 
 * The Context is available during the entire execution of the game also during
 * the menu phase.
 * 
 * @author christian.knuechel@gmx.de
 */
public class Context
{
	private static final String CONTEXT_FILE = "context.xml";
	
	/*
	 * persistent data
	 */
	
	private ContextData data;
	
	/*
	 * volatile data
	 */
	
	private List<LevelDescriptor> levels = new ArrayList<LevelDescriptor>();
	
	private int selectedLevelIndex;
	
	public Context()
	{
		levels.add(new LevelDescriptor(CircleLevel.ID, "Die runde Hoelle"));
		levels.add(new LevelDescriptor(EightLevel.ID, "Die krumme Acht"));
	}
	
	public void load()
	{
		File file = new File(CONTEXT_FILE);
		
		if(file.exists())
		{
			data = new XML<ContextData>(ContextData.class).fromXML(file);
		}
		else
		{
			data = new ContextData();
		}
	}
	
	public void save()
	{
		File file = new File(CONTEXT_FILE);
		
		new XML<ContextData>(ContextData.class).toXMLFile(file, data);
	}
	
	public List<Profile> getProfiles()
	{
		return data.profiles;
	}
	
	public void addProfile(Profile profile)
	{
		data.profiles.add(profile);
	}
	
	public Highscore getHighscore()
	{
		return data.highscore;
	}
	
	public List<LevelDescriptor> getLevels()
	{
		return levels;
	}
	
	public void setSelectedLevelIndex(int selectedLevelIndex)
	{
		this.selectedLevelIndex = selectedLevelIndex;
	}

	public int getSelectedLevelIndex()
	{
		return selectedLevelIndex;
	}
}
