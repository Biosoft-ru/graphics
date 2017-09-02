package ru.biosoft.graphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.json.JSONException;
import org.json.JSONObject;

public class LineView extends View
{
    protected Pen pen = null;

    public LineView(JSONObject jsonObj)
    {
        super(null);
        initFromJSON(jsonObj);
    }

    public LineView(Pen pen, Point2D pt0, Point2D pt1)
    {
        super(new Line2D.Float(pt0, pt1));
        this.pen = pen;
    }

    public LineView(Pen pen, float X1, float Y1, float X2, float Y2)
    {
        this(pen, new Point2D.Float(X1, Y1), new Point2D.Float(X2, Y2));
    }

    @Override
    public Rectangle getBounds()
    {
        Rectangle rect = (Rectangle)super.getBounds().clone();
        if( pen != null )
        {
            rect.grow((int)Math.ceil(pen.getWidth() / 2), (int)Math.ceil(pen.getWidth() / 2));
        }
        return rect;
    }

    @Override
    public boolean equals(Object obj)
    {
        if( obj == null || ! ( obj instanceof LineView ) )
            return false;
        if( obj == this )
            return true;
        if( !super.equals(obj) )
            return false;
        LineView v = (LineView)obj;
        if( ( pen == null && v.pen != null ) || ( pen != null && !pen.equals(v.pen) ) )
            return false;
        return true;
    }

    @Override
    public void move(int sx, int sy)
    {
        ( (Line2D.Float)shape ).x1 += sx;
        ( (Line2D.Float)shape ).x2 += sx;
        ( (Line2D.Float)shape ).y1 += sy;
        ( (Line2D.Float)shape ).y2 += sy;
    }

    @Override
    public void setToScale(double sx, double sy)
    {
        super.setToScale(sx, sy);
        if( pen != null )
        {
            float w = (float) ( pen.getWidth() * sx );
            pen.setWidth(w);
        }
    }

    @Override
    public void paint(Graphics2D g2)
    {
        if( pen != null && isVisible() )
        {
            g2.setColor(pen.color);
            g2.setStroke(pen.getStroke());
            g2.draw(shape);
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        if( pen != null )
        {
            result.put("pen", pen.toJSON());
        }
        Line2D.Float pos = (Line2D.Float)shape;
        result.put("x1", pos.x1);
        result.put("y1", pos.y1);
        result.put("x2", pos.x2);
        result.put("y2", pos.y2);
        return result;
    }

    @Override
    protected void initFromJSON(JSONObject from)
    {
        super.initFromJSON(from);

        try
        {
            if( from.has("pen") )
                pen = new Pen(from.getJSONObject("pen"));

            double x1 = from.getDouble("x1");
            double y1 = from.getDouble("y1");
            double x2 = from.getDouble("x2");
            double y2 = from.getDouble("y2");

            shape = new Line2D.Float((float)x1, (float)y1, (float)x2, (float)y2);
        }
        catch( JSONException e )
        {
        }
    }
}
