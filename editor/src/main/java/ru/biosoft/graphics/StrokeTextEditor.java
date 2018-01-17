package ru.biosoft.graphics;

import java.awt.BasicStroke;
import java.util.Arrays;
import java.util.HashMap;

import com.developmentontheedge.beans.editors.TextButtonEditor;

public abstract class StrokeTextEditor extends TextButtonEditor
{
    public final static float[] SOLID = Pen.SOLID;
    public final static float[] DASHED = Pen.DASHED;
    public final static float[] DOT = Pen.DOT;
    public final static float[] DASH_DOT = Pen.DASH_DOT;

    private static HashMap<String, float[]> patternToArray = new HashMap<String, float[]>()
    {
        {
            put( "-", SOLID );
            put( "--", DASHED );
            put( "---", DASHED );
            put( "..", DOT );
            put( ".", DOT );
            put( ":", DOT );
            put( "-.", DASH_DOT );
            put( "-.-", DASH_DOT );
        }
    };

    private static HashMap<float[], String> arrayToPattern = new HashMap<float[], String>()
    {
        {
            put( SOLID, "-" );
            put( DASHED, "--" );
            put( DOT, ":" );
            put( DASH_DOT, "-." );
        }
    };

    public static String getUserStrokeString(BasicStroke stroke)
    {
        return getPatternByArray( stroke.getDashArray() );
    }

    private static String getPatternByArray(float[] array)
    {
        for( float[] val : arrayToPattern.keySet() )
        {
            if( Arrays.equals( val, array ) )
                return arrayToPattern.get( val );
        }
        return "~";
    }

    public static float[] getArrayByPattern(String pattern)
    {
        return patternToArray.getOrDefault( pattern, SOLID );
    }
}
