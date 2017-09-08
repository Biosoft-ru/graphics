package ru.biosoft.graphics;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Decorator for {@link java.awt.Paint} that can be serialised into JSON and edited using property inspector.
 */
public class Brush
{
    private float angle;
    private transient double sin, cos;
    protected Paint paint;

    public Brush()
    {
        this.paint = Color.BLACK;
    }

    public Brush(Paint color)
    {
        setPaint(color);
    }

    public Brush(JSONObject jsonObj)
    {
        initFromJSON(jsonObj);
    }

    /**
     * Gradient fill from color1 to color2
     * @param color1
     * @param color2
     * @param angle angle between vertical line and fill direction in degrees (0-360)
     */
    public Brush(Color color1, Color color2, float angle)
    {
        this.angle = (float) ( angle*Math.PI/180 );
        this.sin = Math.sin(this.angle);
        this.cos = Math.cos(this.angle);
        this.paint = new GradientPaint(0, 0, color1, (float) ( 100 * this.sin ), (float) ( 100 * this.cos ), color2);
    }
    
    public Brush(Color color1, Color color2)
    {
        this(color1, color2, 0);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(angle);
        result = prime * result + ( ( paint == null ) ? 0 : paint.hashCode() );
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if( this == obj )
            return true;

        if( obj == null || getClass() != obj.getClass() )
            return false;
        
        Brush other = (Brush)obj;
        if( Float.floatToIntBits(angle) != Float.floatToIntBits(other.angle) )
            return false;

        if( paint == null )
            return other.paint == null;
        else if( paint instanceof GradientPaint && other.paint instanceof GradientPaint)
            return  gradientEquals((GradientPaint)paint, (GradientPaint)other.paint);
        else if( !paint.equals(other.paint) )
            return false;
        
        return true;
    }
    
    public static boolean gradientEquals(GradientPaint gp1, GradientPaint gp2)
    {
        return gp1.getColor1().equals(gp2.getColor1()) && gp1.getColor2().equals(gp2.getColor2())
                && ( gp1.isCyclic() == gp2.isCyclic() ) && gp1.getPoint1().equals(gp2.getPoint1())
                && gp1.getPoint2().equals(gp2.getPoint2());
    }

    public Paint getPaint()
    {
        return paint;
    }

    public Paint getPaint(Rectangle bounds)
    {
        if(paint instanceof GradientPaint)
        {
            double proj = (Math.abs(bounds.width*sin)+Math.abs(bounds.height*cos))/2;
            double sinDist = proj*sin;
            double cosDist = proj*cos;
            return new GradientPaint((float) ( bounds.getCenterX() - sinDist ), (float) ( bounds.getCenterY() - cosDist ),
                    ( (GradientPaint)paint ).getColor1(), (float) ( bounds.getCenterX() + sinDist ),
                    (float) ( bounds.getCenterY() + cosDist ), ( (GradientPaint)paint ).getColor2());
        } else return paint;
    }

    public void setPaint(Paint value)
    {
        paint = value;
        if(paint instanceof GradientPaint)
        {
            Point2D p1 = ((GradientPaint)paint).getPoint1();
            Point2D p2 = ((GradientPaint)paint).getPoint2();
            angle = (float)Math.atan2(p2.getX()-p1.getX(), p2.getY()-p1.getY());
            sin = Math.sin(angle);
            cos = Math.cos(angle);
        }
        json = null;
    }
    
    public Color getColor()
    {
        if(paint instanceof GradientPaint)
            return ((GradientPaint)paint).getColor1();
        return (Color)paint;
    }
    
    public void setColor(Color color)
    {
        Object oldValue = paint;
        if(paint instanceof GradientPaint)
        {
            GradientPaint oldPaint = (GradientPaint)paint;
            paint = new GradientPaint(oldPaint.getPoint1(), color, oldPaint.getPoint2(), oldPaint.getColor2());
        }
        else
        {
            paint = color;
        }
        json = null;
        
        firePropertyChange("paint", oldValue, paint);
    }

    public Color getColor2()
    {
        if(paint instanceof GradientPaint)
            return ((GradientPaint)paint).getColor2();
        return (Color)paint;
    }

    public void setColor2(Color color)
    {
        Object oldValue = paint;
        if(paint instanceof GradientPaint)
        {
            GradientPaint oldPaint = (GradientPaint)paint;
            paint = new GradientPaint(oldPaint.getPoint1(), oldPaint.getColor1(), oldPaint.getPoint2(), color);
        }
        else
        {
            paint = color;
        }
        json = null;
        
        firePropertyChange( "paint", oldValue, paint );
    }
    
    public boolean isGradient()
    {
        return paint instanceof GradientPaint;
    }
    
    public void setGradient(boolean gradient)
    {
        if(gradient == isGradient())
            return;
        Object oldValue = paint;
        if(gradient)
        {
            paint = new GradientPaint(0, 0, getColor(), 0, 100, getColor());
            angle = 0;
            sin = 0;
            cos = 1;
        }
        else
            paint = getColor();
        json = null;

        firePropertyChange( "paint", oldValue, paint );
    }
    
    public boolean isGradientOptionsHidden()
    {
        return !isGradient();
    }
    
    public double getAngle()
    {
        if(paint instanceof GradientPaint)
        {
            return angle*180/Math.PI;
        }
        return 0;
    }
    
    public void setAngle(double angle)
    {
        if(!(paint instanceof GradientPaint))
            return;
        Object oldValue = paint;
        this.angle = (float) ( angle*Math.PI/180 );
        this.sin = Math.sin(this.angle);
        this.cos = Math.cos(this.angle);
        this.paint = new GradientPaint(0, 0, getColor(), (float) ( 100 * this.sin ), (float) ( 100 * this.cos ), getColor2());
        json = null;

        firePropertyChange( "paint", oldValue, paint );
    }

    @Override
    public String toString()
    {
        try
        {
            return toJSON().toString();
        }
        catch( JSONException e )
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
            Color color1 = null;
            Color color2 = null;
            if( paint instanceof Color )
            {
                color1 = (Color)paint;
            }
            else if( paint instanceof GradientPaint )
            {
                color1 = ( (GradientPaint)paint ).getColor1();
                color2 = ( (GradientPaint)paint ).getColor2();
            }
            else
                throw new IllegalArgumentException("Unsupported paint: "+paint);
            JSONArray c1 = new JSONArray();
            c1.put(color1.getRed());
            c1.put(color1.getGreen());
            c1.put(color1.getBlue());
            c1.put(color1.getAlpha());
            json.put("color", c1);
            if( color2 != null )
            {
                JSONArray c2 = new JSONArray();
                c2.put(color2.getRed());
                c2.put(color2.getGreen());
                c2.put(color2.getBlue());
                c2.put(color2.getAlpha());
                json.put("color2", c2);
                if(Math.abs(angle) > 0.0001) json.put("angle", angle);
            }
        }
        
        return json;
    }

    private void initFromJSON(JSONObject from)
    {
        try
        {
            JSONArray color = (JSONArray)from.get("color");
            int red = color.getInt(0);
            int green = color.getInt(1);
            int blue = color.getInt(2);
            int alpha = 255;
            if(color.length()>3)
            {
                alpha = color.getInt(3);
            }
            JSONArray color2 = (JSONArray)from.opt("color2");

            if( color2 == null )
            {
                paint = new Color(red, green, blue, alpha);
            }
            else
            {
                int red2 = color2.getInt(0);
                int green2 = color2.getInt(1);
                int blue2 = color2.getInt(2);
                int alpha2 = color2.getInt(3);
                angle = (float)from.optDouble( "angle", 0.0 );
                sin = Math.sin(angle);
                cos = Math.cos(angle);
                paint = new GradientPaint(0, 0, new Color(red, green, blue, alpha), (float) ( 100 * this.sin ), (float) ( 100 * this.cos ), new Color(red2, green2, blue2, alpha2));
            }
        }
        catch( JSONException e )
        {}
    }

    // /////////////////////////////////////////////////////////////////////////
    // Listeners
    //
    
	protected PropertyChangeSupport pcSupport = null;

	public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if(pcSupport == null)
            pcSupport = new PropertyChangeSupport(this);
    
        pcSupport.addPropertyChangeListener(listener);
    }

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if(pcSupport == null)
            pcSupport = new PropertyChangeSupport(this);
    
        pcSupport.addPropertyChangeListener(propertyName, listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if(pcSupport != null)
            pcSupport.removePropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if(pcSupport != null)
            pcSupport.removePropertyChangeListener(propertyName, listener);
    }

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if(pcSupport != null)
            pcSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

}