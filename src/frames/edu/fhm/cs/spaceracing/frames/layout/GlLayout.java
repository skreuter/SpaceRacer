package edu.fhm.cs.spaceracing.frames.layout;

import java.awt.Point;

import edu.fhm.cs.spaceracing.frames.components.GlComponent;

// TODO_JAN Gesamte Hierarchie für Layouts überarbeiten, falls das mit unterschiedlichen constraints überhaupt möglich ist.
/**
 * Die abstrakte Klasse aller Layouts.<br>
 * Sie hält sich das Offset für die Neuberechnung der Positionen für die einzelnen Komponenten.
 * Die erbende Klasse muß beschreiben:
 * <ul>
 * 	<li>wie sie die Positionen neu setzt, 
 * 	<li>wie sie die Komponenten hinzufügt und
 * 	<li>wie sie Komponenten liefert
 * </ul>
 * 
 * @author Jan Bouillon
 *
 */
public abstract class GlLayout
{
    
	protected int horizontalOffset;
	protected int verticalOffset;
	
	public GlLayout(Point anchor)
	{
		defineOffset(anchor);
	}

	public int getHorizontalOffset()
	{
		return horizontalOffset;
	}

	public void addHorizontalOffset(int offset)
	{
		horizontalOffset += offset;
	}

	public int getVerticalOffset()
	{
		return verticalOffset;
	}
	
	public void addVerticalOffset(int offset)
	{
		verticalOffset += offset;
	}
	
    public abstract void redefineAnchor(GlComponent component, GlComponent parent, Object constraints);
	public abstract void addLayoutComponent(GlComponent component, Object constraints);
	public abstract GlComponent getLayoutComponent(Object constraints);

    public void defineOffset(Point anchor)
    {
        horizontalOffset = anchor.y;
        verticalOffset = anchor.x;
    }
}
