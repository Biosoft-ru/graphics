package ru.biosoft.graphics.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.developmentontheedge.beans.annot.PropertyDescription;
import com.developmentontheedge.beans.annot.PropertyName;
import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.Option;
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
                pen.setStroke(createBasicStroke(getArrayByPattern(arr[4])));
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
    	//return StreamEx.ofValues(arrayToPattern, val -> Arrays.equals( array, val )).findAny().orElse("~");
    	
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

    public static class PenInfo extends Option
    {
        String stroke;
        float width;
        Color color;

        private static HashMap<String, float[]> nameToArray = new LinkedHashMap<String, float[]>()
        {
            {
                put("Solid",    SOLID);
                put("Dashed",   DASHED);
                put("Dot", 		DOT);
                put("Dash-dot", DASH_DOT);
            }
        };

        public static String[] getAvailableStrokes()
        {
            return nameToArray.keySet().toArray(new String[nameToArray.size()]);
        }

        protected PenInfo(Pen pen)
        {
            this.color = pen.getColor();
            this.width = (float)pen.getWidth();
            this.stroke = getNameByStroke(pen.getStroke());
        }
         
        @PropertyName("Color")
        @PropertyDescription("Color of the pen.")
        public Color getColor()
        {
            return color;
        }
        public void setColor(Color color)
        {
            Color oldValue = this.color;
            this.color = color;
            firePropertyChange("color", oldValue, color);
        }

        @PropertyName("Width")
        @PropertyDescription("Width of the pen.")
        public float getWidth()
        {
            return width;
        }

        public void setWidth(float width)
        {
            float oldValue = this.width;
            this.width = width;
            firePropertyChange( "width", Float.valueOf( oldValue ), Float.valueOf( width ) );
        }

        @PropertyName("Stroke")
        @PropertyDescription("Stroke of the pen.")
        public String getStroke()
        {
            return stroke;
        }
        public void setStroke(String stroke)
        {
            String oldValue = this.stroke;
            this.stroke = stroke;
            firePropertyChange("stroke", oldValue, stroke);
        }

        public static String getNameByStroke(BasicStroke basicStroke)
        {
            float[] array = basicStroke.getDashArray();
            
            for(String name : nameToArray.keySet() ) 
            {
            	if( Arrays.equals(array, nameToArray.get(name)) )
            		return name;
            }

            return "Custom";
       		//StreamEx.ofKeys(nameToArray, val -> Arrays.equals( array, val )).findAny().orElse("Custom");
        }
        
        public static BasicStroke getStrokeByName(BasicStroke oldStroke, String name)
        {
            if(oldStroke == null)
                return createBasicStroke( nameToArray.get( name ) );
            
            return new BasicStroke(oldStroke.getLineWidth(), oldStroke.getEndCap(), oldStroke.getLineJoin(), oldStroke.getMiterLimit(), nameToArray.get(name), 0);
        }

        public Pen getPen()
        {
            Pen pen = new Pen();
            pen.setColor(color);
            pen.setStroke(getStrokeByName( null, stroke ));
            pen.setWidth(width);
            return pen;
        }
    }

    public static class PenInfoBeanInfo extends BeanInfoEx
    {
        public PenInfoBeanInfo()
        {
            super(PenInfo.class);
        }

        @Override
        public void initProperties() throws Exception
        {
            add("color");
            add("width");
            addWithTags("stroke", PenInfo.getAvailableStrokes());
        }
    }

    public static BasicStroke createBasicStroke(float[] array)
    {
        return new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, array, 0);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    //

    @Override
    protected void buttonPressed()
    {
    	Pen pen = (Pen) getValue();	
		PenInfo penInfo = new PenInfo(pen.clone());
		PropertyInspector inspector = new PropertyInspector();
        inspector.explore(penInfo);

        int result = JOptionPane.showOptionDialog(JOptionPane.getRootFrame(), inspector, "Line spec settings:",
		                                          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		if(result == JOptionPane.OK_OPTION)
		{
			setValue(penInfo.getPen());
		}
    }
}
