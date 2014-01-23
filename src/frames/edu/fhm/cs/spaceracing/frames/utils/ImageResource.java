package edu.fhm.cs.spaceracing.frames.utils;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Eine Enum für die Texturen.<br>
 * Ein Vorteil sind vordefinierten Konstruktoren. Sie ermöglichen einen besser lesbaren Code und ein einfacheres Refactoring (Kapseln der Pfade zu den Images).
 * 
 * @author Jan Bouillon
 *
 */
public enum ImageResource
{
    // GlButtons für DefaultMenu (Footer)
    CreditsButton("images/CreditsButton.png", "images/CreditsButtonHighlighted.png"),
    ExitButton("images/ExitButton.png", "images/ExitButtonHighlighted.png"), 
    BackButton("images/BackButton.png", "images/BackButtonHighlighted.png"),
    
    // GlButtons für MainMenu
    PlayButton("images/PlayButton.png", "images/PlayButtonHighlighted.png"),
    OptionButton("images/OptionsButton.png", "images/OptionsButtonHighlighted.png"),
    
    // GlButtons für OptionsMenu
    ProfileButton("images/ProfilesButton.png", "images/ProfilesButtonHighlighted.png"),
    ControlsButton("images/ControlsButton.png", "images/ControlsButtonHighlighted.png"),
    SoundButton("images/SoundButton.png", "images/SoundButtonHighlighted.png"),
    GraphicsButton("images/GraphicsButton.png", "images/GraphicsButtonHighlighted.png"),
    
    // GlButtons für ProfilesMenu
    CreateButton("images/CreateButton.png", "images/CreateButtonHighlighted.png"),
    EditButton("images/EditButton.png", "images/EditButtonHighlighted.png"),
    DeleteButton("images/DeleteButton.png", "images/DeleteButtonHighlighted.png"),
    
    // GlLabels für DefaultMenu (Header)
    ProfileLabel("images/ProfileLabel.png"),
    EmptyLabel("images/EmptyLabel.png"),
    
    // Frames (paintable GlPanels)
    ProfileListFrame("images/ProfileListFrame.png"),
    InputLabel("images/InputLabel.png"),
    BlackLabel("images/BlackLabel.png"),
    
    // HUD
    SpeedLabel("images/HUD/SpeedPanel.png"),
    RankLabel("images/HUD/RankPanel.png"),
    ArmorLabel("images/HUD/ArmorPanel.png"),
    
    // GlLetters
    LetterZero("images/letters/0_13x9.png"),
    LetterOne("images/letters/1_5x9.png"),
    LetterTwo("images/letters/2_25x9.png"),
    LetterThree("images/letters/3_25x9.png"),
    LetterFour("images/letters/4_27x9.png"),
    LetterFive("images/letters/5_25x9.png"),
    LetterSix("images/letters/6_25x9.png"),
    LetterSeven("images/letters/7_24x9.png"),
    LetterEight("images/letters/8_25x9.png"),
    LetterNine("images/letters/9_24x9.png"),
    LetterA("images/letters/A_27x9.png"),
    LetterB("images/letters/B_25x9.png"),
    LetterC("images/letters/C_25x9.png"),
    LetterD("images/letters/D_24x9.png"),
    LetterE("images/letters/E_25x9.png"),
    LetterF("images/letters/F_25x9.png"),
    LetterG("images/letters/G_25x9.png"),
    LetterH("images/letters/H_25x9.png"),
    LetterI("images/letters/I_3x9.png"),
    LetterJ("images/letters/J_18x9.png"),
    LetterK("images/letters/K_20x9.png"),
    LetterL("images/letters/L_14x9.png"),
    LetterM("images/letters/M_44x9.png"),
    LetterN("images/letters/N_25x9.png"),
    LetterO("images/letters/O_23x9.png"),
    LetterP("images/letters/P_25x9.png"),
    LetterQ("images/letters/Q_26x9.png"),
    LetterR("images/letters/R_24x9.png"),
    LetterS("images/letters/S_24x9.png"),
    LetterT("images/letters/T_15x9.png"),
    LetterU("images/letters/U_25x9.png"),
    LetterV("images/letters/V_26x9.png"),
    LetterW("images/letters/W_44x9.png"),
    LetterX("images/letters/X_32x9.png"),
    LetterY("images/letters/Y_25x9.png"),
    LetterZ("images/letters/Z_25x9.png");
    
    private Resource resource;

    /**
     * Konstruktor für Ressourcen mit zwei Texturen.
     * 
     * @param textureFilename Standardtextur
     * @param highlightedTextureFilename Zusätzliche Textur
     */
    ImageResource(String textureFilename, String highlightedTextureFilename)
    {
        Texture texture = loadTexture(new File(textureFilename));
        Texture highlightedTexture = loadTexture(new File(highlightedTextureFilename));
        
        resource = new ButtonResource(texture, highlightedTexture);
    }

    /**
     * Konstruktor für Ressourcen mit einer Textur.
     * 
     * @param textureFilename Standardtextur
     */
    ImageResource(String textureFilename)
    {
        Texture texture = loadTexture(new File(textureFilename));
        
        resource = new Resource(texture);
    }

    /**
     * Lädt aus einer Datei ein Standardimage, modifiziert dieses und erzeugt einen {@link ByteBuffer} für spätere Anzeige über JOGL.<br>
     * Bisher werden folgende Formate (sicher) unterstützt:
     * <ul>
     * 	<li>*.png
     * </ul>
     * 
     * @param textureFile Quelldatei (PNG)
     * @return Die Textur (Die Rohdaten in einem {@link ByteBuffer} und die Größe der Textur in Pixel)
     */
    protected Texture loadTexture(File textureFile)
    {
        assert textureFile != null : "textureFile not set";
        assert textureFile.exists() : "Can not load image from file=\"" + textureFile.getAbsolutePath() + "\"";
        
        // Lädt das Image
        Image image = Toolkit.getDefaultToolkit().createImage(textureFile.getAbsolutePath());
        MediaTracker tracker = new MediaTracker(new Canvas());
        tracker.addImage (image, 0);
        try 
        {
            tracker.waitForAll();
        } 
        catch (InterruptedException exception) 
        {
            exception.printStackTrace();
        }
        
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        
        // Erzeugen eines modifizierten Images
        int transferType = DataBuffer.TYPE_BYTE;
        WritableRaster raster = Raster.createInterleavedRaster (transferType, width, height, 4, null);
        
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        int[] bits = new int[] {8,8,8,8};
        boolean hasAlpha = true;
        int transparency = Transparency.TRANSLUCENT;
        
        ComponentColorModel colorModel= 
        	new ComponentColorModel (colorSpace, bits, hasAlpha, false, transparency, transferType);            
        BufferedImage bufferedImage = new BufferedImage (colorModel, raster, false, null); 
        
        // Transformieren
        Graphics2D graphics2D = bufferedImage.createGraphics();
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.translate (0, height);
        affineTransform.scale (1, -1d);
        
        graphics2D.transform (affineTransform);
        graphics2D.drawImage (image, null, null);
        graphics2D.dispose();
        
        // ByteBuffer erzeugen
        byte[] dataOfImage = ((DataBufferByte)raster.getDataBuffer()).getData();
        
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(dataOfImage.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.put(dataOfImage, 0, dataOfImage.length);
        byteBuffer.position(0);
        
		return new Texture(byteBuffer, width, height);
    }

    public Resource getResource()
    {
        return resource;
    }

	@Override
	public String toString()
	{
		return this.name();
	}
    
    
}
