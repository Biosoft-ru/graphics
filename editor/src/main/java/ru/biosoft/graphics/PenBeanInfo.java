package ru.biosoft.graphics;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.editors.StringTagEditor;

public class PenBeanInfo extends BeanInfoEx
{
    public PenBeanInfo()
    {
        super(Pen.class, "ru.biosoft.graphics.PenMessageBundle");

        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
    }

    @Override
    protected void initProperties() throws Exception
    {
        property( "color" ).title( "COLOR_NAME" ).description( "COLOR_DESCRIPTION" ).add();
        property( "width" ).editor( PenWidthSelector.class ).title( "WIDTH_NAME" ).description( "WIDTH_DESCRIPTION" ).add();
        property( "stroke" ).hidden().title( "STROKE_NAME" ).description( "STROKE_DESCRIPTION" ).add();
        property( "strokeAsString" ).tags( Pen.getAvailableStrokes() ).title( "STROKE_NAME" ).description( "STROKE_DESCRIPTION" ).add();
    }

    public static class PenWidthSelector extends StringTagEditor
    {
        @Override
        public String getAsText()
        {
            return String.valueOf( getValue() );
        }

        @Override
        public void setAsText(String text)
        {
            try
            {
                setValue( Double.parseDouble( text ) );
            }
            catch( NumberFormatException e )
            {
                setValue(1.0);
            }
        }

        @Override
        public String[] getTags()
        {
            return new String[] {"0.0", "0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "4.0", "5.0"};
        }
    }

}