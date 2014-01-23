package edu.fhm.cs.spaceracing.frames.layout;

import java.awt.Point;

import edu.fhm.cs.spaceracing.frames.components.GlComponent;

/**
 * Ein LayoutManager angelehnt an das BorderLayout der Swing-API von Java 
 * 
 * @author Jan Bouillon
 */
public class GlBorderLayout extends GlLayout
{
    
	private GlComponent center;
	private GlComponent north;
	private GlComponent east;
	private GlComponent south;
	private GlComponent west;
	
	/**
	 * Eine Sammlung von vordefinierten Positionen im Fenster. Nur nutzbar für den LayoutManager {@link GlBorderLayout}  
	 * 
	 * @author Jan Bouillon
	 */
	public enum BorderOrientation implements Orientation
	{
		Center, 
	    North, 
	    East, 
	    South, 
	    West
	}
	
	/**
	 * Erzeugt ein neues BorderLayout. Die übergebene Position ist für das horizontale und vertikale Offset wichtig.
	 * 
	 * @param anchor 
	 */
	public GlBorderLayout(Point anchor)
	{
		super(anchor);
	}

	/**
	 * Erzeugt ein neues BorderLayout mit einem Offset von (0,0)
	 */
	public GlBorderLayout()
	{
		this(new Point(0,0));
	}
	
	/**
	 * Setzt die Komponente an die Position. Die Default-Position ist Center
	 *  
	 * @param component Eine Komponente
	 * @param orientation Eine Position im BorderLayout
	 */
	private void addLayoutComponent(GlComponent component, BorderOrientation orientation)
	{
		switch (orientation)
		{
			case Center:
				center = component;
				break;

			case North:
				north = component;
				break;

			case East:
				east = component;
				break;

			case South:
				south = component;
				break;

			case West:
				west = component;
				break;

			default:
				center = component;
		}
	}
	
	/**
	 * Liefert die Komponente an der entsprechenden Position, oder <code>null</code>,
	 * falls diese Position nicht existiert oder dort keine Komponente vorhanden ist.
	 * 
	 * @param orientation Die Position im Layout
	 * @return Die Komponente oder <code>null</code>
	 */
	private GlComponent getLayoutComponent(BorderOrientation orientation)
	{
		GlComponent component = null;
		
		switch (orientation)
		{
			case Center:
				component = center;
				break;

			case North:
				component = north;
				break;

			case East:
				component = east;
				break;

			case South:
				component = south;
				break;

			case West:
				component = west;
				break;
		}
		return component;
	}

	/**
	 * @see edu.fhm.cs.spaceracing.frames.layout.GlLayout#addLayoutComponent(edu.fhm.cs.spaceracing.frames.components.GlComponent, java.lang.Object)
	 */
	@Override
	public void addLayoutComponent(GlComponent component, Object constraints)
	{
		if (constraints == null || constraints instanceof BorderOrientation)
		{
			addLayoutComponent(component, (BorderOrientation) constraints);
		}
		else
		{
			throw new GlLayoutException("constraints must be a type of " + BorderOrientation.class.getSimpleName());
		}
	}

	/**
	 * @see edu.fhm.cs.spaceracing.frames.layout.GlLayout#getLayoutComponent(java.lang.Object)
	 */
	@Override
	public GlComponent getLayoutComponent(Object constraints)
	{
		if (constraints == null || constraints instanceof BorderOrientation)
		{
			return getLayoutComponent((BorderOrientation) constraints);
		}
        
		return null;
	}
    
    /**
     * @see edu.fhm.cs.spaceracing.frames.layout.GlLayout#redefineAnchor(edu.fhm.cs.spaceracing.frames.components.GlComponent, edu.fhm.cs.spaceracing.frames.components.GlComponent, java.lang.Object)
     */
    @Override
    public void redefineAnchor(GlComponent component, GlComponent parent, Object constraints)
    {
        if (constraints == null || constraints instanceof BorderOrientation)
        {
            redefineAnchor(component, parent, (BorderOrientation) constraints);
        }
        else
        {
            throw new GlLayoutException("constraints must be a type of " + BorderOrientation.class.getSimpleName());
        }
    }
    
    // TODO_JAN Später noch die Position so definieren, daß leere LayoutPositionen mitgenutzt werden
    // z.B.: Es gibt nur eine Komponente in Center, dann liegt sie auf (0,0) und nicht auf (1/4*x, 1/4*y)
    /**
     * Setzt die Position der Komponente, abhängig von der Position der Vater-Komponente und der Position innerhalb des Layouts. 
     * 
     * @param component Die Komponente, für die die Position gesetzt werden soll
     * @param parent Die Vater-Komponente
     * @param orientation Die Layout-Position
     */
    private void redefineAnchor(GlComponent component, GlComponent parent, BorderOrientation orientation)
    {
        Point anchor = parent.getAnchor();
        Point newAnchor = new Point(anchor);
        
        int width = parent.getWidth();
        int height = parent.getHeight();
        
        switch (orientation)
        {
            
            case Center:
                newAnchor.x += (int) (0.25 * width);
                newAnchor.y += (int) (0.25 * height );
                break;
    
            case North:
                // TODO_JAN bisher eher sinnlos, aber später vllt. noch für einen Abstand wichtig
                newAnchor.x += 0;
                newAnchor.y += 0;
                break;
    
            case East:
                newAnchor.x += (int) (0.75 * width);
                newAnchor.y += (int) (0.25 * height);
                break;
    
            case South:
                newAnchor.x += 0;
                newAnchor.y += (int) (0.75 * height);
                break;
    
            case West:
                newAnchor.x += 0;
                newAnchor.y += (int) (0.25 * height);
                break;
    
            default:
                break;
        }
        
        component.setAnchor(newAnchor);
    }
}
