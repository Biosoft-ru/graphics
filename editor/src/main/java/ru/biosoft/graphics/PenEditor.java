package ru.biosoft.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.developmentontheedge.beans.swing.PropertyInspector;

import ru.biosoft.graphics.ComplexTextView;
import ru.biosoft.graphics.Pen;

public class PenEditor extends StrokeTextEditor
{
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
        textField.setText(getUserPenString(pen));
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

    private String getUserPenString(Pen pen)
    {
        if (pen == null)
            return "Auto";
        if( pen.getColor() == null )
            return pen.getWidth() + ";" + getUserStrokeString(pen.getStroke());
        Color color = pen.getColor();
        return pen.getWidth() + ";" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + ";"
                + getUserStrokeString(pen.getStroke());
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
