package ru.biosoft.graphics.editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import ru.biosoft.graphics.CompositeView;
import ru.biosoft.graphics.View;

public class RectangleSelectionManager extends ResizableSelectionManager
{
    private static final Color SELECTION_COLOR = new Color(255, 255, 100, 100);
    protected boolean startSelection = false;
    protected Point start;
    protected Rectangle curRect;

    public RectangleSelectionManager(ViewPane viewPane, ViewEditorHelper helper)
    {
        super(viewPane, helper);
    }

    @Override
    public void mouseDragged(ViewPaneEvent e)
    {
        super.mouseDragged( e );
        if( start != null )
        {
            if( startSelection )
            {

                Rectangle newRect = makeRectangle(start, e.getPoint());
                Rectangle repaintRect = new Rectangle(newRect);
                if(curRect != null)
                    repaintRect.add(curRect);
                ViewPane viewPane = e.getViewPane();
                double sx = viewPane.getScaleX();
                double sy = viewPane.getScaleY();
                Point offset = viewPane.offset;
                if( offset != null )
                {
                    repaintRect.x -= offset.x;
                    repaintRect.y -= offset.y;
                }
                repaintRect.x *= sx;
                repaintRect.y *= sy;
                repaintRect.width *= sx;
                repaintRect.height *= sy;
                repaintRect.grow(1, 1);
                curRect = newRect;
                viewPane.mPanel.repaint(repaintRect);
            }
            else if( e.isControlDown() )
            {
                clearSelection();

                viewPane.repaint();
                startSelection = true;
                if( e.getViewPane() instanceof ViewEditorPane )
                    ( (ViewEditorPane)e.getViewPane() ).setSelectionEnabled(false);
            }
        }
        else
        {
            super.mouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(ViewPaneEvent e)
    {
        super.mouseReleased(e);

        if( startSelection )
        {
            if( e.getViewPane() instanceof ViewEditorPane )
                ( (ViewEditorPane)e.getViewPane() ).setSelectionEnabled(true);

            startSelection = false;
            Rectangle rect = makeRectangle(start, e.getPoint());
            makeSelections(e.getViewPane().getView(), rect, null);
        }
        curRect = null;
    }

    /**
     * New selection logic:
     * 1. Only views which are fully contained in selection rectangle are selected
     * 2. If view is selected then all its inner views will not be selected
     */
    protected void makeSelections(View view, Rectangle rect, Object parentModel)
    {
        if( view.getModel() != null && view.getModel() != parentModel && rect.contains(view.getBounds()) )
        {
            selectView(view, false);
            return;
        }

        if (!(view instanceof CompositeView))
            return;
        
        for( View child : (CompositeView)view )
            makeSelections(child, rect, view.getModel());
    }

    @Override
    public void mousePressed(ViewPaneEvent e)
    {
        super.mousePressed(e);
        if( e.isControlDown() )
        {
            start = e.getPoint();
        }
    }

    protected Rectangle makeRectangle(Point first, Point second)
    {
        int width = first.x - second.x;
        int height = first.y - second.y;
        return new Rectangle(first.x < second.x ? first.x : second.x, first.y < second.y ? first.y : second.y, width < 0 ? -width : width,
                height < 0 ? -height : height);
    }

    @Override
    public void paintLayer(Graphics2D g2)
    {
        super.paintLayer(g2);
        if(curRect != null)
        {
            g2.setColor(SELECTION_COLOR);
            g2.fillRect(curRect.x, curRect.y, curRect.width, curRect.height);
        }
    }
}
