package edu.fhm.cs.spaceracing.client.view.hud;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import edu.fhm.cs.spaceracing.frames.components.GlCharacter;
import edu.fhm.cs.spaceracing.frames.components.GlComponent;
import edu.fhm.cs.spaceracing.frames.components.GlLabel;
import edu.fhm.cs.spaceracing.frames.utils.GlFont;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;
import edu.fhm.cs.spaceracing.frames.utils.Texture;

/**
 * Ein HUD
 * @author Jan Bouillon
 */
public class GameHUD extends HUD
{
	
	private ArrayList<GlLabel> labels;
	private int[] textures;

	public GameHUD()
	{
		labels = new ArrayList<GlLabel>();

		GlLabel speed = new GlLabel(ImageResource.SpeedLabel);
		speed.setAnchor(new Point(0, 768 - speed.getHeight()));
		speed.setTextOffset(175);
		labels.add(speed);
		
		GlLabel rank = new GlLabel(ImageResource.RankLabel);
//		rank.setAnchor(new Point(1024/2 - rank.getWidth()/2, 768 - rank.getHeight()));
		rank.setAnchor(new Point(1024 - rank.getWidth(), 768 - rank.getHeight()));
		rank.setTextOffset(175);
		labels.add(rank);
		
//		GlLabel armor = new GlLabel(ImageResource.ArmorLabel);
//		armor.setAnchor(new Point(1024 - armor.getWidth(), 768 - armor.getHeight()));
//		armor.setTextOffset(175);
//		labels.add(armor);
	}
	
	public void setSpeed(int speed)
	{
		GlLabel speedLabel = labels.get(0);
		speedLabel.setText("" + Math.abs(speed), 175);
	}

	private void initTextures(GL gl)
	{
		textures = new int[labels.size() + 36];
		gl.glGenTextures(textures.length, textures, 0);
		
		int textureIndex = 0;
		for (GlComponent label : labels)
		{
			Texture texture = label.getTexture();
			bindTexture(gl, texture, textureIndex++);
			
            System.out.println(label + " loaded.");
		}
		
		initFont(gl, textureIndex);
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	private void initFont(GL gl, int textureIndex)
	{
		GlFont glFont = GlFont.getInstance();
		glFont.removeAll();
		
		if (!glFont.size())
		{
			ImageResource[] values = ImageResource.values();
			
			for (ImageResource resource : values)
			{
				String letter = resource.name();
				if (letter.startsWith("Letter"))
				{
					Texture texture = new GlCharacter(resource).getTexture();
					bindTexture(gl, texture, textureIndex++);
					glFont.addCharacter(texture);
				}
			}
		}
	}

	private void bindTexture(GL gl, Texture texture, int indexOfCurrentComponent)
	{
		int texturePointer = textures[indexOfCurrentComponent];
		gl.glBindTexture(GL.GL_TEXTURE_2D, texturePointer);
		
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, texture.getWidth(), texture.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, texture.getByteBuffer());
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        
        // Muß bei ATI Grafikkarten nachgeladen werden...
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		
		texture.setTexturePointer(texturePointer);
	}

	@Override
	protected void drawContents(GLAutoDrawable autoDrawable, int resolutionX, int resolutionY)
	{
		GL gl = autoDrawable.getGL();
		GLU glu = new GLU();
		
		if (textures == null)
		{
			initTextures(gl);
		}
		
		gl.glDisable(GL.GL_DEPTH_TEST);					// Immer im Vordergrund, wenn zuletzt gezeichnet
		gl.glEnable(GL.GL_BLEND);
		
		// Urprung oben links
		glu.gluOrtho2D(0, 1024, 768, 0);
		for (GlLabel label : labels)
		{
			gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	        gl.glEnable (GL.GL_BLEND);
	        
	        label.display(autoDrawable);
		}
		
		gl.glDisable(GL.GL_BLEND);
		gl.glEnable(GL.GL_DEPTH_TEST);	
	}
}
