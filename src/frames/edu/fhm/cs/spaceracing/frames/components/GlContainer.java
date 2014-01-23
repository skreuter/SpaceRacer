package edu.fhm.cs.spaceracing.frames.components;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GLAutoDrawable;

import edu.fhm.cs.spaceracing.frames.layout.GlBorderLayout;
import edu.fhm.cs.spaceracing.frames.layout.GlLayout;
import edu.fhm.cs.spaceracing.frames.layout.Orientation;
import edu.fhm.cs.spaceracing.frames.utils.ImageResource;

/**
 * Ein "Behälter" für Grafikkomponenten, um sie zum Beispiel zu gruppieren. <br>
 * Die Klasse erbt die Fähigkeiten von {@link GlComponent} und hält sich zusätzlich folgende Daten:
 * <ul>
 * 	<li> Die Komponenten, die sie besitzt.
 * 	<li> Das Layout, um die Komponenten innerhalb des Containers zu positionieren.
 * </ul>
 * 
 * @author Jan Bouillon
 * @see GlComponent
 * @see GlLayout
 */
public class GlContainer extends GlComponent
{

	protected ArrayList<GlComponent> components;
	protected GlLayout layout;
	
	public GlContainer(ImageResource resource, Point anchor)
	{
		super(resource, anchor);
		
		// default-Werte
		layout = new GlBorderLayout(anchor);
		components = new ArrayList<GlComponent>();
	}
	
    /**
     * Liefert alle Komponenten, die in diesem Container gehalten werden.
     * 
     * @return Die Komponenten.
     */
    public ArrayList<GlComponent> getComponents()
    {
    	return components;
    }

    /**
     * Fügt dem Container eine Komponte hinzu, ohne sie konkret zu positionieren,
     * d.h. sie wird an die Position (0,0) oder, falls dort bereits eine Komponente liegt,
     * entsprechend darunter, "verankert".
     * 
     * @param component Die Komponente, die hinzugefügt werden soll.
     */
    public void addComponent(GlComponent component)
    {
    	addComponent(component, null);
    }
    
    /**
     * Fügt dem Container eine Komponente hinzu, und zwar an eine definierte Position, in Abhängigkeit des Layouts.
     * <br>
     * <br>
     * Falls das Layout zum Beispiel das <code>GlBorderLayout</code> ist, sind die vordefinierten Positionen:
     * <ul>
     * 	<li> North
     * 	<li> East
     * 	<li> South
     * 	<li> West und
     * 	<li> South
     * </ul>
     * Falls an einer Position bereits eine Komponente existiert, so werden beide zentriert einem neuen Container hinzugefügt und
     * dieser anschließend an der Position verankert. Alle anschließend hinzugefügten Komponenten werden in diesem Container
     * gehalten. 
     * 
     * @param component Die Komponente, die hinzugefügt werden soll
     * @param orientation Ihre Position im Container (und demnach auch im Fenster)
     */
    public void addComponent(GlComponent component, Orientation orientation)
    {
    	assert component != null : "Can not add GlComponent = " + component;
    	
    	// Falls eine konkrete (Layout-)Position angegeben wurde
    	if (orientation != null)
    	{
    		GlComponent currentLayoutComponent = layout.getLayoutComponent(orientation);
    		// falls noch keine Komponente an dieser Position existiert
    		if (currentLayoutComponent == null)
			{
    			// Anker dieser Komponente neu ausrichten (in Abhängigkeit der Layout-Position)
    			layout.redefineAnchor(component, this, orientation);
    			layout.addLayoutComponent(component, orientation);
                
                components.add(component);
			}
    		// falls dort bereits ein Container vorhanden ist
    		else if (currentLayoutComponent instanceof GlContainer)
    		{
    			((GlContainer) currentLayoutComponent).addComponent(component);
    		}
    		// falls dort eine Komponente liegt
    		else
    		{
    			removeComponent(currentLayoutComponent);
    			
    			// neuen Container erzeugen
    			GlPanel glContainer = new GlPanel(currentLayoutComponent.getAnchor());
    			
    			currentLayoutComponent.setAnchor(new Point(0,0));
    			// beide Komponenten hinzufügen
    			glContainer.addComponent(currentLayoutComponent);
    			glContainer.addComponent(component);
                
    			// Container an diese Position setzen
                layout.addLayoutComponent(glContainer, orientation);
                components.add(glContainer);
    		}
    	}
    	// ansonsten Komponente an die nächste mögliche freie Position darunter setzen
    	else
    	{
    		int componentPositionY = component.getAnchor().y + layout.getHorizontalOffset();
    		int componentPositionX = component.getAnchor().x + layout.getVerticalOffset();
    		
    		component.setAnchor(new Point(componentPositionX, componentPositionY));
    		layout.addHorizontalOffset(component.height);
            
    		components.add(component);
    	}
    	
    }
    
    /**
     * Entfernt die übergebene Komponente, soweit sie im Container vorhanden ist.
     * 
     * @param component
     * @return <code>true</code>, wenn die Komponente im Container vorhanden war.
     */
    public boolean removeComponent(GlComponent component)
    {
    	return components.remove(component);
    }

	/**
	 * Zeichnet den Container und alle seine Komponenten
	 * 
	 * @see edu.fhm.cs.spaceracing.frames.components.GlComponent#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(GLAutoDrawable drawable)
	{
        super.display(drawable);
        
		for (GlComponent component : components)
		{
			component.display(drawable);
		}
	}

	/**
	 * Überpr�ft für alle seine Komponenten, ob die übergebene Position innerhalb einer Komponente liegt.
	 * 
	 * @see edu.fhm.cs.spaceracing.frames.components.GlComponent#isTouched(int, int)
	 */
	@Override
	public boolean isTouched(int x, int y)
	{
		boolean touched = false;
		
		for (GlComponent component : components)
		{
			touched = component.isTouched(x, y);
		}
		
		return touched;
	}
	
	/**
	 * Setzen des Layouts und Positions-Offsets (in Abhängigkeit der Position des Containers) 
	 * 
	 * @param layout Das zu setzende Layout
	 */
	public void setLayout(GlLayout layout)
	{
		this.layout = layout;
        this.layout.defineOffset(anchor);
	}

	public GlLayout getLayout()
	{
		return layout;
	}

	@Override
	public void setAnchor(Point anchor)
	{
		super.setAnchor(anchor);
		
		for (GlComponent component : components)
		{
			Point oldAnchor = component.getAnchor();
			oldAnchor.x += anchor.x;
			oldAnchor.y += anchor.y;
			component.setAnchor(oldAnchor);
		}
		
		layout.defineOffset(anchor);
	}
	
}
