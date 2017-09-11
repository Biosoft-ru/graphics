package ru.biosoft.graphics.editor;

import java.awt.Graphics2D;

import ru.biosoft.graphics.View;

public interface ViewSelector
{
    /**
     * paints selected view to the Graphics
     *
     * @param g      painted Graphics2D
     * @param view   painted view
     */
    public void paint(Graphics2D g, View view);
}
