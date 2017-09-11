package ru.biosoft.graphics.editor;

import java.awt.Graphics2D;

/**
 * Additional drawing layer for ViewPane (e.g. selection)
 * @author lan
 */
public interface ViewPaneLayer
{
    public void paintLayer(Graphics2D g2);
}
