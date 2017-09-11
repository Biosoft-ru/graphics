package ru.biosoft.graphics.editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import ru.biosoft.graphics.BoxView;
import ru.biosoft.graphics.Brush;
import ru.biosoft.graphics.View;

public class ResizingBoxSelector extends BoxSelector
{
    public static final int DEFAULT_BOX_SIZE = 6;
    public static final int HALF_DEFAULT_BOX_SIZE = DEFAULT_BOX_SIZE/2;

    private BoxView box = null;

    public ResizingBoxSelector()
    {
        box = new BoxView(null, new Brush(Color.black),  0, 0, DEFAULT_BOX_SIZE, DEFAULT_BOX_SIZE);
    }

   /**
   * paints selected view to the Graphics
   *
   * @param g      painted Graphics
   * @param view   painted view
   */
    @Override
    public void paint(Graphics2D g,View view)
    {
        super.paint(g, view);

        Rectangle r = view.getBounds();

        box.setLocation(r.x-HALF_DEFAULT_BOX_SIZE, r.y-HALF_DEFAULT_BOX_SIZE);
        box.paint(g);

        box.setLocation(r.x+r.width/2-HALF_DEFAULT_BOX_SIZE, r.y-HALF_DEFAULT_BOX_SIZE);
        box.paint(g);

        box.setLocation(r.x+r.width-HALF_DEFAULT_BOX_SIZE, r.y-HALF_DEFAULT_BOX_SIZE);
        box.paint(g);

        box.setLocation(r.x-HALF_DEFAULT_BOX_SIZE, r.y+r.height/2-HALF_DEFAULT_BOX_SIZE);
        box.paint(g);

        box.setLocation(r.x+r.width-HALF_DEFAULT_BOX_SIZE, r.y+r.height/2-HALF_DEFAULT_BOX_SIZE);
        box.paint(g);

        box.setLocation(r.x-HALF_DEFAULT_BOX_SIZE, r.y+r.height-HALF_DEFAULT_BOX_SIZE);
        box.paint(g);

        box.setLocation(r.x+r.width/2-HALF_DEFAULT_BOX_SIZE, r.y+r.height-HALF_DEFAULT_BOX_SIZE);
        box.paint(g);

        box.setLocation(r.x+r.width-HALF_DEFAULT_BOX_SIZE, r.y+r.height-HALF_DEFAULT_BOX_SIZE);
        box.paint(g);
    }
}

