package ru.biosoft.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.developmentontheedge.beans.editors.TextButtonEditor;
import com.developmentontheedge.beans.swing.PropertyInspector;

import ru.biosoft.graphics.ComplexTextView;
import ru.biosoft.graphics.Pen;

public class PenEditor extends TextButtonEditor
{
    final static float[] SOLID    = null;
    final static float[] DASHED   = new float[] {9, 6};
    final static float[] DOT      = new float[] {2, 9};
    final static float[] DASH_DOT = new float[] {9, 3, 2, 3};

    private static HashMap<String, float[]> patternToArray = new HashMap<String, float[]>()
    {
        {
            put("-",   SOLID);
            put("--",  DASHED);
            put("---", DASHED);
            put("..",  DOT);
            put(".",   DOT);
            put(":",   DOT);
            put("-.",  DASH_DOT);
            put("-.-", DASH_DOT);
        }
    };

    private static HashMap<float[], String> arrayToPattern = new HashMap<float[], String>()
    {
        {
            put(SOLID,    "-");
            put(DASHED,   "--");
            put(DOT,      ":");
            put(DASH_DOT, "-.");
        }
    };

    public PenEditor()
    {
        textField.setEditable(true);
        getButton().setText("");

        URL url = PenEditor.class.getResource("resources/edit.gif");
        getButton().setIcon(new ImageIcon(url));
    }

    @Override
    public void setValue(Object value)
    {
        Pen pen = (Pen)value;
        textField.setText(getUserString(pen));
        firePropertyChange();
    }

    @Override
    public Object getValue()
    {
        try
        {
            return getPenByUserString(textField.getText());
        }
        catch( Exception ex )
        {
            return null;
        }
    }

    private String getUserString(Pen pen)
    {
        if (pen == null)
            return "Auto";
        if( pen.getColor() == null )
            return pen.getWidth() + ";" + getUserString(pen.getStroke());
        Color color = pen.getColor();
        return pen.getWidth() + ";" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + ";"
                + getUserString(pen.getStroke());
    }

    private String getUserString(BasicStroke stroke)
    {
        return this.getPatternByArray(stroke.getDashArray());
    }

    private Pen getPenByUserString(String str)
    {
        if ("Auto".equals(str))
            return null;

        try
        {
            String[] arr = ComplexTextView.split(str, ';');
            float width = Float.parseFloat(arr[0]);

            Color color;

            int red = Integer.parseInt(arr[1]);
            int green = Integer.parseInt(arr[2]);
            int blue = Integer.parseInt(arr[3]);
            color = new Color(red, green, blue);

            Pen pen = new Pen(width, color);

            if( arr.length == 5 && !arr[4].isEmpty() )
            {
                pen.setStroke( Pen.createBasicStroke( getArrayByPattern( arr[4] ) ) );
            }
            pen.setWidth(width);
            return pen;
        }
        catch( Exception ex )
        {
            return null;
        }
    }

    private String getPatternByArray(float[] array)
    {
    	for(float[] val : arrayToPattern.keySet() )
    	{
    		if( Arrays.equals(val, array) )
    			return arrayToPattern.get(val);
    	}
    	return "~";
    }

    private float[] getArrayByPattern(String pattern)
    {
        return patternToArray.getOrDefault(pattern, SOLID);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    //

    @Override
    protected void buttonPressed()
    {
    	Pen pen = (Pen) getValue();
		PropertyInspector inspector = new PropertyInspector();
        inspector.explore( pen );
        inspector.setPreferredSize( new Dimension( 480, 200 ) );
        int result = JOptionPane.showOptionDialog(JOptionPane.getRootFrame(), inspector, "Line spec settings:",
		                                          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		if(result == JOptionPane.OK_OPTION)
		{
            setValue( pen );
		}
    }
}
