package ru.biosoft.graphics.editor;


/**
 * The listener interface for receiving "interesting" mouse events (press, release, click,
 * enter, and exit) on a view pane.
 *
 * @see <{MouseListener}>
 * @see <{ru.biosoft.graphics.editor.ViewPaneEvent}>
 */
public interface ViewPaneListener
{
    /**
     * Invoked when the mouse has been clicked on a view pane.
     */
    public void mouseClicked(ViewPaneEvent e);

    /**
     * Invoked when a mouse button has been pressed on a view pane.
     */
    public void mousePressed(ViewPaneEvent e);

    /**
     * Invoked when a mouse button has been released on a view pane.
     */
    public void mouseReleased(ViewPaneEvent e);

    /**
     * Invoked when the mouse enters a view pane.
     */
    public void mouseEntered(ViewPaneEvent e);

    /**
     * Invoked when the mouse exits a view pane.
     */
    public void mouseExited(ViewPaneEvent e);

    public void mouseDragged(ViewPaneEvent e);

    public void mouseMoved(ViewPaneEvent e);
}

