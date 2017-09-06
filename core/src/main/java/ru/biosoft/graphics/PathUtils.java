package ru.biosoft.graphics;

import java.awt.Point;
import java.awt.Rectangle;

public class PathUtils
{
    public static boolean lineIntersects(int x1, int y1, int x2, int y2, Rectangle rect)
    {
        if( horizontalLineIntersect(x1, y1, x2, y2, rect.x, rect.x + rect.width, rect.y) )
        {
            return true;
        }
        else if( horizontalLineIntersect(x1, y1, x2, y2, rect.x, rect.x + rect.width, rect.y + rect.height) )
        {
            return true;
        }
        else if( verticalLineIntersect(x1, y1, x2, y2, rect.x, rect.y, rect.y + rect.height) )
        {
            return true;
        }
        else if( verticalLineIntersect(x1, y1, x2, y2, rect.x + rect.width, rect.y, rect.y + rect.height) )
        {
            return true;
        }
        return false;
    }

    protected static boolean horizontalLineIntersect(int x1, int y1, int x2, int y2, int lineX1, int lineX2, int lineY)
    {
        if( lineY < Math.min(y1, y2) || lineY > Math.max(y1, y2) || ( y1 == y2 ) )
            return false;

        int x = x1 + ( ( x2 - x1 ) * ( lineY - y1 ) ) / ( y2 - y1 );
        if( x >= lineX1 && x <= lineX2 )
        {
            return true;
        }

        return false;
    }

    protected static boolean verticalLineIntersect(int x1, int y1, int x2, int y2, int lineX, int lineY1, int lineY2)
    {
        if( lineX < Math.min(x1, x2) || lineX > Math.max(x1, x2) || ( x1 == x2 ) )
            return false;

        int y = y1 + ( ( y2 - y1 ) * ( lineX - x1 ) ) / ( x2 - x1 );
        if( y >= lineY1 && y <= lineY2 )
        {
            return true;
        }

        return false;
    }

    public static int getNearestSegment(SimplePath path, Point point)
    {
        double recordDistance = Double.MAX_VALUE;
        int recordSegment = -1;

        for( int i = 0; i < path.npoints - 1; i++ )
        {
            double dist = ( ( path.xpoints[i] - point.x ) * ( path.xpoints[i] - point.x ) ) + ( ( path.ypoints[i] - point.y ) * ( path.ypoints[i] - point.y ) );
            dist += ( ( path.xpoints[i + 1] - point.x ) * ( path.xpoints[i + 1] - point.x ) ) + ( ( path.ypoints[i + 1] - point.y ) * ( path.ypoints[i + 1] - point.y ) );
            dist /= ( ( path.xpoints[i + 1] - path.xpoints[i] ) * ( path.xpoints[i + 1] - path.xpoints[i] ) ) + ( ( path.ypoints[i + 1] - path.ypoints[i] ) * ( path.ypoints[i + 1] - path.ypoints[i] ) );
            if( dist < recordDistance )
            {
                recordDistance = dist;
                recordSegment = i;
            }
        }
        return recordSegment;
    }
}
