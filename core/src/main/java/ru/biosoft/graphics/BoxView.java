
package ru.biosoft.graphics;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

import org.json.JSONException;
import org.json.JSONObject;

public class BoxView extends ShapeView
{
    public BoxView(JSONObject jsonObj)
    {
        super(null);
        initFromJSON(jsonObj);
    }

    public BoxView(Pen pen, Brush brush, Rectangle rect)
    {
        this(pen, brush, rect.x, rect.y, rect.width, rect.height);
    }

    public BoxView(Pen pen, Brush brush, int x, int y, int width, int height)
    {
        this(pen, brush, (float)x, (float)y, (float)width, (float)height);
    }

    public BoxView(Pen pen, Brush brush, float x, float y, float width, float height)
    {
        super(new Rectangle2D.Float(width > 0 ? x : x + width, height > 0 ? y : y + height, width > 0 ? width : -width, height > 0 ? height
                : -height), pen, brush);
    }

    public BoxView(Pen pen, Brush brush, float x, float y, float width, float height, float arcWidth, float arcHeight)
    {
        super(new RoundRectangle2D.Float(width > 0 ? x : x + width, height > 0 ? y : y + height, width > 0 ? width : -width, height > 0 ? height
                : -height, arcWidth, arcHeight), pen, brush);
    }

    public BoxView(Pen pen, Brush brush, RectangularShape shape)
    {
        super(shape, pen, brush);
    }

    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Rectangle getBounds()
    {
        Rectangle rect = (Rectangle)super.getBounds().clone();

        if( pen != null )
            rect.grow((int) ( pen.getWidth() / 2 ), (int) ( pen.getWidth() / 2 ));

        return rect;
    }

    @Override
    public void move(int sx, int sy)
    {
        RectangularShape rect = (RectangularShape)shape;
        rect.setFrame(rect.getX() + sx, rect.getY() + sy, rect.getWidth(), rect.getHeight());
    }

    public void resize(int sx, int sy)
    {
        RectangularShape rect = (RectangularShape)shape;
        rect.setFrame(rect.getX(), rect.getY(), rect.getWidth() + sx, rect.getHeight() + sy);
    }

    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        Rectangle rect = (Rectangle)super.getBounds().clone();
        if( shape instanceof RoundRectangle2D )
        {
            RoundRectangle2D roundRect = (RoundRectangle2D)shape;
            result.put("arcWidth", roundRect.getArcWidth());
            result.put("arcHeight", roundRect.getArcHeight());
        }

        if( shape instanceof Ellipse2D )
        {
            result.put("class", "EllipseView");
            result.put("x", rect.x + rect.width / 2);
            result.put("y", rect.y + rect.height / 2);
        }
        else
        {
            result.put("x", rect.x);
            result.put("y", rect.y);
        }

        result.put("width", rect.width);
        result.put("height", rect.height);
        return result;
    }

    @Override
    protected void initFromJSON(JSONObject from)
    {
        super.initFromJSON(from);

        try
        {
            int x = from.getInt("x");
            int y = from.getInt("y");

            int width = from.getInt("width");
            int height = from.getInt("height");

            if( from.has("arcWidth") && from.has("arcHeight") )
            {
                float arcw = (float)from.getDouble("arcWidth");
                float arch = (float)from.getDouble("arcHeight");
                shape = new RoundRectangle2D.Float(x, y, width, height, arcw, arch);
            }
            else if( from.has("class") && from.getString("class").equals("EllipseView") )
            {
                shape = new Ellipse2D.Float(x - width / 2, y - width / 2, width, height);
            }
            else
            {
                shape = new Rectangle2D.Float(x, y, width, height);
            }
        }
        catch( JSONException e )
        {
        }
    }
}
