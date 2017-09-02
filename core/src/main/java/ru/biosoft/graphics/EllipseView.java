package ru.biosoft.graphics;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import org.json.JSONException;
import org.json.JSONObject;

public class EllipseView extends ShapeView
{
    public EllipseView(JSONObject jsonObj)
    {
        super(null);
        initFromJSON(jsonObj);
    }

    public EllipseView(Pen pen, Brush brush, float xCenter, float yCenter, float width, float height)
    {
        super(new Ellipse2D.Float(xCenter, yCenter, width, height), pen, brush);
    }

    @Override
    public void move(int sx, int sy)
    {
        ((Ellipse2D.Float) shape).x += sx;
        ((Ellipse2D.Float) shape).y += sy;
    }
    
    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        Rectangle rect = (Rectangle)super.getBounds().clone();
        result.put("x", rect.x+rect.width/2);
        result.put("y", rect.y+rect.height/2);
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

            shape = new Ellipse2D.Float(x - width / 2, y - width / 2, width, height);
        }
        catch( JSONException e )
        {
        }
    }
}