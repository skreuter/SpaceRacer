package edu.fhm.cs.spaceracing.model.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Konfigurations-Klasse des Space-Racers.
 * 
 * @author Stefan Kreuter
 */
@XStreamAlias("config")
public class Configuration
{
	private int antiAliasing = 0;
	private int anisotropic = -1;
	private float particleDensityFactor = 1.0f;
	private boolean lowPolyModels = false;
	private boolean occlusionTest = true;
	private boolean fullscreen = false;
	private boolean showMenu = true;
	private boolean playMusic = true;
	private boolean limitFPS = true;
	private boolean drawCollisionSpheres = false;
	private boolean debug = false;
	private boolean logging = false;
	
	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public boolean isLimitFPS()
	{
		return limitFPS;
	}

	public void setLimitFPS(boolean limitFPS)
	{
		this.limitFPS = limitFPS;
	}

	public int getAntiAliasing()
	{
		return antiAliasing;
	}

	public void setAntiAliasing(int antiAliasing)
	{
		this.antiAliasing = antiAliasing;
	}

	public int getAnisotropic()
	{
		return anisotropic;
	}

	public void setAnisotropic(int anisotropic)
	{
		this.anisotropic = anisotropic;
	}

	public boolean isLowPolyModels()
	{
		return lowPolyModels;
	}

	public void setLowPolyModels(boolean lowPolyModels)
	{
		this.lowPolyModels = lowPolyModels;
	}

	public boolean isOcclusionTest()
	{
		return occlusionTest;
	}

	public void setOcclusionTest(boolean occlusionTest)
	{
		this.occlusionTest = occlusionTest;
	}

	public boolean isPlayMusic()
	{
		return playMusic;
	}

	public void setPlayMusic(boolean playMusic)
	{
		this.playMusic = playMusic;
	}

	public boolean isShowMenu()
	{
		return showMenu;
	}

	public void setShowMenu(boolean showMenu)
	{
		this.showMenu = showMenu;
	}

	public boolean isFullscreen()
	{
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen)
	{
		this.fullscreen = fullscreen;
	}

	public float getParticleDensityFactor()
	{
		return particleDensityFactor;
	}

	public void setParticleDensityFactor(float particleDensityFactor)
	{
		this.particleDensityFactor = particleDensityFactor;
	}

	public boolean isLogging()
	{
		return logging;
	}
	
	public void setLogging(boolean logging)
	{
		this.logging = logging;
	}

	public boolean isDrawCollisionSpheres()
	{
		return drawCollisionSpheres;
	}

	public void setDrawCollisionSpheres(boolean drawCollisionSpheres)
	{
		this.drawCollisionSpheres = drawCollisionSpheres;
	}
}
