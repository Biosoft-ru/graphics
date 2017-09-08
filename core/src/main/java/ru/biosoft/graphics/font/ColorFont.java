package ru.biosoft.graphics.font;

import java.awt.Color;
import java.awt.Font;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ColorFont
{
    public ColorFont(String name, int style, int size, Color c)
    {
        font = new Font(name, style, size);
        this.color = c;
    }

    public ColorFont(String name, int style, int size)
    {
        this(name, style, size, Color.black);
    }

    public ColorFont(Font font, Color c)
    {
        this(font.getName(), font.getStyle(), font.getSize(), c);
    }

    public ColorFont()
    {
        this("Courier", Font.PLAIN, 12, Color.black);
    }

    public ColorFont(String jsonStr) throws JSONException
    {
        this(new JSONObject(jsonStr));
    }

    public ColorFont(JSONObject json)
    {
        initFromJSON(json);
    }

    private Color color;
    public void setColor(Color color)
    {
        this.color = color;
        json = null;
    }
    public Color getColor()
    {
        return color;
    }

    protected Font font = null;
    public Font getFont()
    {
        return font;
    }
    
    public void setFont(Font font)
    {
        this.font = font;
        json = null;
    }

    @Override
    public String toString()
    {
        try
        {
            return toJSON().toString();
        }
        catch( Exception ex )
        {
            return "";
        }
    }
    
    JSONObject json = null;
    public JSONObject toJSON() throws JSONException
    {
        if( json == null )
        {
            json = new JSONObject();

            JSONArray JSONfont = new JSONArray();
            JSONfont.put(font.getName());
            JSONfont.put(font.getStyle());
            JSONfont.put(font.getSize());
            json.put("font", JSONfont);

            JSONArray JSONcolor = new JSONArray();
            JSONcolor.put(color.getRed());
            JSONcolor.put(color.getGreen());
            JSONcolor.put(color.getBlue());
            JSONcolor.put(color.getAlpha());
            json.put("color", JSONcolor);
        }

        return json;
    }

    private void initFromJSON(JSONObject from)
    {
        try
        {
            JSONArray font = (JSONArray)from.get("font");
            JSONArray color = (JSONArray)from.get("color");
            String fontName = font.getString(0);
            int fontStyle = font.getInt(1);
            int fontSize = font.getInt(2);

            int red = color.getInt(0);
            int green = color.getInt(1);
            int blue = color.getInt(2);
            int alpha = color.getInt(3);

            this.color = new Color(red, green, blue, alpha);
            this.font = new Font(fontName, fontStyle, fontSize);
        }
        catch( JSONException e )
        {
        }
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        ColorFont other = (ColorFont)obj;
        
        if (!color.equals(other.color))
            return false;
        
        if (!font.equals(other.font))
            return false;

        return true;
    }

}