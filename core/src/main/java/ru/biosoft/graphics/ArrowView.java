package ru.biosoft.graphics;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Arrays;

import org.json.JSONObject;

public class ArrowView extends CompositeView
{
    public static final int ARROW_TIP = 1;
    public static final int TRIANGLE_TIP = 2;
    public static final int SIMPLE_TIP = 3;
    public static final int DIAMOND_TIP = 4;
    
    protected Pen pen = null;
    
    protected Point start;
    protected Point end;
    protected SimplePath path;
    protected PathView pathView = null;
    
    public static class Tip
    {
        public PolygonView view;
        public float width;
    }

    public static Tip createTip(Pen pen, Brush brush, int tipType)
    {
        int w1 = 10;
        int w2 = 15;
        int h = 5;
        Tip tip = null;
        switch( tipType )
        {
            case ARROW_TIP:
                tip = createArrowTip(pen, brush, w1, w2, h);
                break;
            case DIAMOND_TIP:
                tip = createDiamondTip(pen, brush, w1, w2, h);
                break;
            case TRIANGLE_TIP:
                tip = createTriangleTip(pen, brush, w2, h);
                break;
            case SIMPLE_TIP:
                tip = createSimpleTip(pen, w2, h);
                break;
        }

        return tip;
    }

    public static Tip createArrowTip(Pen pen, Brush brush, int w1, int w2, int h)
    {
        Tip tip = new Tip();
        tip.view = new PolygonView(pen, brush, new int[] { -w1, -w2, 0, -w2}, new int[] {0, h, 0, -h});

        tip.width = w2 - w1;
        return tip;
    }

    public static Tip createTriggerTip(Pen pen, Brush brush, int w, int h)
    {
        return createTriggerTip(pen, brush, w, h, 2, 2);
    }
    public static Tip createTriggerTip(Pen pen, Brush brush, int w, int h, int w2, int h2)
    {
        Tip tip = new Tip();

        tip.view = new PolygonView(pen, brush, new int[] { -w, -w, -w, -w + w2, -w + w2, 0, -w + w2, -w + w2, -w, -w}, new int[] {0, -h, 0, 0,
                -h + h2, 0, h - h2, 0, 0, h});

        tip.width = w;
        return tip;
    }
    
    public static Tip createTriangleTip(Pen pen, Brush brush, int w, int h)
    {
        Tip tip = new Tip();
        tip.view = new PolygonView(pen, brush, new int[] { -w, 0, -w}, new int[] {h, 0, -h});

        tip.width = w;
        return tip;
    }
    
    public static Tip createReverseTriangleTip(Pen pen, Brush brush, int w, int h)
    {
        Tip tip = new Tip();
        tip.view = new PolygonView(pen, brush, new int[] { -w, 0, 0}, new int[] {0, h, -h});

        tip.width = w;
        return tip;
    }

    public static Tip createSimpleTip(Pen pen, int w, int h)
    {
        Tip tip = new Tip();
        tip.view = new PolylineView(pen, new int[] { -w, 0, -w}, new int[] {h, 0, -h});

        tip.width = (float)pen.getWidth();
        return tip;
    }

    public static Tip createDiamondTip(Pen pen, Brush brush, int w1, int w2, int h)
    {
        Tip tip = new Tip();
        tip.view = new PolygonView(pen, brush, new int[] { -w2, -w1, 0, -w1}, new int[] {0, h, 0, -h});

        tip.width = w2 - w1;
        return tip;
    }

    public static Tip createLineTip(Pen pen, Brush brush, int w, int h)
    {
        Tip tip = new Tip();
        tip.view = new PolygonView(pen, brush, new int[] { -w, -w}, new int[] {h, -h});

        tip.width = 0;
        return tip;
    }

    public static Tip createEllipseTip(Pen pen, Brush brush, int r)
    {
        Tip tip = new Tip();
        int edgeCount = 16;
        int xArray[] = new int[edgeCount];
        int yArray[] = new int[edgeCount];
        for( int i = 0; i < edgeCount; i++ )
        {
            xArray[i] = (int) ( r * Math.cos(2 * Math.PI * i / edgeCount) ) - r;
            yArray[i] = (int) ( r * Math.sin(2 * Math.PI * i / edgeCount) );
        }
        tip.view = new PolygonView(pen, brush, xArray, yArray);
        tip.width = 2 * r;
        return tip;
    }
    public static void locateTip(Tip tip, double alpha, int x, int y)
    {
        Polygon pol = (Polygon)tip.view.shape;
        rotate(pol, alpha);
        pol.translate(x, y);
    }

    public static void rotate(Polygon pol, double alpha)
    {
        for( int i = 0; i < pol.npoints; i++ )
        {
            int x = pol.xpoints[i];
            int y = pol.ypoints[i];
            double l = Math.sqrt( ( (double)x ) * x + ( (double)y ) * y);
            double b = Math.asin(y / l);
            if( x < 0 )
                b = Math.PI - b;
            b += alpha;
            pol.xpoints[i] = (int)Math.round(l * Math.cos(b));
            pol.ypoints[i] = (int)Math.round(l * Math.sin(b));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public ArrowView(Pen pen, Brush brush, Point pt0, Point pt1, int startTip, int endTip)
    {
        this(pen, brush, pt0.x, pt0.y, pt1.x, pt1.y, startTip, endTip);
    }

    public ArrowView(Pen pen, Point pt0, Point pt1, int startTip, int endTip)
    {
        this(pen, null, pt0, pt1, startTip, endTip);
    }
    public ArrowView(Pen pen, int x1, int y1, int x2, int y2, int startTip, int endTip)
    {
        this(pen, null, x1, y1, x2, y2, startTip, endTip);
    }

    public ArrowView(Pen pen, Brush brush, int x1, int y1, int x2, int y2, int startTipType, int endTipType)
    {
        this(pen, null, x1, y1, x2, y2, createTip(pen, brush, startTipType), createTip(pen, brush, endTipType));
    }

    public ArrowView(Pen pen, Brush brush, int x1, int y1, int x2, int y2, Tip startTip, Tip endTip)
    {
        this.pen = pen;

        int dx = x2 - x1;
        int dy = y2 - y1;
        double l = Math.sqrt(dx * dx + dy * dy);
        double alpha = Math.asin(dy / l);
        if( dx < 0 )
            alpha = Math.PI - alpha;

        Point2D.Float pt1 = new Point2D.Float(x1, y1);
        Point2D.Float pt2 = new Point2D.Float(x2, y2);

        add(new LineView(pen, pt1, pt2));
        if( startTip != null )
        {
            locateTip(startTip, alpha + Math.PI, x1, y1);
            add(startTip.view);
        }

        if( endTip != null )
        {
            locateTip(endTip, alpha, x2, y2);
            add(endTip.view);
        }

        this.path = new SimplePath(new Point(x1, y1) , new Point(x2, y2));
    }

    public ArrowView(Pen pen, Brush brush, SimplePath path, int startTipType, int endTipType)
    {
        this(pen, brush, path, createTip(pen, brush, startTipType), createTip(pen, brush, endTipType));
    }

    public ArrowView(Pen pen, Brush brush, SimplePath path, Tip startTip, Tip endTip)
    {
        if( path.npoints >= 2 )
        {
            GeneralPath generalPath = new GeneralPath();
            generalPath.moveTo(path.xpoints[0], path.ypoints[0]);
            for( int i = 1; i < path.npoints; i++ )
            {
                if( path.pointTypes[i] == 1 && i < path.npoints - 1 )
                {
                    generalPath.quadTo(path.xpoints[i], path.ypoints[i], path.xpoints[i + 1], path.ypoints[i + 1]);
                    i += 1;
                }
                else
                {
                if( path.pointTypes[i] == 2 && i < path.npoints - 2 )
                {
                    generalPath.curveTo(path.xpoints[i], path.ypoints[i], path.xpoints[i + 1], path.ypoints[i + 1], path.xpoints[i + 2],
                            path.ypoints[i + 2]);
                    i += 2;
                }
                else
                {
                    generalPath.lineTo(path.xpoints[i], path.ypoints[i]);
                }
                }
            }
            pathView = new PathView(pen, generalPath);
            add(pathView);

            if( startTip != null )
            {
                int dx = path.xpoints[1] - path.xpoints[0];
                int dy = path.ypoints[1] - path.ypoints[0];
                double l = Math.sqrt(dx * dx + dy * dy);
                double alpha = Math.asin(dy / l);
                if( dx < 0 )
                    alpha = Math.PI - alpha;
                locateTip(startTip, alpha + Math.PI, path.xpoints[0], path.ypoints[0]);
                add(startTip.view);
            }

            if( endTip != null )
            {
                int dx = path.xpoints[path.npoints - 1] - path.xpoints[path.npoints - 2];
                int dy = path.ypoints[path.npoints - 1] - path.ypoints[path.npoints - 2];
                double l = Math.sqrt(dx * dx + dy * dy);
                double alpha = Math.asin(dy / l);
                if( dx < 0 )
                    alpha = Math.PI - alpha;
                locateTip(endTip, alpha, path.xpoints[path.npoints - 1], path.ypoints[path.npoints - 1]);
                add(endTip.view);
            }
        }

        this.path = path;
    }
    
    public ArrowView(JSONObject json)
    {
        super( json );
    }

    /**
     * Return path for arrow
     */
    public SimplePath getPath()
    {
        return path;
    }


    /**
     * Returns difference between path and view locations
     */
    public Point getPathOffset()
    {
        Point viewLocation = this.getLocation();
        Point pathLocation = new Point(path.xpoints[0], path.ypoints[0]);
        for( int i = 1; i < path.npoints; i++ )
        {
            if( path.xpoints[i] < pathLocation.x )
                pathLocation.x = path.xpoints[i];
            if( path.ypoints[i] < pathLocation.y )
                pathLocation.y = path.ypoints[i];
        }

        return new Point(viewLocation.x - pathLocation.x, viewLocation.y - pathLocation.y);
    }

    /**
     * Check intersection between path and rectangle
     */
    @Override
    public boolean intersects(Rectangle rect)
    {
        if( pathView != null )
        {
            if( pathView.intersects(rect) )
            {
                return true;
            }
        }
        else if( super.intersects(rect) )
        {
            return true;
        }

        if( path == null || path.npoints < 2 )
            return false;

        Point offset = getPathOffset();
        Rectangle correctedRect = new Rectangle(rect.x - offset.x, rect.y - offset.y, rect.width, rect.height);

        for( int i = 1; i < path.npoints - 1; i++ )
        {
            if( correctedRect.contains(path.xpoints[i], path.ypoints[i]) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns priority of the view.
     * 
     * @param rect specified rectangle
     * @return 1 if control point selected, 0 otherwise
     */
    @Override
    public int getSelectionPriority(Rectangle rect)
    {
        if( path == null || path.npoints < 2 )
            return 0;
        
        Point offset = getPathOffset();
        Rectangle correctedRect = new Rectangle(rect.x - offset.x, rect.y - offset.y, rect.width, rect.height);

        for( int i = 1; i < path.npoints - 1; i++ )
        {
            if( correctedRect.contains(path.xpoints[i], path.ypoints[i]) )
            {
                return 1;
            }
        }
        return 0;
    }

    public PathView getPathView()
    {
        return pathView;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || !(obj instanceof ArrowView)) return false;
        if(!super.equals(obj)) return false;
        ArrowView v = (ArrowView)obj;
        if(v.getPath().npoints != path.npoints) return false;
        if(!Arrays.equals(v.getPath().xpoints, path.xpoints)) return false;
        if(!Arrays.equals(v.getPath().ypoints, path.ypoints)) return false;
        if(!Arrays.equals(v.getPath().pointTypes, path.pointTypes)) return false;
        return true;
    }
}
