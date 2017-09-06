package ru.biosoft.graphics;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FigureView extends ShapeView
{
    public FigureView(Pen pen, Brush brush, int[] xpoints, int[] ypoints, int[] pointTypes)
    {
        super(new GeneralPath(), pen, brush);

        GeneralPath generalPath = (GeneralPath)shape;
        generalPath.moveTo(xpoints[0], ypoints[0]);
        for( int i = 1; i < xpoints.length; i++ )
        {
            if( pointTypes[i] == 1 && i < xpoints.length - 1 )
            {
                generalPath.quadTo(xpoints[i], ypoints[i], xpoints[i + 1], ypoints[i + 1]);
                i += 1;
            }
            if( pointTypes[i] == 2 && i < xpoints.length - 2 )
            {
                generalPath.curveTo(xpoints[i], ypoints[i], xpoints[i + 1], ypoints[i + 1], xpoints[i + 2], ypoints[i + 2]);
                i += 2;
            }
            else
            {
                generalPath.lineTo(xpoints[i], ypoints[i]);
            }
        }
        generalPath.lineTo(xpoints[0], ypoints[0]);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || !(obj instanceof FigureView) || !super.equals(obj) ) 
        	return false;
        
        FigureView v = (FigureView)obj;
        GeneralPath p1 = (GeneralPath)shape;
        GeneralPath p2 = (GeneralPath)v.shape;

        // Shape comparison taken from ShapeUtilities.java (Licensed as GNU LGPL)
        // http://www.java2s.com/Open-Source/Java-Document/Graphic-Library/jcommon-components/org/jfree/util/ShapeUtilities.java.htm
        if (p1 == null) 
            return (p2 == null);

        if (p2 == null) 
            return false;

        if (p1.getWindingRule() != p2.getWindingRule()) 
            return false;

        PathIterator iterator1 = p1.getPathIterator(null);
        PathIterator iterator2 = p1.getPathIterator(null);
        double[] d1 = new double[6];
        double[] d2 = new double[6];
        boolean done = iterator1.isDone() && iterator2.isDone();
        while (!done) 
        {
            if (iterator1.isDone() != iterator2.isDone()) 
                return false;

            int seg1 = iterator1.currentSegment(d1);
            int seg2 = iterator2.currentSegment(d2);
            if (seg1 != seg2) 
                return false;

            if (!Arrays.equals(d1, d2)) 
                return false;
            iterator1.next();
            iterator2.next();
            done = iterator1.isDone() && iterator2.isDone();
        }

        return true;
    }

    @Override
    public void move(int x, int y)
    {
        AffineTransform at = new AffineTransform();
        at.setToTranslation(x, y);
        ((GeneralPath)shape).transform(at);
    }
    
    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        result.put("pen", pen.toJSON());
        result.put("brush", brush.toJSON());
        
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
