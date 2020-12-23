package ru.biosoft.graphics.editor;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Action;

import ru.biosoft.graphics.CompositeView;
import ru.biosoft.graphics.View;

/**
 * Default implementation of ViewEditorHelper interface.
 * 
 * This class provides all necessary changes in view, but do not affect to the
 * model.
 */
public class DefaultViewEditorHelper implements ViewEditorHelper
{
    /** register this ViewEditorHelper for the specified ViewEditorPane. */
    @Override
    public void register ( ViewEditorPane viewEditorPane )
    {
    }

    /**
     * Should provides necessary changes in view and model if the view can be
     * moved on the specified offset.
     * 
     * @returns shift on which the view was moved.
     */
    @Override
    public Dimension moveView ( View view, Dimension offset ) throws Exception
    {
        view.move ( new Point ( offset.width, offset.height ) );
        // viewEditorPane.repaint();

        return offset;
    }

    /**
     * Should provides necessary changes in view and model if the view can be
     * resized.
     * 
     * @returns new view size.
     */
    @Override
    public Dimension resizeView ( View view, Dimension size ) throws Exception
    {
        Rectangle initialSize = view.getBounds ( );
        float scaleX = ( ( float ) initialSize.width + size.width )
                / ( initialSize.width );
        float scaleY = ( ( float ) initialSize.height + size.height )
                / ( initialSize.height );

        view.scale ( scaleX, scaleY );

        return size;
    }

    /**
     * Should provides necessary changes in view and model if the view can be
     * resized.
     * 
     * @returns view for the specified object.
     */
    @Override
    public View add ( Object obj, Point location )
    {
        return null;
    }

    /**
     * Should provides necessary changes in view and model if the view can be
     * resized.
     * 
     * @returns new view size.
     */
    @Override
    public boolean removeView ( View view )
    {
        return false;
    }

    /**
     * @returns whether a specified viewt can be inserted to other view.
     */
    @Override
    public boolean canAccept ( CompositeView composite, View view )
    {
        return false;
    }

    /**
     * @returns whether a specified diagram element can be resized.
     */
    @Override
    public boolean isResizable ( View view )
    {
        return false;
    }

    @Override
    public Object createObject ( Object clazz, Point pt )
    {
        return null;
    }

    @Override
    public Action[] getActions ( )
    {
        return new Action[0];
    }
    
    @Override
    public boolean drawOnFly()
    {
        return false;
    }

    @Override
    public Dimension resizeView(View view, Dimension size, Dimension offset) throws Exception
    {
        moveView( view, offset );
        return resizeView( view, size );
    }

}
