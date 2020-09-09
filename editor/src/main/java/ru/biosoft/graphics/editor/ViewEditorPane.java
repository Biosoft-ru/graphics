package ru.biosoft.graphics.editor;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import ru.biosoft.graphics.ArrowView;
import ru.biosoft.graphics.BoxView;
import ru.biosoft.graphics.CompositeView;
import ru.biosoft.graphics.PathUtils;
import ru.biosoft.graphics.Pen;
import ru.biosoft.graphics.SimplePath;
import ru.biosoft.graphics.View;

import com.developmentontheedge.beans.undo.Transactable;
import com.developmentontheedge.beans.undo.TransactionEvent;
import com.developmentontheedge.beans.undo.TransactionListener;


/**
 * These a general class to edit any CompositeView
 *
 * To provide specific changes in data model it uses some instance of ViewEditorHelper.
 */
@SuppressWarnings ( "serial" )
public class ViewEditorPane extends ViewPane implements Transactable, TransactionListener, KeyListener
{
    public static final int TOOLBAR_BUTTON_SIZE = 25;
    public static final int DEFAULT_MOVE_STEP = 5;
    ViewEditorHelper helper;
    ActionListener insertActionListener;
    Object insertMode;
    ButtonGroup bg = new ButtonGroup();
    JToggleButton selectbutton;
    JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);

    ////////////////////////////////////////////////////////////////////////////
    // Constructor
    //

    public ViewEditorPane(ViewEditorHelper helper)
    {
        super();

        this.helper = helper;
        helper.register(this);
        fillToolbar(helper);
        toolbar.setRollover( true );
        add(toolbar, BorderLayout.NORTH);
        addKeyListener(this);
        selectionEnabled = true;

        setSelectionManager(new RectangleSelectionManager(this, helper));
    }
    
    public ViewEditorHelper getHelper()
    {
        return helper;
    }

    public JPanel getPanel()
    {
        return mPanel;
    }

    private ActionListener getInsertActionListener()
    {
        if( insertActionListener == null )
        {
            insertActionListener = e -> {
                Action insterAction = (Action)e.getSource();
                Object classToInsert = insterAction.getValue(InsertAction.TYPE);
                setInsertMode(classToInsert);
            };
        }
        return insertActionListener;
    }

    public void fillToolbar(ViewEditorHelper helper)
    {
        toolbar.removeAll();
        Action insterActions[] = helper.getActions();
        for( Action action : insterActions )
        {
            if( action instanceof InsertAction )
            {
                ( (InsertAction)action ).addActionListener(getInsertActionListener());
                JToggleButton tb = new JToggleButton(action);
                tb.setAlignmentY(0.5f);
                Dimension btnSize = new Dimension(TOOLBAR_BUTTON_SIZE, TOOLBAR_BUTTON_SIZE);
                tb.setSize(btnSize);
                tb.setPreferredSize(btnSize);
                tb.setMinimumSize(btnSize);
                tb.setMaximumSize(btnSize);

                if( tb.getIcon() != null )
                {
                    tb.setText(null);
                }
                String name = (String)action.getValue(Action.NAME);
                if( "Select".equals(name) )
                {
                    selectbutton = tb;
                }
                bg.add(tb);
                toolbar.add(tb);
            }
        }
        resetInsertMode();
    }

    protected void setInsertMode(Object classToInsert)
    {
        insertMode = classToInsert;
    }

    public void resetInsertMode()
    {
        insertMode = null;
        if( selectbutton != null )
        {
            bg.setSelected(selectbutton.getModel(), true);
            toolbar.repaint();
        }
    }

    public boolean isInsertMode()
    {
        return insertMode != null;
    }

    private boolean selectionEnabled;
    public boolean getSelectionEnabled()
    {
        return selectionEnabled;
    }
    public void setSelectionEnabled(boolean selectionEnabled)
    {
        this.selectionEnabled = selectionEnabled;
    }

    ////////////////////////////////////////////////////////////////////////////
    // View editing issues
    //
    synchronized public void move(Dimension offset)
    {
        move(offset, false);
    }
    
    /** Move all selected entities to the specified offset*/
    synchronized public void move(Dimension offset, boolean moveOnlyEdges)
    {
        startTransaction("Move "+getSelectionName());

        Set<Object> processedObjects = new HashSet<>();
        for( int i = 0; i < selectionManager.getSelectedViewCount(); i++ )
        {
            View view = selectionManager.getSelectedView(i);
            if( !processedObjects.contains(view.getModel()) )
            {
                processedObjects.add(view.getModel());
                if( controlPoint >= -1 && initialRect != null )
                {
                    //move control point for path
                    Point pathOffset = ( (ArrowView)view ).getPathOffset();
                    Rectangle boxRectangle = new Rectangle(initialRect.x - pathOffset.x, initialRect.y - pathOffset.y, initialRect.width,
                            initialRect.height);
                    View cpView = new BoxView(null, null, boxRectangle);
                    cpView.setModel(view.getModel());
                    helper.moveView(cpView, offset);

                    if( initialRect2 != null )
                    {
                        boxRectangle = new Rectangle(initialRect2.x - pathOffset.x, initialRect2.y - pathOffset.y, initialRect2.width,
                                initialRect2.height);
                        cpView = new BoxView(null, null, boxRectangle);
                        cpView.setModel(view.getModel());
                        helper.moveView(cpView, offset);
                    }
                }
                else if (!moveOnlyEdges)
                {
                    helper.moveView(view, offset);
                }
            }
        }

        completeTransaction();
    }

    /**
     * @return
     */
    protected String getSelectionName()
    {
    	List<String> names = new ArrayList<>();
    	for( int i = 0; i < selectionManager.getSelectedViewCount(); i++ )
    	{
    		View view = selectionManager.getSelectedView(i);
    		if( view.getModel() instanceof Double )
    		{
    			names.add(getName(view.getModel()));
    			if(names.size() >= 3)
    			{
    				break;
    			}
    		}
    	}
    	String selectionName = String.join(", ", names);
    	return selectionName;
    }
    
    protected static String getName(Object obj)
    {
    	String name = null;
    	try
        {
    		name = (String) obj.getClass().getMethod( "getName" ).invoke(obj);
        }
    	catch( Exception t )
    	{
        }
    	return name == null ? obj.toString() : name; 
    }

    /** Change size for the selected (one) entity. */
    synchronized public void changeSize(Dimension offset, Dimension size)
    {
        startTransaction("Change size");

        View view = selectionManager.getSelectedView(0);

        // some resizing require moving
        if( offset != null )
        {
            helper.resizeView(view, size, offset);
        }
        else
            helper.resizeView(view, size);

        completeTransaction();
    }

    /**
     * Adds new diagram element to the specified location (left, top) to the diagram.
     */
    synchronized public void add(Object obj, Point point)
    {
    	startTransaction("Add "+ getName(obj));
        helper.add(obj, point);
        completeTransaction();
    }

    synchronized public void add(List<?> objects, Point point)
    {
        startTransaction("Add elements");
        for( Object obj : objects )
            helper.add(obj, point);
        completeTransaction();
    }

    /** Remove all selected entities. */
    synchronized public void remove()
    {
        startTransaction("Remove "+getSelectionName());

        for( int i = 0; i < selectionManager.getSelectedViewCount(); i++ )
        {
            View view = selectionManager.getSelectedView(i);
            helper.removeView(view);
        }

        completeTransaction();
    }

    @Override
    public boolean isFocusTraversable()
    {
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // KeyListener interface
    //

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if( selectionEnabled )
        {
            int keyCode = e.getKeyCode();
            if( keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN )
            {
                if( selectionManager.getSelectedViewCount() > 0 )
                {
                    int moveStep = DEFAULT_MOVE_STEP;
                    if( gridOptions != null && gridOptions.isShowGrid() )
                    {
                        moveStep = gridOptions.getStepSize();
                    }
                    if( e.isAltDown() )
                    {
                        moveStep = 1;
                    }
                    Point prevPoint = new Point(0, 0);
                    Point newPoint = new Point();
                    if( keyCode == KeyEvent.VK_RIGHT )
                    {
                        newPoint.x = moveStep;
                    }
                    else if( keyCode == KeyEvent.VK_LEFT )
                    {
                        newPoint.x = -moveStep;
                    }
                    else if( keyCode == KeyEvent.VK_UP )
                    {
                        newPoint.y = -moveStep;
                    }
                    else if( keyCode == KeyEvent.VK_DOWN )
                    {
                        newPoint.y = moveStep;
                    }
                    if( !e.isAltDown() )
                    {
                        Point delta = snapToGrid(prevPoint, prevPoint, newPoint, selectionManager.getBounds());
                        newPoint.x += delta.x;
                        newPoint.y += delta.y;
                    }
                    Dimension offset = new Dimension(newPoint.x, newPoint.y);

                    move(offset);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if( selectionEnabled )
        {
            int keyKode = e.getKeyCode();
            if( keyKode == KeyEvent.VK_DELETE )
            {
                remove();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Overriding MouseListener, MouseMotionListener
    //

    protected boolean initiated = false;

    // resizing directions
    protected static final int TL = 1;
    protected static final int TC = 2; //   TL    TC    TR
    protected static final int TR = 3; //
    protected static final int ML = 4; //   ML          MR
    protected static final int MR = 5; //
    protected static final int BL = 6; //   BL    BC    BR
    protected static final int BC = 7;
    protected static final int BR = 8;

    // helper variables for view resizing dispatching
    protected boolean resizing = false;
    protected Rectangle initialRect = null;
    protected Rectangle initialRect2 = null;
    protected int resizingDirection = 0;

    // helper variables for view moving dispatching
    protected boolean moving = false;
    protected int controlPoint = -1;
    protected Point startPoint = null;
    protected Point prevPoint = null;
    protected Point prevCorrectedPoint = null;
    protected CompositeView selection = null;

    @Override
    public void mousePressed(MouseEvent e)
    {
        requestFocus();
        super.mousePressed(e);

        if( selectionEnabled )
        {
            Point pt = clientToView(e.getPoint());

            int num = selectionManager.getSelectedViewCount();

            if( isInsertMode() ) // inserting
            {

            }
            else
            // resizing and moving
            {
                if( num == 1 && getResizingDirection(selectionManager.getSelectedView(0), pt) > 0 )
                {
                    // start resizing here
                    selection = new CompositeView();

                    View view = selectionManager.getSelectedView(0);
                    initialRect = (Rectangle)view.getBounds().clone();
                    View selectionBox = createSelectionBox(initialRect);
                    selection.add(selectionBox);
                    cView.insert(selection, cView.size());

                    resizingDirection = getResizingDirection(view, pt);
                    startPoint = pt;
                    resizing = true;
                }
                else if( num == 1 && getControlPoint(selectionManager.getSelectedView(0), pt) > -2 )
                {
                    controlPoint = getControlPoint(selectionManager.getSelectedView(0), pt);
                    if( controlPoint >= 0 )
                    {
                        selection = new CompositeView();

                        View view = selectionManager.getSelectedView(0);
                        initialRect = getControlPointBounds(view, controlPoint);
                        View selectionBox = createSelectionBox(initialRect);
                        selection.add(selectionBox);
                        cView.insert(selection, cView.size());

                        startPoint = pt;
                        prevPoint = pt;
                        prevCorrectedPoint = pt;
                        moving = true;
                    }
                    else
                    {
                        int segment = getSelectedSegment(selectionManager.getSelectedView(0), pt);
                        if( segment >= 0 )
                        {
                            selection = new CompositeView();

                            View view = selectionManager.getSelectedView(0);
                            initialRect = getControlPointBounds(view, segment);
                            View selectionBox = createSelectionBox(initialRect);
                            selection.add(selectionBox);
                            initialRect2 = getControlPointBounds(view, segment + 1);
                            selectionBox = createSelectionBox(initialRect2);
                            selection.add(selectionBox);
                            cView.insert(selection, cView.size());

                            startPoint = pt;
                            prevPoint = pt;
                            prevCorrectedPoint = pt;
                            moving = true;
                        }
                    }
                }
                else if( num > 0 )
                {
                    for( int i = 0; i < num; i++ )
                    {
                        View view = selectionManager.getSelectedView(i);
                        Rectangle rect = view.getBounds();
                        if( rect.contains(pt) )
                        {
                            startPoint = pt;
                            prevPoint = pt;
                            prevCorrectedPoint = pt;
                            moving = true;
                            break;
                        }
                    }
                }
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e)
    {
        super.mouseReleased(e);

        if( selectionEnabled )
        {
            Point pt = clientToView(e.getPoint());

            if( isInsertMode() )
            {
                try
                {
                    Object obj = helper.createObject(insertMode, pt);
                    if( obj != null )
                    {
                        add(obj, pt);
                    }
                }
                finally
                {
                    resetInsertMode();
                }
            }
            else if( initiated )
            {
                if( resizing )
                {
                    // finish resizing here
                    Point endPoint = pt;
                    Dimension offset = null;
                    switch( resizingDirection )
                    {
                        case TL:
                            offset = new Dimension( endPoint.x - startPoint.x, endPoint.y - startPoint.y );
                            break;

                        case TC:
                        case TR:
                            offset = new Dimension( 0, endPoint.y - startPoint.y );
                            break;

                        case ML:
                        case BL:
                            offset = new Dimension( endPoint.x - startPoint.x, 0 );
                            break;

                        default:
                            break;
                    }

                    if( offset != null )
                    {
                        if( offset.width >= initialRect.width )
                        {
                            offset.width = initialRect.width - 1;
                        }
                        if( offset.height >= initialRect.height )
                        {
                            offset.height = initialRect.height - 1;
                        }

                        //move(offset);
                    }

                    // do resizing
                    int dx = startPoint.x - pt.x;
                    int dy = startPoint.y - pt.y;
                    if( !e.isAltDown() )
                    {
                        Point delta = snapToGrid(startPoint, pt, resizingDirection, initialRect.getBounds());
                        dx -= delta.x;
                        dy -= delta.y;
                    }
                    Rectangle rect = getResizingSelectionRect(resizingDirection, initialRect, dx, dy);

                    changeSize(offset, new Dimension(rect.width - initialRect.width, rect.height - initialRect.height));

                    // cleanup
                    cView.remove(selection);
                    repaint();

                    initialRect = null;
                    initialRect2 = null;
                    resizingDirection = 0;
                    startPoint = null;
                    selection = null;
                    resizing = false;

                    mPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                else if( moving )
                {
                    Dimension offset = new Dimension(prevCorrectedPoint.x - startPoint.x, prevCorrectedPoint.y - startPoint.y);

                    move(offset, helper.drawOnFly());

                    cView.remove(selection);
                    repaint();

                    startPoint = null;
                    prevPoint = null;
                    prevCorrectedPoint = null;
                    selection = null;
                    moving = false;
                    initialRect = null;
                    initialRect2 = null;
                    controlPoint = -1;
                }
                initiated = false;
            }
            else
            {
                cView.remove(selection);
                repaint();

                initialRect = null;
                initialRect2 = null;
                resizingDirection = 0;
                startPoint = null;
                prevPoint = null;
                prevCorrectedPoint = null;
                selection = null;
                resizing = false;
                moving = false;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        super.mouseDragged(e);

        if( selectionEnabled && !e.isControlDown() )
        {
            Point pt = clientToView(e.getPoint());

            if( resizing )
            {
                // do resizing here
                int dx = startPoint.x - pt.x;
                int dy = startPoint.y - pt.y;
                if( !e.isAltDown() )
                {
                    Point delta = snapToGrid(startPoint, pt, resizingDirection, initialRect.getBounds());
                    dx -= delta.x;
                    dy -= delta.y;
                }
                Rectangle rect = getResizingSelectionRect(resizingDirection, initialRect, dx, dy);

                View selectionBox = createSelectionBox(rect);
                cView.remove(selection);
                selection = new CompositeView();
                selection.add(selectionBox);
                cView.insert(selection, cView.size());

                repaint();
                initiated = true;
            }
            else if( moving )
            {
                if( ( Math.abs(pt.x - startPoint.x) > 5 || Math.abs(pt.y - startPoint.y) > 5 ) && selection == null )
                {
                    selection = new CompositeView();

                    for( int i = 0; i < selectionManager.getSelectedViewCount(); i++ )
                    {
                        View view = selectionManager.getSelectedView(i);
                        if( view.getModel() != null && helper.drawOnFly() && !view.equals(this.cView))
                            selection.add(view);
                        else
                        {
                            Rectangle rect = view.getBounds();
                            View selectionBox = createSelectionBox(rect);
                            selection.add(selectionBox);
                        }
                    }

                    cView.insert(selection, cView.size());
                }

                // Bugfix: if click in other panes then location may be null
                if( selection == null )
                    return;

                if( !e.isAltDown() )
                {
                    // Handle specially edge and vertex selections
                    // (quick and dirty)
                    Rectangle bounds = selection.getBounds();
                    if(bounds.width == 6 || bounds.height == 6)
                        bounds.grow( -3, -3 );
                    Point delta = snapToGrid(prevPoint, prevCorrectedPoint, pt, bounds);
                    prevPoint = new Point(pt.x, pt.y);
                    pt.x += delta.x;
                    pt.y += delta.y;
                }
                else
                {
                    prevPoint = pt;
                }
                Point point = new Point(pt.x - prevCorrectedPoint.x, pt.y - prevCorrectedPoint.y);

                if( selection.getBounds().x + point.x < offset.x )
                {
                    point.x = offset.x-selection.getBounds().x;
                }
                if( selection.getBounds().y + point.y < offset.y )
                {
                    point.y = offset.y-selection.getBounds().y;
                }

                View viewToAccept = cView.getDeepestActive(pt);

                if( viewToAccept instanceof CompositeView )
                {
                    for( int i = 0; i < selectionManager.getSelectedViewCount(); i++ )
                    {
                        View viewToDrop = selectionManager.getSelectedView(i);
                        View accept = cView.getDeepestActive(pt, new Object[] {viewToDrop.getModel()}, null);
                        if( accept instanceof CompositeView && !helper.canAccept((CompositeView)accept, viewToDrop) )
                        {
                            return;
                        }
                    }
                }
                
                selection.move(point);

                if( helper.drawOnFly() )
                {
                    for( View v : selection )
                        if (v.getModel() != null)
                        helper.moveView(v, new Dimension(point.x, point.y));
                }
                
                prevCorrectedPoint = pt;
                repaint();
                initiated = true;
            }
        }
    }
    @Override
    public void mouseMoved(MouseEvent e)
    {
        super.mouseMoved(e);

        if( selectionEnabled )
        {
            if( selectionManager.getSelectedViewCount() == 1 )
            {
                Point pt = clientToView(e.getPoint());

                View view = selectionManager.getSelectedView(0);
                //if(helper.isResizable(view))
                {
                    switch( getResizingDirection(view, pt) )
                    {
                        case TL:
                            mPanel.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
                            break;

                        case TC:
                            mPanel.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
                            break;

                        case TR:
                            mPanel.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
                            break;

                        case ML:
                            mPanel.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
                            break;

                        case MR:
                            mPanel.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
                            break;

                        case BL:
                            mPanel.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
                            break;

                        case BC:
                            mPanel.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
                            break;

                        case BR:
                            mPanel.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
                            break;

                        default:
                            mPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            break;
                    } // switch(getResizingDirection(view, pt))
                } // if(helper.isResizable(view))
            } // if(selectionManager.getSelectedViewCount() == 1)
        } // if(selectionEnabled)
    }

    //////////////////////////////////////////////
    // Helper functions
    //

    protected Rectangle getResizingSelectionRect(int resizingDirection, Rectangle initialRect, int dx, int dy)
    {
        Rectangle rect = null;
        switch( resizingDirection )
        {
            case TL:
                rect = new Rectangle(initialRect.x - dx, initialRect.y - dy, initialRect.width + dx, initialRect.height + dy);
                break;

            case TC:
                rect = new Rectangle(initialRect.x, initialRect.y - dy, initialRect.width, initialRect.height + dy);
                break;

            case TR:
                rect = new Rectangle(initialRect.x, initialRect.y - dy, initialRect.width - dx, initialRect.height + dy);
                break;

            case ML:
                rect = new Rectangle(initialRect.x - dx, initialRect.y, initialRect.width + dx, initialRect.height);
                break;

            case MR:
                rect = new Rectangle(initialRect.x, initialRect.y, initialRect.width - dx, initialRect.height);
                break;

            case BL:
                rect = new Rectangle(initialRect.x - dx, initialRect.y, initialRect.width + dx, initialRect.height - dy);
                break;

            case BC:
                rect = new Rectangle(initialRect.x, initialRect.y, initialRect.width, initialRect.height - dy);
                break;

            case BR:
                rect = new Rectangle(initialRect.x, initialRect.y, initialRect.width - dx, initialRect.height - dy);
                break;
            default:
                throw new RuntimeException( "getResizingSelectionRect called with invalid resizingDirection=" + resizingDirection );
        }
        if( rect.width <= 0 )
        {
            rect.width = 1;
        }
        if( rect.height <= 0 )
        {
            rect.height = 1;
        }
        if( rect.x >= initialRect.x + initialRect.width )
        {
            rect.x = initialRect.x + initialRect.width - 1;
        }
        if( rect.y >= initialRect.y + initialRect.height )
        {
            rect.y = initialRect.y + initialRect.height - 1;
        }

        return rect;
    }

    protected boolean check(Point corner, Point ptToCheck)
    {
        Rectangle rect = new Rectangle(corner.x - ResizingBoxSelector.HALF_DEFAULT_BOX_SIZE, corner.y
                - ResizingBoxSelector.HALF_DEFAULT_BOX_SIZE, ResizingBoxSelector.DEFAULT_BOX_SIZE, ResizingBoxSelector.DEFAULT_BOX_SIZE);
        //return (rect.contains(ptToCheck) ? 1 : 0);
        return rect.contains(ptToCheck);
    }

    protected int getResizingDirection(View view, Point point)
    {
        if( !helper.isResizable(view) )
        {
            return 0;
        }
        Rectangle r = view.getBounds();

        if( check(new Point(r.x, r.y), point) )
        {
            return TL;
        }
        if( check(new Point(r.x + r.width / 2, r.y), point) )
        {
            return TC;
        }
        if( check(new Point(r.x + r.width, r.y), point) )
        {
            return TR;
        }
        if( check(new Point(r.x, r.y + r.height / 2), point) )
        {
            return ML;
        }
        if( check(new Point(r.x + r.width, r.y + r.height / 2), point) )
        {
            return MR;
        }
        if( check(new Point(r.x, r.y + r.height), point) )
        {
            return BL;
        }
        if( check(new Point(r.x + r.width / 2, r.y + r.height), point) )
        {
            return BC;
        }
        if( check(new Point(r.x + r.width, r.y + r.height), point) )
        {
            return BR;
        }
        return 0;
    }

    /**
     * Get selected control point
     * @return
     *     -2 if view in not ArrowView
     *     -1 if point is not equals control point
     *     >=0 the number of selected control point
     */
    protected int getControlPoint(View view, Point point)
    {
        if( ! ( view instanceof ArrowView ) )
            return -2;

        Point pathOffset = ( (ArrowView)view ).getPathOffset();
        Rectangle curRect = new Rectangle(point.x - ArrowView.DELTA - pathOffset.x, point.y - ArrowView.DELTA - pathOffset.y,
                2 * ArrowView.DELTA, 2 * ArrowView.DELTA);
        SimplePath path = ( (ArrowView)view ).getPath();
        for( int i = 0; i < path.npoints; i++ )
        {
            if( curRect.contains(path.xpoints[i], path.ypoints[i]) )
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get selected segment of the path
     * @return
     *     -2 if view in not ArrowView
     *     -1 if no intersections with segments
     *     >=0 the number of the first control point in segment
     */
    protected int getSelectedSegment(View view, Point point)
    {
        if( ! ( view instanceof ArrowView ) )
            return -2;

        Point pathOffset = ( (ArrowView)view ).getPathOffset();
        Point curPoint = new Point(point.x - pathOffset.x, point.y - pathOffset.y);
        SimplePath path = ( (ArrowView)view ).getPath();

        return PathUtils.getNearestSegment(path, curPoint);
    }

    protected Rectangle getControlPointBounds(View view, int point)
    {
        if( ! ( view instanceof ArrowView ) )
            return null;

        Point pathOffset = ( (ArrowView)view ).getPathOffset();
        SimplePath path = ( (ArrowView)view ).getPath();
        return new Rectangle(path.xpoints[point] + pathOffset.x - 3, path.ypoints[point] + pathOffset.y - 3, 6, 6);
    }

    protected View createSelectionBox(Rectangle rect)
    {
        float[] dash = {3f, 3f};
        BasicStroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f);

        Pen pen = new Pen(dashed, Color.black);
        return new BoxView(pen, null, rect);
    }

    protected View createAcceptingSelectionBox(Rectangle rect)
    {
        float[] dash = {1f, 1f};
        BasicStroke dashed = new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f);

        Pen pen = new Pen(dashed, Color.blue);
        return new BoxView(pen, null, rect);
    }

    protected Point snapToGrid(Point oldPoint, Point oldCorrectedPoint, Point newPoint, Rectangle oldBounds)
    {
        Point result = new Point();
        if( gridOptions != null && gridOptions.isShowGrid() && gridOptions.getStepSize() > 0 )
        {
            Point targetLocation = new Point(newPoint.x - oldCorrectedPoint.x, newPoint.y - oldCorrectedPoint.y);
            boolean snapToX = true;
            boolean snapToY = true;
            if( newPoint.x < oldPoint.x )
            {
                //look at left border
                targetLocation.x += oldBounds.x;
            }
            else if( newPoint.x > oldPoint.x )
            {
                //look at right border
                targetLocation.x += oldBounds.x + oldBounds.width;
            }
            else
            {
                snapToX = false;
            }

            if( newPoint.y < oldPoint.y )
            {
                //look at top border
                targetLocation.y += oldBounds.y;
            }
            else if( newPoint.y > oldPoint.y )
            {
                //look at bottom border
                targetLocation.y += oldBounds.y + oldBounds.height;
            }
            else
            {
                snapToY = false;
            }

            int stepSize = gridOptions.getStepSize();
            if( snapToX )
            {
                result.x = ( targetLocation.x / stepSize ) * stepSize - targetLocation.x;
                if( result.x < -stepSize / 2 )
                {
                    result.x = stepSize + result.x;
                }
            }
            else
            {
                result.x = oldCorrectedPoint.x - oldPoint.x;
            }
            if( snapToY )
            {
                result.y = ( targetLocation.y / stepSize ) * stepSize - targetLocation.y;
                if( result.y < -stepSize / 2 )
                {
                    result.y = stepSize + result.y;
                }
            }
            else
            {
                result.y = oldCorrectedPoint.y - oldPoint.y;
            }
        }
        return result;
    }

    protected Point snapToGrid(Point oldPoint, Point newPoint, int direction, Rectangle viewBounds)
    {
        Point result = new Point();
        if( gridOptions != null && gridOptions.isShowGrid() && gridOptions.getStepSize() > 0 )
        {
            int stepSize = gridOptions.getStepSize();
            boolean snapX = false;
            boolean snapY = false;
            Point targetPoint = new Point(viewBounds.x + newPoint.x - oldPoint.x, viewBounds.y + newPoint.y - oldPoint.y);

            if( direction == ML || direction == TL || direction == BL )
            {
                //left border
                snapX = true;
            }
            else if( direction == MR || direction == TR || direction == BR )
            {
                //right border
                targetPoint.x += viewBounds.width;
                snapX = true;
            }

            if( direction == TL || direction == TC || direction == TR )
            {
                //top border
                snapY = true;
            }
            else if( direction == BL || direction == BC || direction == BR )
            {
                //bottom border
                targetPoint.y += viewBounds.height;
                snapY = true;
            }

            if( snapX )
            {
                result.x = ( targetPoint.x / stepSize ) * stepSize - targetPoint.x;
                if( result.x < -stepSize / 2 )
                {
                    result.x = stepSize + result.x;
                }
            }
            if( snapY )
            {
                result.y = ( targetPoint.y / stepSize ) * stepSize - targetPoint.y;
                if( result.y < -stepSize / 2 )
                {
                    result.y = stepSize + result.y;
                }
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Transactable interface implementation
    //

    @Override
    public void addTransactionListener(TransactionListener listener)
    {
        listenerList.add(TransactionListener.class, listener);
    }

    @Override
    public void removeTransactionListener(TransactionListener listener)
    {
        listenerList.remove(TransactionListener.class, listener);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Transaction issues
    //

    protected SelectionUndo selectionUndo = null;
    public void startTransaction(String name)
    {
        if( cView != null && cView.getModel() != null )
        {
            fireStartTransaction(new TransactionEvent(cView.getModel(), name));
        }

        selectionUndo = new SelectionUndo(getSelectionManager().getSelectedModels());
        fireAddEdit(selectionUndo);
    }

    @Override
    public void startTransaction(TransactionEvent te)
    {
        fireStartTransaction(te);
        selectionUndo = new SelectionUndo(getSelectionManager().getSelectedModels());
        fireAddEdit(selectionUndo);
    }

    @Override
    public boolean addEdit(UndoableEdit ue)
    {
        fireAddEdit(ue);
        return true;
    }

    @Override
    public void completeTransaction()
    {
        SelectionUndo s = selectionUndo;
        if(s != null)
        {
            s.setNewSelection(getSelectionManager().getSelectedModels());
        }
        selectionUndo = null;

        fireCompleteTransaction();
    }

    protected void fireStartTransaction(TransactionEvent evt)
    {
        Object[] listeners = listenerList.getListenerList();
        for( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if( listeners[i] == TransactionListener.class )
                ( (TransactionListener)listeners[i + 1] ).startTransaction(evt);
        }
    }

    protected void fireAddEdit(UndoableEdit ue)
    {
        Object[] listeners = listenerList.getListenerList();
        for( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if( listeners[i] == TransactionListener.class )
                ( (TransactionListener)listeners[i + 1] ).addEdit(ue);
        }
    }

    protected void fireCompleteTransaction()
    {
        Object[] listeners = listenerList.getListenerList();
        for( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if( listeners[i] == TransactionListener.class )
                ( (TransactionListener)listeners[i + 1] ).completeTransaction();
        }
    }

    class SelectionUndo extends AbstractUndoableEdit
    {
        Object[] oldSelection = null;
        Object[] newSelection = null;
        SelectionUndo(Object[] oldSelection)
        {
            this.oldSelection = oldSelection;
        }

        public void setNewSelection(Object[] newSelection)
        {
            this.newSelection = newSelection;
        }

        @Override
        public void undo() throws CannotUndoException
        {
            if( oldSelection.length > 0 )
            {
                getSelectionManager().selectModels(oldSelection, true);
            }
        }

        @Override
        public void redo() throws CannotRedoException
        {
            if( newSelection.length > 0 )
            {
                getSelectionManager().selectModels(newSelection, true);
            }
        }
    }
}
