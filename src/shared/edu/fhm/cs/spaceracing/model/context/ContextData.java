package edu.fhm.cs.spaceracing.model.context;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Container class for all context data that is stored persistent in a file.
 * 
 * Is meant to be loaded and saved using the
 * {@link edu.fhm.cs.spaceracing.model.XML} class.
 */
@XStreamAlias("context")
class ContextData
{
	List<Profile> profiles = new ArrayList<Profile>();
	
	Highscore highscore = new Highscore();
}
