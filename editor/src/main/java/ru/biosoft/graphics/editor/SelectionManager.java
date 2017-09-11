package ru.biosoft.graphics.editor;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import one.util.streamex.StreamEx;

import ru.biosoft.graphics.ArrowView;
import ru.biosoft.graphics.View;

public class SelectionManager extends ViewPaneAdapter implements ViewPaneLayer
{

    protected ViewPane viewPane;

    ////////////////////////////////////////////////////////////////////////////
    // Constructor
    //

    public SelectionManager(ViewPane viewPane)
    {
        this.viewPane = viewPane;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Info and access methods
    //

    protected ViewSelector selector = new BoxSelector();
    public ViewSelector getViewSelector()
    {
        return selector;
    }
    public void setViewSelector(ViewSelector selector)
    {
        this.selector = selector;
    }

    protected Vector<View> selectedViews = new Vector<>();

    public boolean contains(View view)
    {
        return selectedViews.contains(view);
    }

    public int getSelectedViewCount()
    {
        return selectedViews.size();
    }

    public View getSelectedView(int i)
    {
        return selectedViews.get(i);
    }

    public Rectangle getBounds()
    {
        if( selectedViews.size() == 0 )
            return null;

        Rectangle result = getSelectedView(0).getBounds();
        for( int i = 1; i < selectedViews.size(); i++ )
        {
            result = result.union(getSelectedView(i).getBounds());
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Selection issues
    //

    /** Removes all selection */
    public void clearSelection()
    {
        selectedViews.clear();
        selectedModels.clear();
    }

    public void selectView(View view, boolean exclusively)
    {
        if( exclusively )
        {
            selectedViews.clear();
            selectedModels.clear();
        }
        else
        {
            if( selectedViews.contains(view) )
            {
                return;
            }
        }

        selectedViews.add(view);
        Object model = view.getModel();
        if( model != null )
            selectedModels.add(model);
    }

    protected Set<Object> selectedModels = new HashSet<>();
    public Object[] getSelectedModels()
    {
        return selectedModels.toArray();
    }

    public View[] selectModel(Object model, boolean exclusively)
    {
        if( exclusively )
        {
            selectedViews.clear();
            selectedModels.clear();
        }
        View[] views = viewPane.getView(model);

        if( selectedModels.contains(model) )
        {
            return views;
        }
        if( views != null && views.length > 0 )
        {
            boolean isEdge = false;
            for( View view : views )
            {
                if( view instanceof ArrowView )
                {
                    selectView(view, false);
                    isEdge = true;
                }
            }
            if( !isEdge )
            {
                for( View view : views )
                {
                    selectView(view, false);
                }
            }
        }
        return views;
    }

    public View[] selectModels(Object[] models, boolean exclusively)
    {
        if( exclusively )
        {
            selectedViews.clear();
            selectedModels.clear();
        }
        return StreamEx.of( models ).flatMap( model -> Arrays.stream( selectModel( model, false ) ) ).toArray( View[]::new );
    }

    protected ViewEditorHelper helper = null;

    @Override
    public void paintLayer(Graphics2D g2)
    {
        if( getSelectedViewCount() == 1 && null != helper && helper.isResizable(getSelectedView(0)) )
        {
            setViewSelector(new ResizingBoxSelector());
        }
        else
        {
            setViewSelector(new BoxSelector());
        }

        for( View view : selectedViews )
        {
            if( view.isVisible() && view.isActive() )
            {
                if( view instanceof ArrowView )
                {
                    new PathSelector().paint(g2, view);
                }
                else
                {
                    selector.paint(g2, view);
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // MouseEvent processing
    //

    /**
     * TODO: selection support
     */
    @Override
    public void mousePressed(ViewPaneEvent e)
    {
        View view = e.getViewSource();
        if( view == null )
            return;

        selectView(view, true);
        viewPane.repaint();
    }
}
