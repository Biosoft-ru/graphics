package ru.biosoft.graphics;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import org.json.JSONException;
import org.json.JSONObject;

import ru.biosoft.graphics.font.ColorFont;

public class TextView extends View
{
    protected String text;
    protected int alignment;
    protected int y;
    protected Rectangle rect = new Rectangle();
    protected ColorFont font;
    protected double yPosRatio;

    public TextView(JSONObject jsonObj)
    {
        super(null);
        initFromJSON(jsonObj);
    }

    public TextView(String text, Point pt, int alignment, ColorFont font, Graphics graphics)
    {
        super(null);
        this.text = text;
        this.rect.x = pt.x;
        y = pt.y;
        this.alignment = alignment;
        this.font = font;

        initSize( graphics.getFontMetrics( font.getFont() ) );
    }

    public TextView(String text, ColorFont font, Graphics graphics)
    {
        this(text, new Point(0, 0), LEFT | BASELINE, font, graphics);
    }

    protected void initSize(FontMetrics fm)
    {
        rect.width = fm.stringWidth(text);
        rect.height = fm.getHeight();

        // X definition
        if( ( alignment & RIGHT ) != 0 )
            rect.x -= rect.width;
        else if( ( alignment & CENTER ) != 0 )
            rect.x -= rect.width / 2;

        // Y definition
        if( ( alignment & TOP ) != 0 )
            y += fm.getAscent();
        else if( ( alignment & BOTTOM ) != 0 )
            y -= fm.getDescent();
        rect.y = y - fm.getAscent();
        yPosRatio = (double)(y - rect.y)/rect.height;
    }

    public String getText()
    {
        return text;
    }

    public Rectangle getTextPos(int from, int to, FontMetrics fm)
    {
        String s = text.substring(from, to);
        Rectangle r = new Rectangle(rect.x, rect.y, fm.stringWidth(s), fm.getHeight());

        if( from > 0 )
        {
            String s2 = text.substring(0, from);
            r.x += fm.stringWidth(s2) + 1;
        }

        return r;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || !(obj instanceof TextView)) return false;
        if(!super.equals(obj)) return false;
        TextView v = (TextView)obj;
        if((text == null && v.text != null) || (text!=null && !text.equals(v.text))) return false;
        if(y != v.y || alignment != v.alignment || !rect.equals(v.rect)) return false;
        if(!font.getColor().equals(v.font.getColor()) || !font.getFont().equals(v.font.getFont())) return false;
        return true;
    }

    @Override
    public Rectangle getBounds()
    {
        Rectangle transformedRect =  at.createTransformedShape(rect).getBounds();
        transformedRect.x = rect.x;
        transformedRect.y = rect.y;
        return transformedRect;
    }

    @Override
    public boolean intersects(Rectangle rect)
    {
        return this.rect.intersects(rect);
    }

    @Override
    public void move(int x, int y)
    {
        this.y += y;
        rect.y += y;
        rect.x += x;
    }

    @Override
    public void paint(Graphics2D g)
    {
        if( isVisible() )
        {
            g.setColor(font.getColor());
            FontRenderContext frc = new FontRenderContext(at,
                    g.getRenderingHint(RenderingHints.KEY_ANTIALIASING) == RenderingHints.VALUE_ANTIALIAS_ON, false);
            Font scaledFont = font.getFont().deriveFont(at);
            GlyphVector vec = scaledFont.createGlyphVector(frc, text);
            Rectangle r = getBounds();
            g.drawGlyphVector(vec, rect.x, (float) ( r.y + yPosRatio*r.height ));
            g.clip(g.getClipBounds());
        }
    }



    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        result.put("text", text);
        result.put("font", font.toJSON());
        result.put("alignment", BASELINE|LEFT);
        result.put("x", rect.x);
        result.put("y", y);
        result.put("scaleX", at.getScaleX());
        result.put("scaleY", at.getScaleY());

        result.put("width",  rect.width);
        result.put("height", rect.height);
        result.put("rect.y", rect.y);

        return result;
    }

    @Override
    protected void initFromJSON(JSONObject from)
    {
        super.initFromJSON(from);

        try
        {
            text      = from.getString("text");
            font      = new ColorFont(from.getJSONObject("font").toString());
            alignment = BASELINE|LEFT;
            rect.x    = from.getInt("x");
            y         = from.getInt("y");
            at.scale(from.optDouble("scaleX", 1.0), from.optDouble("scaleY", 1.0));

            rect.width  = from.getInt("width");
            rect.height = from.getInt("height");
            rect.y      = from.getInt("rect.y");
            yPosRatio = (double) ( y - rect.y ) / rect.height;

        }
        catch(JSONException e)
        {
            initFromJSONOld( from );
        }
    }

    @Deprecated
    protected void initFromJSONOld(JSONObject from)
    {
        try
        {
            text = from.getString( "text" );
            font = new ColorFont( from.getJSONObject( "font" ).toString() );
            alignment = BASELINE | LEFT;
            rect.x = from.getInt( "x" );
            y = from.getInt( "y" );
            at.scale( from.optDouble( "scaleX", 1.0 ), from.optDouble( "scaleY", 1.0 ) );

            Canvas c = new Canvas();
            FontMetrics fm = c.getFontMetrics( font.getFont() );
            initSize( fm );
        }
        catch( JSONException e )
        {
        }
    }
}
