package ru.biosoft.graphics.editor;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.Action;

import ru.biosoft.graphics.CompositeView;
import ru.biosoft.graphics.View;

/**
 * This class is used to provide all neccessary changes and validations in model.
 *
 * Generally two strategies for ViewEditorHelper
 * 1) really update the view and model
 * 2) update the model and recreate the view according to model
 */
public interface ViewEditorHelper
{
    /** register this ViewEditorHelper for the specified ViewEditorPane. */
    public void register(ViewEditorPane viewEditor);


    /**
     * Should provides necessary changes in view and model if the view can be moved on the specified offset.
     *
     * @returns shift on which the view was moved.
     */
    public Dimension moveView(View view, Dimension offset);

    /**
     * Should provides necessary changes in view and model if the view can be resized.
     *
     * @returns new view size.
     */
    public Dimension resizeView(View view, Dimension size);

    /**
     * Should provides necessary changes in view and model if the view can be resized and moved by specified offset.
     *
     * @returns new view size.
     */
    public Dimension resizeView(View view, Dimension size, Dimension offset);

    /**
     * Should provides necessary changes in view and model if the view can be resized.
     *
     * @returns view for the specified object.
     */
    public View add(Object obj, Point location);

    /**
     * Should provides necessary changes in view and model if the view can removed.
     *
     * @returns whether the view was removed.
     */
    public boolean removeView(View view);

    /**
     * @returns whether a specified view can be inserted to other view.
     */
    public boolean canAccept(CompositeView composite, View view);

    /**
     * @returns whether a specified view can be resized.
     */
    public boolean isResizable(View view);

    /**
     * @todo comment
     */
    public Object createObject(Object clazz, Point pt);

    ////////////////////////////////////////////////////////////////////////////
    // selection and cursor issues (unsolved yet)
    //

    /** we can use different selections for different viewes */
//    public ViewSelector provideSelectionView(View view);

//    public Cursor getCursor(View view, Point point);

    public Action[] getActions();
    
    /**
     * If true then views should be redraw while they are dragged by mouse
     */
    public boolean drawOnFly();
}
