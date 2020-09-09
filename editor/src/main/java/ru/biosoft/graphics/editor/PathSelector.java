package ru.biosoft.graphics.editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import ru.biosoft.graphics.ArrowView;
import ru.biosoft.graphics.BoxView;
import ru.biosoft.graphics.Brush;
import ru.biosoft.graphics.Pen;
import ru.biosoft.graphics.SimplePath;
import ru.biosoft.graphics.View;

public class PathSelector implements ViewSelector
{
    /**
     * paints selected view to the Graphics
     *
     * @param g      painted Graphics
     * @param view   painted view
     */
    @Override
    public void paint(Graphics2D g, View view)
    {
        if( ! ( view instanceof ArrowView ) )
            return;

        SimplePath path = ( (ArrowView)view ).getPath();
        Point pathOffset = ( (ArrowView)view ).getPathOffset();
        //createSelectedPoint(new Pen(1.0f, Color.black), new Brush(Color.black), path, 0, pathOffset).paint(g);
        for( int i = 0; i < path.npoints; i++ )
        {
            View controlPoint = createSelectedPoint(new Pen(1.0f, Color.red), new Brush(Color.red), path, i, pathOffset);
            controlPoint.setModel(view.getModel());
            controlPoint.setActive(true);
            controlPoint.paint(g);
        }
        //createSelectedPoint(new Pen(1.0f, Color.black), new Brush(Color.black), path, path.npoints - 1, pathOffset).paint(g);
    }

    protected View createSelectedPoint(Pen pen, Brush brush, SimplePath path, int pos, Point pathOffset)
    {
        View controlPoint = new BoxView(pen, brush, path.xpoints[pos] + pathOffset.x - 3, path.ypoints[pos] + pathOffset.y - 3, 6, 6);
        return controlPoint;
    }
}
