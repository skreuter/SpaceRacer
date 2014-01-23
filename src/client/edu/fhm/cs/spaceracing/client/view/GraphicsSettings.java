package edu.fhm.cs.spaceracing.client.view;

import edu.fhm.cs.spaceracing.model.config.Configuration;
import edu.fhm.cs.spaceracing.model.config.ConfigurationManager;

/**
 * Die Grafikeinstellungen des Space-Racer.
 * @author Thomas "HUFFMAN" Brunner
 */
public class GraphicsSettings
{
	/**
	 * Full-Screen-Multisampling (Kantenglättung).
	 * Erlaubte Werte: 0, 2, 4
	 */
	private static int multisamplingLevel = 4;
	
	/**
	 * Anisotrope Filterung.
	 * Erlaubte Werte: -1, 0, 2, 4, 8, 16 (je nach Hardware).
	 * -1 für Maximum
	 */
	private static int anisotropicLevel = -1;

	/**
	 * Polygonreduzierte Models verwenden.
	 */
	private static boolean isLowPolyModels = false;
	
	/**
	 * Hardware-Occlusion-Query für verdecktes Sonnenlicht aktivieren.
	 */
	private static boolean doOcclusionTest = true;
	
	/**
	 * Im Vollbild-Modus spielen.
	 */
	private static boolean isFullscreen = false;
	
	/**
	 * Partikeldichte: Je höher, umso mehr Partikel werden durch die 
	 * Antriebsspur erzeugt(glattere Darstellung). Muss >0.0f sein.
	 */
	private static float particleDensityFactor = 1.0f;
	
	/**
	 * FPS auf ca. 64 beschränken.
	 */
	private static boolean limitFPS = true;
	
	/**
	 * Die Kollisionskugeln der SpaceObjects einzeichnen.
	 */
	private static boolean drawCollisionSpheres = false;


	/**
	 * Lädt die Konfiguration aus der globalen SpaceRacer-Config.
	 */
	public static void load()
	{
		Configuration configuration = ConfigurationManager.get();
		
		int aa = configuration.getAntiAliasing();
		
		if(aa != 0 && aa != 2 && aa != 4)
			throw new IllegalArgumentException("Only AA levels of 0, 2, 4 are supported!");
		multisamplingLevel = aa;
		
		int af = configuration.getAnisotropic();
		if(af%2 != 0 && af != -1)
			throw new IllegalArgumentException("Only even AF levels are supported!");
		anisotropicLevel = af;
		
		float particleDensityFactor = configuration.getParticleDensityFactor();
		if(particleDensityFactor <= 0.0f)
			throw new IllegalArgumentException("Particle Density Factor must be > 0.0f!");
		GraphicsSettings.particleDensityFactor = particleDensityFactor;
		
		isLowPolyModels = configuration.isLowPolyModels();
		doOcclusionTest = configuration.isOcclusionTest();
		isFullscreen = configuration.isFullscreen();
		limitFPS = configuration.isLimitFPS();
		drawCollisionSpheres = configuration.isDrawCollisionSpheres();
	}

	public static int getAnisotropicLevel()
	{
		return anisotropicLevel;
	}

	public static boolean isFullscreen()
	{
		return isFullscreen;
	}

	public static boolean isLowPolyModels()
	{
		return isLowPolyModels;
	}

	public static boolean isDoOcclusionTest()
	{
		return doOcclusionTest;
	}

	public static boolean isLimitFPS()
	{
		return limitFPS;
	}

	public static int getMultisamplingLevel()
	{
		return multisamplingLevel;
	}

	public static float getParticleDensityFactor()
	{
		return particleDensityFactor;
	}
	
	public static boolean isDrawCollisionSpheres()
	{
		return drawCollisionSpheres;
	}
	
	public static void setDoOcclusionTest(boolean doOcclusionTest)
	{
		GraphicsSettings.doOcclusionTest = doOcclusionTest;
	}

}
