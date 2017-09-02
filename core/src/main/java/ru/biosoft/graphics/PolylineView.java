package ru.biosoft.graphics;

import java.awt.Graphics2D;
import java.awt.Polygon;

import org.json.JSONException;
import org.json.JSONObject;

public class PolylineView extends PolygonView
{
    public PolylineView(Pen pen)
    {
        this(pen, new Polygon());
    }

    public PolylineView(Pen pen, int[] xpoints, int[] ypoints)
    {
        this(pen, new Polygon(xpoints, ypoints, xpoints.length));
    }

    public PolylineView(Pen pen, Polygon polygon)
    {
        super(pen, null, polygon);
    }

    @Override
    public void paint( Graphics2D g )
    {
        g.setColor(pen.color);
        g.setStroke(pen.getStroke());

        double sx = at.getScaleX();
        double sy = at.getScaleY();

        for( int i=0; i<((Polygon)shape).npoints-1; i++ )
            g.drawLine((int)Math.round(((Polygon)shape).xpoints[i]*sx),   (int)Math.round((((Polygon)shape).ypoints[i]*sy)),
                       (int)Math.round(((Polygon)shape).xpoints[i+1]*sx), (int)Math.round(((Polygon)shape).ypoints[i+1]*sy));
    }

    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        return result;
    }
}
