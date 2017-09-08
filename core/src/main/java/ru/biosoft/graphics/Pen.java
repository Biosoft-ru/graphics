package ru.biosoft.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Pen
{
    public Pen()
    {
        this(1.0f);
    }

    public Pen(float width)
    {
        this(width, Color.BLACK);
    }

    public Pen(BasicStroke stroke, Color col)
    {
        this.color = col;
        this.setStroke(stroke);
    }

    public Pen(float width, Color col)
    {
        this.color = col;
        setWidth(width);
    }

    public Pen(JSONObject jsonObj)
    {
        this.initFromJSON(jsonObj);
    }

    @Override
    public boolean equals(Object obj)
    {
        if( obj == this )
            return true;
        if( obj == null || ! ( obj instanceof Pen ) )
            return false;
        Pen p = (Pen)obj;
        return p.width == width && Objects.equals( p.stroke, stroke ) && Objects.equals( p.color, color );
    }
    
    @Override
    public Pen clone()
    {
        Color newColor = ( color != null ) ? new Color(color.getRGB()) : null;

        return new Pen(cloneStroke(stroke), newColor);
    }

    public static BasicStroke cloneStroke(BasicStroke stroke)
    {
        return new BasicStroke(stroke.getLineWidth(), stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke
                .getDashArray(), stroke.getDashPhase());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties
    //
    
    protected double width;
    public double getWidth()
    {
        return width;
    }

    public void setWidth(double width)
    {
        double oldValue = this.width;
        this.width = width;
        if( stroke == null )
        {
            stroke = new BasicStroke((float)width);
        }
        else
        {
            stroke = new BasicStroke((float)width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke
                    .getDashPhase());
        }
        json = null;
        firePropertyChange("width", oldValue, width);
    }

    public Pen withWidth(float thickness)
    {
        Pen pen = new Pen( stroke, color );
        pen.setWidth( thickness );
        return pen;
    }

    protected Color color;
    public Color getColor()
    {
        return color;
    }
    public void setColor(Color color)
    {
        Color oldValue = this.color;
        this.color = color;
        json = null;
        firePropertyChange("color", oldValue, color);
    }

    protected BasicStroke stroke;
    public BasicStroke getStroke()
    {
        return stroke;
    }
    public void setStroke(BasicStroke stroke)
    {
        BasicStroke oldValue = this.stroke;
        this.stroke = stroke;
        this.width = stroke.getLineWidth();
        json = null;
        firePropertyChange("stroke", oldValue, stroke);
    }



    ////////////////////////////////////////////////////////////////////////////
    // JSON
    //

    public static Pen createInstance(String str)
    {
        Pen pen = new Pen();
        try
        {
            pen.initFromJSON(new JSONObject(str));
            return pen;
        }
        catch( Exception e )
        {
            return pen;
        }
    }

    @Override
    public String toString()
    {
        try
        {
            return toJSON().toString();
        }
        catch( Exception e )
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
            JSONArray color = new JSONArray();
            Color clr = this.color;
            if(clr == null)
                clr = Color.BLACK;
            color.put(clr.getRed());
            color.put(clr.getGreen());
            color.put(clr.getBlue());
            color.put(clr.getAlpha());
            json.put("color", color);
            json.put("width", getWidth());
            if( this.stroke != null && this.stroke.getDashArray() != null )
            {
                json.put("dash", this.stroke.getDashArray());
            }
            if( this.stroke != null && this.stroke.getDashPhase() != 0 )
            {
                json.put("dashOffset", this.stroke.getDashPhase());
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
            int alpha = color.getInt(3);
            this.color = new Color(red, green, blue, alpha);

            float dashPhase = (float)from.optDouble("dashOffset", 0.0);
            float[] dashArray = null;
            JSONArray dashJson = from.optJSONArray("dash");
            if( dashJson != null )
            {
                dashArray = new float[dashJson.length()];
                for( int i = 0; i < dashJson.length(); i++ )
                    dashArray[i] = (float)dashJson.optDouble(i, 0.0);
            }
            setStroke(new BasicStroke((float)from.getDouble("width"), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dashArray,
                    dashPhase));
            setWidth((float)from.getDouble("width"));
        }
        catch( JSONException e )
        {
        }
    }

    public static BasicStroke fromString(String str)
    {
        try
        {
            if( str.equals("") )
                return new BasicStroke();
            String[] arr = ComplexTextView.split( str, ',' );
            String[] dashStringArr = arr[0].replace("[", "").split(",");
            float[] dashArray = new float[dashStringArr.length];
            for( int i = 0; i < dashArray.length; i++ )
            {
                dashArray[i] = Float.parseFloat(dashStringArr[i]);
            }
            float dashOffset = ( arr.length > 1 ) ? Float.parseFloat(arr[1]) : 0;

            return new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, dashArray, dashOffset);
        }
        catch( Exception ex )
        {
            return null;
        }
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