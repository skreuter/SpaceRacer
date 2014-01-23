package edu.fhm.cs.spaceracing.frames.utils;

import java.util.ArrayList;

/**
 * Verwaltet den Font für das Spiel. <br>
 * Jeder Buchstabe und Ziffer hat eine Textur.<br>
 * 
 * @author Jan Bouillon
 *
 */
public class GlFont
{
	
	private static final GlFont glFont = new GlFont();
	
	private ArrayList<Texture> characterTextures;
	
	// TODO_JAN Singleton-Konstrukt aufgeben
	private GlFont()
	{
		characterTextures = new ArrayList<Texture>();
	}

	public static GlFont getInstance()
	{
		return glFont;
	}

	/**
	 * Fügt eine neue Textur hinzu, sofern sie nicht <code>null</code> ist.
	 * 
	 * @param texture Die Textur
	 */
	public void addCharacter(Texture texture)
	{
		// TODO_JAN Dürfen mehrmals gleiche Texturen rein? Wohl nicht, also anderen Datentyp für GlFont 
		if (texture != null)// && characterTextures.indexOf(texture) == -1)
		{
			characterTextures.add(texture);
		}
	}

	public boolean size()
	{
		return characterTextures.size() > 0;
	}

	/**
	 * Liefert für den Character die entsprechende Textur. 
	 * 
	 * @param character Der Character
	 * @return Die entsprechende Textur
	 */
	public Texture getCharacterTexture(char character)
	{
		int charValue = (int) character;
		Texture texture = null;
		
		// Zahlen
		if (charValue >= 48 && charValue <= 57)
		{
			texture =  characterTextures.get(charValue-48);
		}
		// Grossbuchstaben
		else if (charValue >= 65 && charValue <= 90)
		{
			texture = characterTextures.get(charValue - 55);
		}
		
		return texture;
	}

	public int getCharacterHeight()
	{
		return characterTextures.get(0).getHeight();
	}

	public void removeAll()
	{
		characterTextures = new ArrayList<Texture>();
	}
}
