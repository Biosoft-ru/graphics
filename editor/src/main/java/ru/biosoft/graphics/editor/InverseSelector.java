package ru.biosoft.graphics.editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import ru.biosoft.graphics.View;

public class InverseSelector implements ViewSelector
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
    //g.setXORMode(Color.white);
    //view.paint((Graphics2D)g);

    Rectangle rect = (Rectangle)view.getBounds().clone();

    rect.width  += 10;
    rect.height += 10;

    BufferedImage bi1  = new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_ARGB);
    BufferedImage bi2  = new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_ARGB);

    Graphics2D g1 = bi1.createGraphics();
    Graphics2D g2 = bi2.createGraphics();

    int x = rect.x-5 ,  y = rect.y-5 ;

    view.move(-x, -y);
        view.paint(g1);
        g2.drawImage(bi1, 0,0, rect.width,rect.height,null);
        g2.setXORMode(Color.white);
        g2.drawImage(bi1, 0,0, rect.width,rect.height,null);
        g.setXORMode(Color.black);
        g.drawImage(bi2, x, y, rect.width,rect.height,null);
        //g.drawRect(0,0,rect.width,rect.height);
        //g.drawImage(bi2, 0, 0, rect.width,rect.height,null);
    view.move(x, y);
  }
}

