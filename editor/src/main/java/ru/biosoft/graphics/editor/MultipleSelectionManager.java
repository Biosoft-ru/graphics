package ru.biosoft.graphics.editor;

import java.awt.Point;
import java.awt.Rectangle;

import ru.biosoft.graphics.View;

/**
 *
 */
public class MultipleSelectionManager extends SelectionManager
{
    boolean wasDragged = false;
    ////////////////////////////////////////////////////////////////////////////
    // Constructor
    //

    public MultipleSelectionManager(ViewPane viewPane)
    {
        super(viewPane);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Overriding MouseEvent processing
    //

    /**
     * @pending multiple selection support
     */
    @Override
    public void mousePressed(ViewPaneEvent e)
    {
        View view = getSelectedViewForEvent( e );
        if(view == null)
            return;

        boolean modifier = (e.isControlDown() || e.isShiftDown());

        if(modifier && selectedViews.contains(view))
        {
            selectedViews.remove(view);
            selectedModels.remove(view.getModel());
            viewPane.repaint();
        }
        else if(modifier)
        {
            selectView(view, false);
        }
        else if(selectedViews.size() == 0)
        {
            selectView(view, false);
        }
        else if(!selectedViews.contains(view))
        {
            selectView(view, true);
        }
    }
    
    @Override
    public void mouseReleased(ViewPaneEvent e)
    {
        if(this.wasDragged)
        {
            wasDragged = false;
            return;
        }
        
        View view = getSelectedViewForEvent( e );
        if( view == null )
            return;

        boolean modifier = ( e.isControlDown() || e.isShiftDown() );
        if( !modifier )
            selectView( view, true );
    }
    
    @Override
    public void mouseDragged(ViewPaneEvent e)
    {
        wasDragged = true;
    }
    
    private View getSelectedViewForEvent(ViewPaneEvent e)
    {
        View view = getSelectedElement( e.getPoint() );
        if( view == null )
            view = e.getViewSource();
        return view;
    }

    private View getSelectedElement(Point point)
    {
        for(Object obj: selectedViews)
        {
            View view = (View)obj;
            Rectangle rectInside = (Rectangle)view.getBounds().clone();
            Rectangle rectOutside = (Rectangle)rectInside.clone();
            rectOutside.grow(4, 4);
            if(rectOutside.contains(point) && !rectInside.contains(point))
                return view;
        }
        return null;
    }

    @Override
    public void mouseClicked(ViewPaneEvent e)
    {
        View view = e.getViewSource();
        if(view == null)
        {
            return;
        }

        boolean modifier = (e.isControlDown() || e.isShiftDown());

        if(!modifier)
        {
            selectView(view, true);
        }
    }
}
