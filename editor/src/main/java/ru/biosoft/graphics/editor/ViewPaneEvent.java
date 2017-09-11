package ru.biosoft.graphics.editor;

import java.awt.Component;
import java.awt.event.MouseEvent;

import ru.biosoft.graphics.View;


/**
 * An event which indicates that a mouse action occurred in a View.
 */
public class ViewPaneEvent extends MouseEvent
{
    /**
     * The object on which the Event initially occurred.
     */
    protected View viewSource;

    protected ViewPane viewPane;

    /**
     * Constructs a Event object with the specified source View.
     *
     * @param source the View that originated the event
     */
    public ViewPaneEvent(ViewPane viewPane, View viewSource, MouseEvent e,
                         int x, int y)
    {
        super((Component)e.getSource(),
               e.getID(), e.getWhen(), e.getModifiers(),
               x, y, e.getClickCount(), e.isPopupTrigger());
        this.viewPane = viewPane;
        this.viewSource = viewSource;
    }

    /**
     * Returns the originator of the event.
     *
     * @return the View object that originated the event
     */
    public View getViewSource()
    {
        return viewSource;
    }

    public ViewPane getViewPane()
    {
        return viewPane;
    }
}
