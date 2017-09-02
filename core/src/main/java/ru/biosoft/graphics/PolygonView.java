package ru.biosoft.graphics;

import java.awt.Point;
import java.awt.Polygon;
import java.util.Arrays;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PolygonView extends ShapeView
{
    public PolygonView(Pen pen, Brush brush)
    {
        this(pen, brush, new Polygon());
    }

    public PolygonView(Pen pen, Brush brush, int[] xpoints, int[] ypoints)
    {
        this(pen, brush, new Polygon(xpoints, ypoints, xpoints.length));
    }

    public PolygonView(Pen pen, Brush brush, Collection<Point> points)
    {
        this(pen, brush, createPolygon(points));
    }

    /**
     * @param points
     * @return Polygon created from given points
     */
    private static Polygon createPolygon(Collection<Point> points)
    {
        int[] xpoints = new int[points.size()];
        int[] ypoints = new int[points.size()];
        int i = 0;
        for( Point point : points )
        {
            xpoints[i] = point.x;
            ypoints[i] = point.y;
            i++;
        }
        return new Polygon(xpoints, ypoints, points.size());
    }

    public PolygonView(Pen pen, Brush brush, Polygon polygon)
    {
        super(polygon, pen, brush);
    }

    @Override
    public boolean equals(Object obj)
    {
        if( obj == null || ! ( obj instanceof PolygonView ) )
            return false;
        
        if( !super.equals(obj) )
            return false;
        
        PolygonView v = (PolygonView)obj;
        Polygon s1 = (Polygon)shape, s2 = (Polygon)v.shape;
        if( s1.npoints != s2.npoints || !Arrays.equals(s1.xpoints, s2.xpoints) || !Arrays.equals(s1.ypoints, s2.ypoints) )
            return false;
        
        return true;
    }

    public void addPoint(Point p)
    {
        ((Polygon)shape).addPoint(p.x, p.y);
    }

    public void addPoint(int x, int y)
    {
        ((Polygon)shape).addPoint(x, y);
    }

    @Override
    public void move(int x, int y)
    {
        ((Polygon)shape).translate(x, y);
    }
    
    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        Polygon pol = (Polygon)shape;

        JSONArray xpoints = new JSONArray();
        JSONArray ypoints = new JSONArray();
        for( int i = 0; i < pol.npoints; i++ )
        {
            xpoints.put(pol.xpoints[i]);
            ypoints.put(pol.ypoints[i]);
        }
        result.put("xpoints", xpoints);
        result.put("ypoints", ypoints);

        return result;
    }
}
