package ru.biosoft.graphics.editor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.developmentontheedge.beans.ActionsProvider;

import ru.biosoft.graphics.CompositeView;
import ru.biosoft.graphics.View;
import ru.biosoft.graphics.editor.GridOptions.GridStyle;

/**
 * Panel for visualisation of {@link View}s. This <code>Component</code> can be used
 * in Dialogs or Frames.
 *
 * It is also provides following functionality:
 * 1) zoom support
 * 2) mouse event dispatching
 * 3) selection (through {$link SelectionManager}).
 */
@SuppressWarnings ( "serial" )
public class ViewPane extends JPanel implements MouseListener, MouseMotionListener
{
    ////////////////////////////////////////////////////////////////////////////
    // Internal variables (not properties)
    //

    /** Panel on which all views are painted */
    protected MPanel mPanel;

    /** Transformer for scaling of visual representation of all views */
    protected AffineTransform at = AffineTransform.getScaleInstance(1.0, 1.0);
    protected Point offset = new Point(0, 0);

    private static final int SCROLL_DELTA = 15;
    private JScrollBar barHor = null;
    private JScrollBar barVert = null;
    protected JScrollPane scrollPane = null;
    private boolean allignToX = false;
    private boolean allignToY = false;
    private final List<ViewPaneLayer> layers = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////
    // Constructor
    //

    public ViewPane()
    {
        mPanel = new MPanel(true);

        // scroll pane issues
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(mPanel);
        scrollPane.setDoubleBuffered(true);

        barHor = scrollPane.getHorizontalScrollBar();
        barVert = scrollPane.getVerticalScrollBar();
        barHor.setUnitIncrement(SCROLL_DELTA);
        barVert.setUnitIncrement(SCROLL_DELTA);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        // forwarding mouse events
        mPanel.addMouseListener(this);
        mPanel.addMouseMotionListener(this);

        addViewPaneListener(new PopupListener());

        SelectionManager selectionManager = new SelectionManager(this);
        selectionManager.setViewSelector(new BoxSelector());

        setSelectionManager(selectionManager);

        // set up empty composite view
        cView = new CompositeView();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties
    //

    /** Storage for views */
    protected CompositeView cView;
    public CompositeView getView()
    {
        return cView;
    }

    /**
     * Set up the view */
    public void setView(CompositeView value)
    {
        setView(value, null);
    }

    public void setView(CompositeView value, Point offset)
    {
        cView = value;
        if( value != null )
        {
            Rectangle rect = (Rectangle)cView.getBounds().clone();
            if( offset != null )
            {
                rect.x -= offset.x;
                rect.y -= offset.y;
            }
            this.offset = rect.getLocation();
        }

        if( selectionManager != null )
        {
            Object[] models = selectionManager.getSelectedModels();
            selectionManager.clearSelection();
            selectionManager.selectModels(models, false);
        }

        updateScrollBars();
        repaint();
    }

    protected GridOptions gridOptions;

    public GridOptions getGridOptions()
    {
        return gridOptions;
    }

    public void setGridOptions(GridOptions gridOptions)
    {
        this.gridOptions = gridOptions;
    }
    
    public void addLayer(ViewPaneLayer layer)
    {
        layers.add(layer);
    }
    
    public void removeLayer(ViewPaneLayer layer)
    {
        layers.remove(layer);
    }

    protected SelectionManager selectionManager;
    public SelectionManager getSelectionManager()
    {
        return selectionManager;
    }

    public void setSelectionManager(SelectionManager selectionManager)
    {
        removeViewPaneListener(this.selectionManager);
        this.selectionManager = selectionManager;
        addViewPaneListener(this.selectionManager);

        repaint();
    }

    protected ActionsProvider popupActionsProvider;
    public ActionsProvider getPopupActionsProvider()
    {
        return popupActionsProvider;
    }

    public void setPopupActionsProvider(ActionsProvider popupActionsProvider)
    {
        this.popupActionsProvider = popupActionsProvider;
    }

    ////////////////////////////////////////////////////////////////////////////
    // View event listener issues
    //

    /** listeners storage */
    protected Vector<ViewPaneListener> viewPaneListeners = new Vector<>();

    public void addViewPaneListener(ViewPaneListener viewPaneListener)
    {
        viewPaneListeners.addElement(viewPaneListener);
    }

    public void removeViewPaneListener(ViewPaneListener viewPaneListener)
    {
        viewPaneListeners.removeElement(viewPaneListener);
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    //
    @Override
    public void addMouseListener(MouseListener mouseListener)
    {
        mPanel.addMouseListener(mouseListener);
    }

    @Override
    public void removeMouseListener(MouseListener mouseListener)
    {
        mPanel.removeMouseListener(mouseListener);
    }

    @Override
    public void addMouseMotionListener(MouseMotionListener mouseListener)
    {
        mPanel.addMouseMotionListener(mouseListener);
    }

    @Override
    public void removeMouseMotionListener(MouseMotionListener mouseListener)
    {
        mPanel.removeMouseMotionListener(mouseListener);
    }
    
    @Override
    public void addMouseWheelListener(MouseWheelListener l)
    {
        mPanel.addMouseWheelListener(l);
    }

    @Override
    public void removeMouseWheelListener(MouseWheelListener l)
    {
        mPanel.removeMouseWheelListener(l);
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    //

    public void setSizeAllign(boolean x, boolean y)
    {
        allignToX = x;
        allignToY = y;
    }

    @Override
    public void repaint()
    {
        super.repaint();

        if( mPanel != null )
            mPanel.repaint();
    }

    public void scale(double sx, double sy)
    {
        setToScale(sx * at.getScaleX(), sy * at.getScaleY());
    }

    public void setToScale(double sx, double sy)
    {
        at.setToScale(sx, sy);
        updateScrollBars();
        mPanel.repaint();

        final int ADDER = 5;

        // checking scrollbars
        if( allignToX )
        {
            if( barVert.isVisible() )
            {
                sx = sx * getBounds().width / ( getBounds().width + barVert.getBounds().width + ADDER );
                sy = sy * getBounds().width / ( getBounds().width + barVert.getBounds().width + ADDER );
            }
            else
            {
                sx = sx * getBounds().width / ( getBounds().width + ADDER );
                sy = sy * getBounds().width / ( getBounds().width + ADDER );
            }
            if( allignToY )
                at.setToScale(sx, sy);
            else
                at.setToScale(sx, sx);
            updateScrollBars();
            barHor.setVisible(false);
        }
        else
        {
        }
        if( allignToY )
        {
            if( barHor.isVisible() )
            {
                sx = sx * getBounds().height / ( getBounds().height + barHor.getBounds().height + ADDER );
                sy = sy * getBounds().height / ( getBounds().height + barHor.getBounds().height + ADDER );
            }
            else
            {
                sx = sx * getBounds().height / ( getBounds().height + ADDER );
                sy = sy * getBounds().height / ( getBounds().height + ADDER );
            }
            if( allignToX )
                at.setToScale(sx, sy);
            else
                at.setToScale(sy, sy);
            updateScrollBars();
            barVert.setVisible(false);
        }
        mPanel.repaint();
    }

    public double getScaleX()
    {
        return at.getScaleX();
    }

    public double getScaleY()
    {
        return at.getScaleY();
    }


    public void updateScrollBars()
    {
        if( cView == null )
            return;
        Rectangle r = cView.getBounds();

        Dimension dm = new Dimension((int) ( ( r.width + 4 ) * at.getScaleX() ), (int) ( ( r.height + 4 ) * at.getScaleY() ));
        mPanel.setPreferredSize(dm);
        mPanel.setMinimumSize(dm);
        mPanel.setMaximumSize(dm);
        mPanel.setSize(dm);

        barHor.revalidate();
        barVert.revalidate();
    }

    /**
     * Set policy for scroll pane
     */
    public void setScrollPolicy(int horizontal, int vertical)
    {
        scrollPane.setHorizontalScrollBarPolicy(horizontal);
        scrollPane.setVerticalScrollBarPolicy(vertical);
    }

    ///////////////////////////////////////////////////
    //  Model to view mapping
    //

    private Object findModel;

    private Collection<View> traceFor(View v)
    {
        HashSet<View> views = new HashSet<View>();
        if( v.isActive() && v.getModel() == findModel )
        {
            views.add(v);
        }
        else if( v instanceof CompositeView )
        {
            for(View vv: (CompositeView)v)
            {
                Collection<View> col = traceFor(vv);
                views.addAll(col);
            }

        }
        return views;
    }

    /**
     * TODO: take into account that many views can have the same model
     */
    public View[] getView(Object model)
    {
        findModel = model;
        ArrayList<View> vec = new ArrayList<View>(traceFor(cView));
        return vec.toArray(new View[] {});
    }

    
    /**
     * Converts mouse coordinates to view coordinates
     * @param pt
     * @return
     */
    public Point clientToView(Point pt)
    {
        Point result = (Point)pt.clone();
        result.x /= at.getScaleX();
        result.y /= at.getScaleY();
        result.x += offset.x;
        result.y += offset.y;
        return result;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Mouse dispatching issues
    //
    
    protected ViewPaneEvent prepareEvent(MouseEvent e)
    {
        Point pt = clientToView(e.getPoint());

        return new ViewPaneEvent(this, cView.getDeepestActive(pt), e, pt.x, pt.y);
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        ViewPaneEvent viewPaneEvent = prepareEvent(e);
        for( ViewPaneListener vpl : viewPaneListeners )
        {
            vpl.mouseClicked(viewPaneEvent);
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        ViewPaneEvent viewPaneEvent = prepareEvent(e);
        for( ViewPaneListener vpl : getListeners() )
        {
            vpl.mousePressed(viewPaneEvent);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        ViewPaneEvent viewPaneEvent = prepareEvent(e);
        for( ViewPaneListener vpl : getListeners() )
        {
            vpl.mouseReleased(viewPaneEvent);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        ViewPaneEvent viewPaneEvent = prepareEvent(e);
        for( ViewPaneListener vpl : getListeners() )
        {
            vpl.mouseEntered(viewPaneEvent);
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        ViewPaneEvent viewPaneEvent = prepareEvent(e);
        for( ViewPaneListener vpl : getListeners() )
        {
            vpl.mouseExited(viewPaneEvent);
        }
    }

    /**
     * @pending - check
     */
    @Override
    public void mouseDragged(MouseEvent e)
    {
        final Rectangle tmpRect = new Rectangle();
        scrollPane.getBounds(tmpRect);

        int val = barHor.getValue();
        if( e.getPoint().x < val )
            val -= SCROLL_DELTA;
        if( e.getPoint().x > val + tmpRect.width )
            val += SCROLL_DELTA;
        barHor.setValue(val);

        val = barVert.getValue();
        if( e.getPoint().y < val )
            val -= SCROLL_DELTA;
        if( e.getPoint().y > val + tmpRect.height )
            val += SCROLL_DELTA;
        barVert.setValue(val);

        ViewPaneEvent viewPaneEvent = prepareEvent(e);
        for( ViewPaneListener vpl : getListeners() )
        {
            vpl.mouseDragged(viewPaneEvent);
        }
    }

    /**
     * @pending - check
     */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        ViewPaneEvent viewPaneEvent = prepareEvent(e);
        for( ViewPaneListener vpl : getListeners() )
        {
            vpl.mouseMoved(viewPaneEvent);
        }
    }

    protected ViewPaneListener[] getListeners()
    {
        return viewPaneListeners.toArray(new ViewPaneListener[viewPaneListeners.size()]);
    }

    public JComponent getContent()
    {
        return new MPanel(false);
    }

    public void resetScrollBars()
    {
        barHor.setValue(0);
        barVert.setValue(0);
    }

    @Override
    public void setBackground(Color color)
    {
        if( mPanel != null )
            mPanel.setBackground(color);
    }
    
    public Rectangle getViewportBounds()
    {
        return scrollPane.getViewportBorderBounds();
    }

    /**
     * Context menu support
     */
    protected class PopupListener extends ViewPaneAdapter
    {
        protected JPopupMenu popup = null;

        @Override
        public void mouseReleased(ViewPaneEvent e)
        {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(ViewPaneEvent e)
        {
            // this used instead of  !e.isPopupTrigger() because the latter does not work under Linux
            if( !SwingUtilities.isRightMouseButton(e) )
            {
                return;
            }

            initPopupMenu(e);

            int x =  (int) ( e.getX() * at.getScaleX() );
            int y = (int) ( e.getY() * at.getScaleY() );
            if( offset != null )
            {
                x -= offset.x;
                y -= offset.y;
            }
            
            if( popup != null )
                popup.show(e.getComponent(), x, y);
        }

        private void initPopupMenu(ViewPaneEvent e)
        {
            popup = null;

            if( popupActionsProvider == null || e.getViewSource() == null || e.getViewSource().getModel() == null )
                return;

            Action[] actions = popupActionsProvider.getActions(e);
            if( actions == null || actions.length == 0 )
                return;

            popup = new JPopupMenu();

            for( Action a : actions )
            {
                if( a == null )
                {
                    popup.addSeparator();
                    continue;
                }

                JMenuItem menuItem = popup.add(a);
                menuItem.setActionCommand((String)a.getValue(Action.ACTION_COMMAND_KEY));

                Dimension dim = menuItem.getPreferredSize();
                dim.height = 23;
                menuItem.setPreferredSize(dim);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Inner classes
    //

    protected class MPanel extends JPanel
    {
        protected boolean useGrid = false;
        public MPanel(boolean useGrid)
        {
            this.useGrid = useGrid;
        }

        @Override
        public void paint(Graphics g)
        {
            super.paint(g);

            if( cView == null )
                return;

            Graphics2D g2 = (Graphics2D)g;
            g2.transform(at);
            g2.translate(-offset.x, -offset.y);

            if( useGrid && gridOptions != null )
            {
                if( gridOptions.getGridStyle() == GridStyle.BACKGROUND_GRID )
                {
                    paintGrid(g2);
                    cView.paint((Graphics2D)g);
                }
                else
                {
                    cView.paint((Graphics2D)g);
                    paintGrid(g2);
                }
            }
            else
            {
                cView.paint((Graphics2D)g);
            }

            if( selectionManager != null )
                selectionManager.paintLayer(g2);
            
            for(ViewPaneLayer layer: layers)
                layer.paintLayer(g2);
            g2.translate(offset.x, offset.y);
        }

        protected void paintGrid(Graphics2D g2)
        {
            // define whether grid should be shown
            if( !gridOptions.isShowGrid() )
                return;

            int cellSize = adoptStep(gridOptions.getCellSize(), 5);

            if( cellSize < 0 )
                return;

            g2.setStroke(new BasicStroke(1.0f));

            if( gridOptions.getGridStyle() == GridStyle.POINTS )
                g2.setColor(Color.gray);
            else
                g2.setColor(Color.lightGray);

            Rectangle bounds = g2.getClipBounds();

            int startX = (int) ( Math.floor( ((double)bounds.x) / cellSize ) * cellSize );
            int startY = (int) ( Math.floor( ((double)bounds.y) / cellSize ) * cellSize );

            for( int i = 0; i < bounds.width / cellSize + 2; i++ )
            {
                int x = startX + i * cellSize;
                paintGridLine(g2, x, startY, x, bounds.y + bounds.height);
            }

            for( int i = 0; i < bounds.height / cellSize + 2; i++ )
            {
                int y = startY + i * cellSize;
                paintGridLine(g2, startX, y, bounds.x + bounds.width, y);
            }
        }

        protected int adoptStep(int step, int minStep)
        {
            if( getScaleX() > 1 )
                return step;

            if( (int) ( getScaleX() * step ) >= minStep )
                return step;

            return -1;
        }

        protected void paintGridLine(Graphics2D g2, int x1, int y1, int x2, int y2)
        {
            if( gridOptions.getGridStyle() != GridStyle.POINTS )
            {
                g2.drawLine(x1, y1, x2, y2);
            }
            else
            {
                if( gridOptions.getStepSize() <= 0 )
                    return;

                int stepSize = adoptStep(gridOptions.getStepSize(), 5);
                if( stepSize < 1 )
                {
                    stepSize = adoptStep(gridOptions.getCellSize(), 5);
                    if( stepSize < 1 )
                        return;
                }

                if( x1 == x2 )
                {
                    int point = y1;
                    while( point <= y2 )
                    {
                        g2.drawLine(x1, point, x1, point);
                        point += stepSize;
                    }
                }
                else if( y1 == y2 )
                {
                    int point = x1;
                    while( point <= x2 )
                    {
                        g2.drawLine(point, y1, point, y1);
                        point += stepSize;
                    }
                }
            }
        }

        @Override
        public String getToolTipText(MouseEvent event)
        {
            return ViewPane.this.getToolTipText(event);
        }

        @Override
        public Point getToolTipLocation(MouseEvent event)
        {
            return ViewPane.this.getToolTipLocation(event);
        }
    }
}
