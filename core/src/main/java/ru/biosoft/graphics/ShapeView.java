package ru.biosoft.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * General shape view.
 */
public abstract class ShapeView extends View
{
    protected Pen pen = null;
    protected Brush brush = null;

    public ShapeView(Shape shape)
    {
        super(shape);
    }

    public ShapeView(Shape shape, Pen pen, Brush brush)
    {
        super(shape);

        this.pen = pen;
        this.brush = brush;
    }

    public Pen getPen()
    {
        return pen;
    }

    public Brush getBrush()
    {
        return brush;
    }

    public void setBrush(Brush brush)
    {
        this.brush = brush;
    }

    public void setPen(Pen pen)
    {
        this.pen = pen;
    }

    @Override
    public boolean equals(Object obj)
    {
        if( obj == null || ! ( obj instanceof ShapeView ) )
            return false;
        if( !super.equals(obj) )
            return false;
        ShapeView v = (ShapeView)obj;
        if( ( pen == null && v.pen != null ) || ( pen != null && !pen.equals(v.pen) ) )
            return false;
        if( ( brush == null && v.brush != null ) || ( brush != null && !brush.equals(v.brush) ) )
            return false;
        return true;
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
        if( isVisible() )
        {
            if( brush != null )
            {
                Paint paint = brush.getPaint(shape.getBounds());
                g2.setPaint(paint);
                g2.fill(shape);
            }

            if( pen != null && pen.getWidth() > 0 )
            {
                g2.setColor(pen.color);
                g2.setStroke(pen.getStroke());
                try
                {
                    g2.draw( shape );
                }
                catch( Throwable t )
                {
                    g2.setColor( Color.RED );
                    g2.setStroke( new BasicStroke(3) );
                    g2.draw( shape );
                }
            }
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        if( pen != null )
            result.put("pen", pen.toJSON());
        if( brush != null )
            result.put("brush", brush.toJSON());
        return result;
    }

    @Override
    protected void initFromJSON(JSONObject from)
    {
        super.initFromJSON(from);

        try
        {
            if( from.has("pen") )
            {
                pen = new Pen(from.getJSONObject("pen"));
            }

            if( from.has("brush") )
            {
                brush = new Brush(from.getJSONObject("brush"));
            }
        }
        catch( JSONException e )
        {
        }
    }
}
