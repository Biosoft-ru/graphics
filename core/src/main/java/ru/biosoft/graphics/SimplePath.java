package ru.biosoft.graphics;

import java.awt.Point;

/**
 * Container class created to replace ru.biosoft.graph.Path in ru.biosoft.graphics
 */
public class SimplePath
{
    public int[] xpoints;
    public int[] ypoints;
    public int[] pointTypes;
    public int npoints;

    public SimplePath(int[] xPoints, int[] yPoints, int[] pointTypes, int npoints)
    {
        this.xpoints = xPoints;
        this.ypoints = yPoints;
        this.pointTypes = pointTypes;
        this.npoints = npoints;
    }

    public SimplePath(int[] xPoints, int[] yPoints, int npoints)
    {
        this(xPoints, yPoints, new int[npoints], npoints);
    }

    public SimplePath(Point start, Point end)
    {
        this(new int[] {start.x, end.x}, new int[] {start.y, end.y}, 2);
    }
    
    public void addPoint(Point p)
    {
        npoints++;
        xpoints = new int[npoints];
        ypoints = new int[npoints];
        pointTypes = new int[npoints];
    }
}
