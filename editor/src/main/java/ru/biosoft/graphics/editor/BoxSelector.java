
package ru.biosoft.graphics.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import ru.biosoft.graphics.BoxView;
import ru.biosoft.graphics.Pen;
import ru.biosoft.graphics.View;

public class BoxSelector implements ViewSelector
{
  /**
   * paints selected view to the Graphics
   *
   * @param g      painted Graphics
   * @param view   painted view
   */
    @Override
    public void paint(Graphics2D g,View view)
    {
        Rectangle selection = (Rectangle)view.getBounds().clone();

        selection.grow(2, 2);

        float[] dash = { 3f, 3f };
        BasicStroke dashed = new BasicStroke(1f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 10f, dash, 0f);

        Pen pen = new Pen(dashed, Color.black);
        BoxView box = new BoxView(pen, null, selection);

        box.paint(g);
    }
}

