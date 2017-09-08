package ru.biosoft.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Container for {@link View} objects.
 */
public class CompositeView extends View implements Iterable<View>
{

    /**  Rectangle bound of this composite view */
    protected Rectangle rect = new Rectangle(0, 0, 0, 0);
    /**  Storage for child views */
    protected Vector<View> children = new Vector<>();
    /**  If this bit is cleared,then coordinates are used directly from x,y coordinates of view location */
    public static final int REL = 0x08; //
    /** Arrange mode, X coordinate is get from x coordinate of view location */
    public static final int X_UN = 0x00 + REL;
    /**  Arrange mode, view is arranged by left side to the right of previous rectangle*/
    public static final int X_RL = 0x01 + REL;
    /**  Arrange mode, view is arranged by center along X axis to the right of previous rectangle*/
    public static final int X_RC = 0x02 + REL;
    /**  Arrange mode, view is arranged by right side to the right side of previous rectangle*/
    public static final int X_RR = 0x03 + REL;
    /**  Arrange mode, view is arranged by left side to the left side of previous rectangle*/
    public static final int X_LL = 0x04 + REL;
    /**  Arrange mode, view is arranged by center along X axis to the left of previous rectangle*/
    public static final int X_LC = 0x05 + REL;
    /**  Arrange mode, view is arranged by right side to the left side of previous rectangle*/
    public static final int X_LR = 0x06 + REL;
    /**  Arrange mode, view is arranged by center along X axis to the center along X axis of previous rectangle*/
    public static final int X_CC = 0x07 + REL;
    /** Arrange mode, Y coordinate is get from y coordinate of view location */
    public static final int Y_UN = 0x00 + REL;
    /**  Arrange mode, view is arranged by top side to the top of previous rectangle*/
    public static final int Y_TT = 0x10 + REL;
    /**  Arrange mode, view is arranged by center along Y axis to the top of previous rectangle*/
    public static final int Y_TC = 0x20 + REL;
    /**  Arrange mode, view is arranged by bottom side  to the top of previous rectangle*/
    public static final int Y_TB = 0x30 + REL;
    /**  Arrange mode, view is arranged by top side to the bottom of previous rectangle*/
    public static final int Y_BT = 0x40 + REL;
    /**  Arrange mode, view is arranged by center along Y axis to the bottom of previous rectangle*/
    public static final int Y_BC = 0x50 + REL;
    /**  Arrange mode, view is arranged by bottom side to the bottom of previous rectangle*/
    public static final int Y_BB = 0x60 + REL;
    /**  Arrange mode, view is arranged by center along Y axis to the  center along Y axis of previous rectangle*/
    public static final int Y_CC = 0x70 + REL;

    /**  Constructs composite view  */
    public CompositeView()
    {
        super(null);
    }

    public CompositeView(JSONObject jsonObj)
    {
        super(null);
        initFromJSON(jsonObj);
    }

    /**
     * Scales all children of container in relation to old values.
     *
     * @param sx  the factor by which coordinates are scaled along the X axis direction
     * @param sy  the factor by which coordinates are scaled along the Y axis direction
     */
    @Override
    public void scale(double sx, double sy)
    {
        for(View v: children)
        {
            v.scale(sx, sy);
        }
        at.scale(sx, sy);
    }
    
    /**
     * Moves all children to the new location using specified offsets
     *
     * @param x offset along the X axis direction.
     * @param y offset along the Y axis direction.
     */
    @Override
    public void move(int x, int y)
    {
        for(View v: children)
        {
            v.move(x, y);
        }

        rect.translate(x, y);
    }

    /**
     * Synchronizes the <code>Rectangle</code> rectangle returned by {@link #getBounds()}
     * with composite view.
     */
    @Override
    public void updateBounds()
    {
        if( children.size() == 0 )
            rect = new Rectangle(0, 0, 0, 0);
        else
        {
            rect = null;
            for( View view : children )
            {
                if( view instanceof CompositeView )
                    ( (CompositeView)view ).updateBounds();

                if( rect == null )
                    rect = (Rectangle)view.getBounds().clone();
                else
                    rect.add(view.getBounds());
            }
        }
    }

    /**
     * Returns the view at the specified index.
     *
     * @param an index into this composite view.
     * @return the view at the specified index.
     */
    public View elementAt(int index)
    {
        return children.elementAt(index);
    }
    
    /**
     * Return size of composite view
     *
     * @return size of composite view
     */

    public int size()
    {
        return children.size();
    }

    @Override
    public Iterator<View> iterator()
    {
        return children.iterator();
    }

    /**
     * Inserts view in specified position.
     *
     * @param v inserted View
     * @param i target position
     * @exception ArrayIndexOutOfBoundsException if the index was invalid.
     */
    public void insert(View v, int i) throws ArrayIndexOutOfBoundsException
    {
        if( children.size() == 0 )
            rect = (Rectangle)v.getBounds().clone();
        else
            rect.add(v.getBounds());

        children.insertElementAt(v, i);
    }

    /**
     * Adds element with insets coordinates ( 0,0 )
     *
     * @param v
     * @param mode
     * @see #add(View v, int mode, Point insets)
     */
    public void add(View v, int mode)
    {
        add(v, mode, new Point(0, 0));
    }
    
    /**
     *  Adds new elements and arrange them relative the previous objects.
     *
     * @param v     specified View
     * @param mode  mode to arrange new element relative previous:
     *
     *  Abbreviations:
     *  <pre>
     *  X_RL
     *  | ||
     *  | |--- boundary of new element
     *  | ---- boundary of minimal rectangle, described all
     *  |      previous elements
     *  ------ x or y coordinate
     *
     *  X - the x coordinate:
     *   L - left boundary of the object
     *   C - center of the object
     *   R - right boundary of the object
     *
     *  Y - the y coordinate:
     *   T - top boundary of the object
     *   C - center of the object
     *   B - bottom boundary of the object
     *
     *   Special:
     *  UN  - don't change the corresponding x or y boundary of new element.
     *  REL - If this bit is cleared, target coordinates is used from location of view.
     * </pre>
     * @param insets  Determines the insets of this view in relation to the side of rectangle.<br>
     *                When mode along X axis is X_RR or X_LR added view shifts to the left. Otherwise it shifts to the right for the rest of modes.<br>
     *                When mode along Y axis is Y_BB or Y_TB added view shifts to the up. Otherwise it shifts to the down for the rest of modes.
     */
    public void add(View v, int mode, Point insets)
    {
        Rectangle r = v.getBounds();
        Rectangle rect = getBounds();
        int right = rect.x + rect.width; // right border of this object
        int bottom = rect.y + rect.height; // bottom border of this object

        if( insets == null )
            insets = new Point(0, 0);

        int x = 0;
        int y = 0;
        if( ( mode & REL ) == 0 )
        {
            x = r.x;
            y = r.y;
        }
        else
        {
            switch( mode & 0x0F )
            {
                case X_RL:
                    x = right + insets.x;
                    break;
                case X_RC:
                    x = right - r.width / 2 + insets.x;
                    break;
                case X_RR:
                    x = right - r.width - insets.x;
                    break;
                case X_LL:
                    x = rect.x + insets.x;
                    break;
                case X_LC:
                    x = rect.x - r.width / 2 + insets.x;
                    break;
                case X_LR:
                    x = rect.x - r.width - insets.x;
                    break;
                case X_CC:
                    x = rect.x + rect.width / 2 - r.width / 2 + insets.x;
                    break;
                case X_UN:
                default:
                    x = r.x + insets.x;
                    break;
            }
            switch( mode & 0x78 )
            {
                case Y_BT:
                    y = bottom + insets.y;
                    break;
                case Y_BC:
                    y = bottom - r.height / 2 + insets.y;
                    break;
                case Y_BB:
                    y = bottom - r.height - insets.y;
                    break;
                case Y_TT:
                    y = rect.y + insets.y;
                    break;
                case Y_TC:
                    y = rect.y - r.height / 2 + insets.y;
                    break;
                case Y_TB:
                    y = rect.y - r.height - insets.y;
                    break;
                case Y_CC:
                    y = rect.y + rect.height / 2 - r.height / 2 + insets.y;
                    break;
                case Y_UN:
                default:
                    y = r.y + insets.y;
                    break;
            }
        }

        v.setLocation(x, y);
        add(v);
    }

    /**
     * Tracer for remove
     *
     * @param v current view
     * @return <code>true</code> if view is found in current node of tree
     */
    protected boolean recursiveRemove(View v)
    {
        for( int i = 0; i < children.size(); i++ )
        {
            if( children.elementAt(i) == v )
            {
                children.remove(i);
                return true;
            }
            if( children.elementAt(i) instanceof CompositeView )
            {
                if( ( (CompositeView)children.elementAt(i) ).recursiveRemove(v) )
                    return true;
            }
        }
        return false;
    }

    /**
     * Removes specified view from composite view
     *
     * @param v specified view
     * @return <code>true</code> if view is removed or <code>false</code> otherwise.
     */
    public boolean remove(View v)
    {
        //recursive remove
        for( int i = 0; i < children.size(); i++ )
        {
            if( children.elementAt(i) == v )
            {
                children.remove(i);
                return true;
            }
        }

        return recursiveRemove(v);
    }


    /**
     * Adds the view to the children list and corrects
     * rectangle of composite view bound. The location of view is copied from view location
     *
     * @param v the added view
     */
    public void add(View v)
    {
        if( children.size() == 0 )
            rect = (Rectangle)v.getBounds().clone();
        else
            rect.add(v.getBounds());

        children.addElement(v);
    }

    /**
     * Returns rectangle bound of this composite view
     *
     * @return rectangle bound of this composite view
     */
    @Override
    public Rectangle getBounds()
    {
        return rect.getBounds();
    }

    /**
     * Tests if this composite view bound intersects the interior of a specified rectangle.
     *
     * @param rect specified rectangle
     * @return <code>true</code>  if intersects,<code>false</code> otherwise
     */
    @Override
    public boolean intersects(Rectangle rect)
    {
        if( isVisible() )
        {
            if( this.rect.intersects(rect) )
            {
                for(View v: children)
                {
                    if( v.intersects(rect) )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns priority of the view.
     * 
     * @param rect specified rectangle
     * @return priority
     */
    @Override
    public int getSelectionPriority(Rectangle rect)
    {
        int result = 0;
        for( View v : children )
        {
            int priority = v.getSelectionPriority(rect);
            if( priority > result )
            {
                result = priority;
            }
        }
        return result;
    }

    private Rectangle curRect = null;
    public final static int DELTA = 3;

    /**
     * Recursive tracer for {@link getDeepestActive(Point pt) }
     *
     * @param cv current composite view
     * @param ignoreModels ignore views whose modes are listed at ignoreModelsArray.
     * @param modelClass if this parameter is specified the view model should be
     * assignable to the specified class.
     */
    private View traceFor(CompositeView cv, Object[] ignoreModels, Class<?> modelClass, View maxView)
    {
        List<View> selectedViews = new ArrayList<>();
        for( int i = cv.size() - 1; i >= 0; i-- )
        {
            View v = cv.elementAt(i);
            View curMaxView = maxView;

            if( v.isActive() )
            {
                Rectangle bounds = v.getBounds();
                if( v.intersects(curRect) && ( maxView == null || maxView.getBounds().contains(bounds) )
                        && ( modelClass == null || modelClass.isInstance(v.getModel()) ) )
                {
                    boolean setUp = true;
                    if( ignoreModels != null )
                    {
                        for( Object ignoreModel : ignoreModels )
                        {
                            if( ignoreModel == v.getModel() )
                            {
                                setUp = false;
                                break;
                            }
                        }
                    }

                    if( setUp )
                    {
                        curMaxView = v;
                    }
                }
            }

            if( v instanceof CompositeView )
            {
                View childView = traceFor((CompositeView)v, ignoreModels, modelClass, curMaxView);
                if( childView != null )
                {
                    curMaxView = childView;
                }
            }

            if( curMaxView != null && curMaxView != maxView )
            {
                selectedViews.add(curMaxView);
            }
        }

        if( selectedViews.size() == 0 )
        {
            return maxView;
        }
        else
        {
            View result = selectedViews.get(0);
            int maxPriority = result.getSelectionPriority(curRect);
            Rectangle bounds = result.getBounds();
            int record = bounds.width * bounds.height;
            for( int i = 1; i < selectedViews.size(); i++ )
            {
                View v = selectedViews.get(i);
                int priority = v.getSelectionPriority(curRect);
                bounds = v.getBounds();
                if( priority > maxPriority )
                {
                    maxPriority = priority;
                    record = bounds.width * bounds.height;
                    result = v;
                }
                else if( priority == maxPriority )
                {
                    if( ( bounds.width * bounds.height ) < record )
                    {
                        record = bounds.width * bounds.height;
                        result = v;
                    }
                }
            }
            return result;
        }
    }


    /**
     * Returns deepest view in tree hierarchy, that has {@link #ACTIVE} state and intersects the specified point.
     *
     * @param pt specified point
     * @return deepest view in tree hierarchy, that has {@link #ACTIVE} state and intersects the specified point.
     */
    public View getDeepestActive(Point pt)
    {
        return getDeepestActive(pt, null, null);
    }
    
    /**
     * Sets pen recursively for all children ShapeView's
     * @param pen
     */
    public void setPen(Pen pen)
    {
        for(View child: this)
        {
            if(child instanceof ShapeView)
            {
                ((ShapeView)child).setPen(pen);
            } else if(child instanceof CompositeView)
            {
                ((CompositeView)child).setPen(pen);
            }
        }
    }
    
    /**
     * Sets brush recursively for all children ShapeView's
     * @param brush
     */
    public void setBrush(Brush brush)
    {
        for(View child: this)
        {
            if(child instanceof ShapeView)
            {
                ((ShapeView)child).setBrush(brush);
            } else if(child instanceof CompositeView)
            {
                ((CompositeView)child).setBrush(brush);
            }
        }
    }

    /**
     * Returns deepest view in tree hierarchy, that has {@link #ACTIVE} state and intersects the specified point.
     *
     * @param pt specified point
     * @param ignoreModels ignore views whose modes are listed at ignoreModelsArray.
     * @return deepest view in tree hierarchy, that has {@link #ACTIVE} state and intersects the specified point.
     * @param modelClass if this parameter is specified the view model should be
     * assignable to the specified class.
     */
    public View getDeepestActive(Point pt, Object[] ignoreModels, Class<?> modelClass)
    {
        curRect = new Rectangle(pt.x - DELTA, pt.y - DELTA, 2 * DELTA, 2 * DELTA);
        View maxView = null;
        if( isActive() )
            maxView = this;

        return traceFor(this, ignoreModels, modelClass, maxView);
    }


    /**
     * Returns the location of this <code>Rectangle</code> bound.
     *
     * @return the location of this <code>Rectangle</code> bound.
     */
    public Point getLocation()
    {
        return rect.getLocation();
    }

    /**
     * Paints this composite view on specified Graphics2D.
     *
     * @param g2 specified Graphics2D.
     */
    @Override
    public void paint(Graphics2D g)
    {
        Rectangle clip = g.getClipBounds();
        if( clip != null )
        {
            Rectangle rect = getBounds();
            if( !rect.intersects(clip) )
                return;
        }

        if( isVisible() )
        {
            for(View v: children)
            {
                v.paint(g);
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if( obj == null || ! ( obj instanceof CompositeView ) )
            return false;

        if( obj == this )
            return true;

        if( !super.equals(obj) )
            return false;

        CompositeView v = (CompositeView)obj;
        if( children.size() != v.children.size() )
            return false;

        for( int i = 0; i < children.size(); i++ )
        {
            if( !children.get(i).equals(v.children.get(i)) )
                return false;
        }
        
        return true;
    }

    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        JSONArray childArray = new JSONArray();
        View[] childrenArray = children.toArray(new View[children.size()]);
        for( View childView : childrenArray )
        {
            if(childView.isVisible())
                childArray.put(childView.toJSON());
        }
        result.put("children", childArray);

        return result;
    }

    @Override
    public JSONObject toJSONIfChanged(View v) throws JSONException
    {
        if( equals(v) )
            return ( new DummyView(model, isActive()) ).toJSON();
        if( ! ( v instanceof CompositeView ) )
            return toJSON();
        JSONObject result = super.toJSON();
        CompositeView cv = (CompositeView)v;
        int start = 0, length = cv.children.size();
        if(length == 0)
            return toJSON();
        JSONArray childArray = new JSONArray();
        View[] childrenArray = children.toArray(new View[children.size()]);
        for( View childView : childrenArray )
        {
            if(!childView.isVisible())
                continue;
            Object newModel = childView.getModel();
            if( newModel == null )
            {
                childArray.put(childView.toJSON());
            }
            else
            {
                boolean found = false;
                int i = start;
                do
                {
                    View oldChildView = cv.children.get(i);
                    if(oldChildView.isVisible())
                    {
                        Object oldModel = oldChildView.getModel();
                        if( oldModel != null && newModel == oldModel )
                        {
                            childArray.put(childView.toJSONIfChanged(oldChildView));
                            start = ( i + 1 ) % length;
                            found = true;
                            break;
                        }
                    }
                    i = ( i + 1 ) % length;
                }
                while( i != start );
                if( !found )
                {
                    childArray.put(childView.toJSON());
                }
            }
        }
        result.put("children", childArray);

        return result;
    }

    @Override
    protected void initFromJSON(JSONObject from)
    {
        super.initFromJSON(from);

        try
        {
            JSONArray childArray = from.getJSONArray("children");
            for( int i = 0; i < childArray.length(); ++i )
            {
                JSONObject childObj = childArray.getJSONObject(i);

                View child = fromJSON(childObj);
                if( child != null )
                    add(child);
            }
        }
        catch( JSONException e )
        {}
    }

}
