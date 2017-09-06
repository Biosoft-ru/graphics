package ru.biosoft.graphics;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * General path view.
 *
 * @pending whether brush can be used to fill the path?
 */
public class PathView extends ShapeView
{
    public PathView(Pen pen, GeneralPath path)
    {
        super(path, pen, null);
    }

    @Override
    public void move(int sx, int sy)
    {
        AffineTransform at = new AffineTransform();
        at.translate(sx, sy);
        ( (GeneralPath)shape ).transform(at);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || !(obj instanceof PathView)) return false;
        if(!super.equals(obj)) return false;
        PathView v = (PathView)obj;
        GeneralPath p1 = (GeneralPath)shape, p2 = (GeneralPath)v.shape;
        // Shape comparison taken from ShapeUtilities.java (Licensed as GNU LGPL)
        // http://www.java2s.com/Open-Source/Java-Document/Graphic-Library/jcommon-components/org/jfree/util/ShapeUtilities.java.htm
        if (p1 == null) {
            return (p2 == null);
        }
        if (p2 == null) {
            return false;
        }
        if (p1.getWindingRule() != p2.getWindingRule()) {
            return false;
        }
        PathIterator iterator1 = p1.getPathIterator(null);
        PathIterator iterator2 = p1.getPathIterator(null);
        double[] d1 = new double[6];
        double[] d2 = new double[6];
        boolean done = iterator1.isDone() && iterator2.isDone();
        while (!done) {
            if (iterator1.isDone() != iterator2.isDone()) {
                return false;
            }
            int seg1 = iterator1.currentSegment(d1);
            int seg2 = iterator2.currentSegment(d2);
            if (seg1 != seg2) {
                return false;
            }
            if (!Arrays.equals(d1, d2)) {
                return false;
            }
            iterator1.next();
            iterator2.next();
            done = iterator1.isDone() && iterator2.isDone();
        }
        return true;
    }

    /**
     * Redefine View.getBounds to exclude path with zero width or height
     */
    @Override
    public Rectangle getBounds()
    {
        Rectangle bounds = shape.getBounds();
        if( bounds.width == 0 || bounds.height == 0 )
        {
            Rectangle correctedBounds = new Rectangle(bounds);
            if( bounds.width == 0 )
                correctedBounds.width = 1;
            if( bounds.height == 0 )
                correctedBounds.height = 1;
            return correctedBounds;
        }
        return bounds;
    }

    /**
     * Check intersection between path and rectangle
     */
    @Override
    public boolean intersects(Rectangle rect)
    {
        GeneralPath path = (GeneralPath)shape;
        FlatteningPathIterator iter = new FlatteningPathIterator(path.getPathIterator(new AffineTransform()), 5.0);
        double[] coords = new double[6];
        Point prev = new Point();
        while( !iter.isDone() )
        {
            int type = iter.currentSegment(coords);
            if( type == FlatteningPathIterator.SEG_LINETO )
            {
                if( PathUtils.lineIntersects(prev.x, prev.y, (int)coords[0], (int)coords[1], rect) )
                {
                    return true;
                }
            }
            prev.x = (int)coords[0];
            prev.y = (int)coords[1];
            iter.next();
        }
        return false;
    }

    /**
     * Returns middle point of the path
     */
    public Point getMiddlePoint()
    {
        GeneralPath path = (GeneralPath)shape;
        FlatteningPathIterator iter = new FlatteningPathIterator(path.getPathIterator(new AffineTransform()), 5.0);
        double[] coords = new double[6];
        
        List<Point> points = new ArrayList<>();
        while( !iter.isDone() )
        {
            iter.currentSegment(coords);
            points.add(new Point((int)coords[0], (int)coords[1]));
            iter.next();
        }
        int npoints = points.size();
        if( npoints % 2 == 0 )
        {
            return new Point( ( points.get(npoints / 2 - 1).x + points.get(npoints / 2).x ) / 2,
                    ( points.get(npoints / 2 - 1).y + points.get(npoints / 2).y ) / 2);
        }
        else
        {
            return new Point(points.get(npoints / 2));
        }
    }
    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        result.put("pen", pen.toJSON());

        GeneralPath path = (GeneralPath)shape;
        PathIterator iter = path.getPathIterator(new AffineTransform());
        JSONArray xpoints = new JSONArray();
        JSONArray ypoints = new JSONArray();
        JSONArray pointtypes = new JSONArray();
        double[] coords = new double[6];
        while( !iter.isDone() )
        {
            int segmentType = iter.currentSegment(coords);
            int n=0;
            switch(segmentType)
            {
                // TODO: properly support paths containing SEG_MOVETO
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    n=1;
                    break;
                case PathIterator.SEG_QUADTO:
                    n=2;
                    break;
                case PathIterator.SEG_CUBICTO:
                    n=3;
                    break;
                case PathIterator.SEG_CLOSE:
                    xpoints.put(xpoints.get(0));
                    ypoints.put(ypoints.get(0));
                    pointtypes.put(0);
                    break;
            }
            for(int i=0; i<n; i++)
            {
                xpoints.put((int)coords[i*2]);
                ypoints.put((int)coords[i*2+1]);
                pointtypes.put(n-1);
            }
            result.put("xpoints", xpoints);
            result.put("ypoints", ypoints);
            result.put("pointtypes", pointtypes);
            iter.next();
        }
        return result;
    }
}
