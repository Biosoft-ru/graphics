package ru.biosoft.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.lang.reflect.Constructor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Basic primitive to represent view.
 */
public abstract class View
{
    /** Shape of view */
    protected Shape shape = null;
    
    /**
     * Type of view.
     * <ul> Type can be:
     *   <li>{@link #ACTIVE}</li>
     *   <li>{@link #HIDE}</li>
     *   <li>{@link #SELECTABLE}</li>
     * </ul>
     */
    protected int type = 0;
    
    /**
     * The model of the view. Object for which view is created
     */
    protected Object model = null;
    
    /**
     * Description for this view. Normally it should be displayed as floating tooltip when moving the mouse over the view.
     */
    protected String description = null;

    /**
    *  Affine transformer of view
    */
    protected AffineTransform at;
    
    /**
     * Bit field of {@link #type}.It is set, if the view can be manipulated.
     */
    public static final int ACTIVE = 4;

    /**
     * Bit field of {@link #type}.It is set, if the view can be selected.
     * Selectable view presumes active view.
     */
    public static final int SELECTABLE = 16;

    /**
     * Bit field of {@link #type}.It is set, if the view has visible state.
     */
    public static final int HIDE = 8;
    
    /** Alignment mode. */
    public static final int LEFT = 0;
    
    /** Alignment mode. */
    public static final int RIGHT = 1;
    
    /** Alignment mode. */
    public static final int CENTER = 2;
    
    /** Alignment mode. */
    public static final int BOTTOM = 8;

    /** Alignment mode. */
    public static final int TOP = 16;

    /** Alignment mode. */
    public static final int BASELINE = 0;

    /**
     * Constructs View using specified Shape.
     *
     * @param shape Shape of view
     * @see Shape
     */
    public View(Shape shape)
    {
        this.shape = shape;
        at = AffineTransform.getScaleInstance(1.0, 1.0);
        setToScale(1.0, 1.0);
    }

    /**
     * Returns a model for view.
     *
     * @return model for view
     * @see #model
     */
    public Object getModel()
    {
        return model;
    }

    /**
     * Sets a model for the view.
     *
     * @param model new model
     * @see #model
     */
    public void setModel(Object model)
    {
        this.model = model;
    }
    
    /**
     * Returns an integer {@link Rectangle} that completely encloses the
     * <code>View</code>.
     *
     * @return an integer {@link Rectangle} that completely encloses the
     * <code>View</code>.
     */
    public Rectangle getBounds()
    {
        return shape.getBounds();
    }
    
    /**
     * Returns shape of
     * <code>View</code>.
     *
     * @return Shape of the <code>View</code>.
     */
    public Shape getShape()
    {
        return shape;
    }
    
    /**
     * Synchronizes the <code>Rectangle</code> rectangle returned by {@link #getBounds()}
     * with view. Base implementation of method does nothing.
     */
    public void updateBounds() {}

    /**
     * Tests if this View's shape intersects the interior of a specified rectangle.
     *
     * @param rect specified rectangle
     * @return <code>true</code>  if intersects,<code>false</code> otherwise
     */
    public boolean intersects(Rectangle rect)
    {
        return ( shape == null ) ? false : shape.intersects(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Returns priority of the view.
     * 
     * @param rect specified rectangle
     * @return priority
     */
    public int getSelectionPriority(Rectangle rect)
    {
        return 0;
    }

    /**
     * Set the location of the upper left corner of the view x, y coordinates.
     *
     * @param x new coordinate along X axis
     * @param y new coordinate along Y axis
     */
    public void setLocation(int x, int y)
    {
        Rectangle r = getBounds();
        move(new Point(x - r.x, y - r.y));
    }
    /**
     * Set the location of the upper left corner of the view x, y coordinates.
     *
     * @param pt Point of new location
     */
    public void setLocation(Point pt)
    {
        setLocation(pt.x, pt.y);
    }

    /**
     * Moves View to the new location using specified offsets.
     *
     * @param x offset along the X axis direction.
     * @param y offset along the Y axis direction.
     */
    abstract public void move(int x, int y);

    /**
     * Moves View to the new location using specified offset.
     *
     * @param offset Point that units both coordinates.
     */
    public void move(Point offset)
    {
        move(offset.x, offset.y);
    }

    /**
     * Scales view (absolutely).
     *
     * @param factorX sets factor along the X axis direction
     * @param factorY sets factor along the Y axis direction
     */
    public void setToScale(double factorX, double factorY)
    {
        at.setToScale(factorX, factorY);
    }
    
    /**
     * Scales view in relation to old values.
     *
     * @param sx  the factor by which coordinates are scaled along the X axis direction
     * @param sy  the factor by which coordinates are scaled along the Y axis direction
     */
    public void scale(double sx, double sy)
    {
        double ssx = at.getScaleX();
        double ssy = at.getScaleY();
        setToScale(sx * ssx, sy * ssy);
    }
    
    /**
     * Returns <code>true</code> if view is active
     *
     * @return <code>true</code> if view is active
     */
    public boolean isActive()
    {
        return ( type & ACTIVE ) != 0;
    }
    
    /**
     * Sets active and selectable states of view.
     *
     * @param isActive new active value
     */
    public void setActive(boolean isActive)
    {
        if( isActive )
            type |= ACTIVE | SELECTABLE;
        else
            type &= ~ACTIVE & ~SELECTABLE;
    }
    
    /**
     * Returns <code>true</code> if view is selectable
     *
     * @return <code>true</code> if view is selectable
     */
    public boolean isSelectable()
    {
        return ( type & SELECTABLE ) != 0;
    }
    
    /**
     * Sets selectable state of view.
     *
     * @param isSelectable new selectable value
     */
    public void setSelectable(boolean isSelectable)
    {
        if( isSelectable )
            type |= SELECTABLE;
        else
            type &= ~SELECTABLE;
    }
    
    /**
     * Returns <code>true</code> if view is visible
     *
     * @return <code>true</code> if view is visible
     */
    public boolean isVisible()
    {
        return ( type & HIDE ) == 0;
    }

    /**
     * Sets visible state of view
     *
     * @param isVisible new value of visible flag
     */
    public void setVisible(boolean isVisible)
    {
        if( !isVisible )
            type |= HIDE;
        else
            type &= ~HIDE;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }
    
    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Paints this view on specified Graphics2D.
     *
     * @param g2 specified Graphics2D.
     */
    public void paint(Graphics2D g2)
    {
        if( isVisible() )
        {
            g2.draw(shape);
        }
    }

    @Override
    public int hashCode()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj)
    {
        if( obj == null || ! ( obj instanceof View ) )
            return false;
        if( obj == this )
            return true;
        View v = (View)obj;
        if( type != v.type )
            return false;
        if( ( model == null ^ v.model == null ) || ( model != null && !model.toString().equals(v.model.toString()) ) )
            return false;
        if( ( shape == null ^ v.shape == null ) || ( shape != null && !shape.getBounds().equals(v.shape.getBounds()) ) )
            return false;
        return true;
    }

    /**
     * Transform view state to JSON object
     * 
     * @return {@link JSONObject}
     */
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = new JSONObject();
        result.put("type", String.valueOf(type));

        if( getDescription() != null )
            result.put("description", getDescription());

        String className = getClass().getName();
        int ind = className.lastIndexOf('.');
        if( ind != -1 )
        {
            className = className.substring(ind + 1);
        }
        result.put("class", className);

        Object model = getModel();
        View.ModelResolver resolver = View.getModelResolver(); 
        if( model != null && resolver != null )
        {
        	String str = resolver.toString(model);
        	if( str != null )
        		result.put("model", str);
        }

        return result;
    }

    public JSONObject toJSONIfChanged(View v) throws JSONException
    {
        if( !equals(v) )
            return toJSON();

        return ( new DummyView(model, isActive()) ).toJSON();
    }
    
    protected void initFromJSON(JSONObject from)
    {
        try
        {
            type = from.getInt("type");
            if( from.has("description") )
                setDescription(from.getString("description"));

            if( from.has("model") )
            {
            	View.ModelResolver resolver = View.getModelResolver();
            	if( resolver != null )
            		setModel( resolver.fromString(from.getString("model")) );
            }
        }
        catch( JSONException e )
        {}
    }
    
    public static View fromJSON(JSONObject childObj)
    {
        View child = null;
        try
        {
            String className = childObj.getString("class");
            int ind = View.class.getName().lastIndexOf(".");
            Class<?> c = View.class.getClassLoader().loadClass(View.class.getName().substring(0, ind + 1) + className);
            Constructor<?> constructor = c.getConstructor(JSONObject.class);
            child = (View)constructor.newInstance(new Object[] {childObj});
        }
        catch( Exception e )
        {
        }
        return child;
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

    // /////////////////////////////////////////////////////////////////////////
    // Issues related to serialization model to JSON
    //
    
    public static interface ModelResolver
    {
        public String toString(Object model);
    	public Object fromString(String name);
    }
    
    private static ModelResolver modelResolver;
    public static  ModelResolver getModelResolver()
    {
    	return modelResolver;
    }
    
    public static void setModelResolver(ModelResolver modelResolver)
    {
    	View.modelResolver = modelResolver;
    }

}
