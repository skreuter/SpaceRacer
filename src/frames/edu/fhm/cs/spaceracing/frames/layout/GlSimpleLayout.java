package edu.fhm.cs.spaceracing.frames.layout;

import java.awt.Point;

import edu.fhm.cs.spaceracing.frames.components.GlComponent;

/**
 * Ein einfaches Layout für das schnelle Positionieren von Komponenten innerhalb eines Containers.<br>
 * 
 * Folgende Positionen werden unterstützt:
 * <ul>
 * 	<li>left
 * 	<li>leftTop
 * 	<li>leftBottom
 * 	<li>right
 * 	<li>rightTop
 * 	<li>rightBottom
 * 	<li>top
 * 	<li>center
 * 	<li>bottom
 *</ul>
 *
 * @author Jan Bouillon
 *
 */
public class GlSimpleLayout extends GlLayout
{
    private GlComponent left;
    private GlComponent leftTop;
    private GlComponent leftBottom;
    private GlComponent right;
    private GlComponent rightTop;
    private GlComponent rightBottom;
    private GlComponent top;
    private GlComponent bottom;
    
    /**
     * Eine Sammlung von vordefinierten Positionen im Fenster. Nur nutzbar für den LayoutManager {@link GlSimpleLayout} 
     * 
     * @author Jan Bouillon
     *
     */
    public enum SimpleOrientation implements Orientation
    {
        left,
        leftTop,
        leftBottom,
        
        right,
        rightTop,
        rightBottom,
        
        top,
        center,
        bottom
    }
    
	/**
	 * Erzeugt ein neues SimpleLayout. Die übergebene Position ist für das horizontale und vertikale Offset wichtig.
	 * 
	 * @param anchor 
	 */
    public GlSimpleLayout(Point anchor)
    {
        super(anchor);
    }
    
	/**
	 * Erzeugt ein neues SimpleLayout mit einem Offset von (0,0)
	 */
    public GlSimpleLayout()
    {
        this(new Point(0,0));
    }
    
	/**
	 * Setzt die Komponente an die Position. Die Default-Position ist left
	 *  
	 * @param component Eine Komponente
	 * @param orientation Eine Position im SimpleLayout
	 */
    private void addLayoutComponent(GlComponent component, SimpleOrientation orientation)
    {
        switch (orientation)
        {
            case left:
                left = component;
                break;

            case leftTop:
                leftTop = component;
                break;

            case leftBottom:
                leftBottom = component;
                break;

            case right:
                right = component;
                break;

            case rightTop:
                rightTop = component;
                break;

            case rightBottom:
                rightBottom = component;
                break;

            case top:
                top = component;
                break;

            case bottom:
                bottom = component;
                break;

            default:
                left = component;
                break;
        }
    }
    
	/**
	 * Liefert die Komponente an der entsprechenden Position, oder <code>null</code>,
	 * falls diese Position nicht existiert oder dort keine Komponente vorhanden ist.
	 * 
	 * @param orientation Die Position im Layout
	 * @return Die Komponente oder <code>null</code>
	 */
    private GlComponent getLayoutComponent(SimpleOrientation orientation)
    {
        GlComponent component = null;
        
        switch (orientation)
        {
            case left:
                component = left;
                break;

            case leftTop:
                component = leftTop;
                break;

            case leftBottom:
                component = leftBottom;
                break;

            case right:
                component = right;
                break;

            case rightTop:
                component = rightTop;
                break;

            case rightBottom:
                component = rightBottom;
                break;

            case top:
                component = top;
                break;

            case bottom:
                component = bottom;
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
        if (constraints == null || constraints instanceof SimpleOrientation)
        {
            addLayoutComponent(component, (SimpleOrientation) constraints);
        }
        else
        {
            throw new GlLayoutException("constraints must be a type of " + SimpleOrientation.class.getSimpleName());
        }
    }

    /**
     * @see edu.fhm.cs.spaceracing.frames.layout.GlLayout#getLayoutComponent(java.lang.Object)
     */
    @Override
    public GlComponent getLayoutComponent(Object constraints)
    {
        if (constraints == null || constraints instanceof SimpleOrientation)
        {
            return getLayoutComponent((SimpleOrientation) constraints);
        }
        
        return null;
    }

    /**
     * @see edu.fhm.cs.spaceracing.frames.layout.GlLayout#redefineAnchor(edu.fhm.cs.spaceracing.frames.components.GlComponent, edu.fhm.cs.spaceracing.frames.components.GlComponent, java.lang.Object)
     */
    @Override
    public void redefineAnchor(GlComponent component, GlComponent parent, Object constraints)
    {
        if (constraints == null || constraints instanceof SimpleOrientation)
        {
            redefineAnchor(component, parent, (SimpleOrientation) constraints);
        }
        else
        {
            throw new GlLayoutException("constraints must be a type of " + SimpleOrientation.class.getSimpleName());
        }
    }
    
    /**
     * Setzt die Position der Komponente, abhängig von der Position der Vater-Komponente und der Position innerhalb des Layouts. 
     * 
     * @param component Die Komponente, für die die Position gesetzt werden soll
     * @param parent Die Vater-Komponente
     * @param orientation Die Layout-Position
     */
    private void redefineAnchor(GlComponent component, GlComponent parent, SimpleOrientation orientation)
    {
        Point anchor = parent.getAnchor();
        int width = parent.getWidth();
        int height = parent.getHeight();
        
        // TODO_JAN size optimieren!!! Vorerst setzen sich die Components ihre size selber 
        Point newAnchor = new Point(anchor);
        
        switch (orientation)
        {
            case left:
                newAnchor.x += 0;
                newAnchor.y += (height/2 - component.getHeight()/2);
                break;

            case leftTop:
                newAnchor.x += 0;
                newAnchor.y += 0;
                break;

            case leftBottom:
                newAnchor.x += 0;
                newAnchor.y += (height - component.getHeight());
                break;

            case right:
                newAnchor.x += (width - component.getWidth());
                newAnchor.y += (width/2 - component.getWidth()/2);
                break;

            case rightTop:
                newAnchor.x += (width - component.getWidth());
                newAnchor.y += 0;
                break;

            case rightBottom:
                newAnchor.x += (width - component.getWidth());
                newAnchor.y += (height - component.getHeight());
                break;

            case top:
                newAnchor.x += (width/2 - component.getWidth()/2);
                newAnchor.y += 0;
                break;

            case bottom:
                newAnchor.x += (width/2 - component.getWidth()/2);
                newAnchor.y += (height - component.getHeight());
                break;

            default:
                newAnchor.x += 0;
                newAnchor.y += 0;
                break;
        }
        
        component.setAnchor(newAnchor);
    }
}
