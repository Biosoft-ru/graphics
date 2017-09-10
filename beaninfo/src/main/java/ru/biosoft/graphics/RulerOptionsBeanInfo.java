package ru.biosoft.graphics;

import java.beans.IntrospectionException;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;
import com.developmentontheedge.beans.editors.FontEditor;

public class RulerOptionsBeanInfo extends BeanInfoEx
{
    public RulerOptionsBeanInfo() throws IntrospectionException
    {
        super(RulerOptions.class, RulerOptionsMessageBundle.class.getName());

        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));

        add(new PropertyDescriptorEx("majorFont", beanClass),
            FontEditor.class,
            getResourceString("MAJORFONT_NAME"),           //  display_name ()
            getResourceString("MAJORFONT_DESCRIPTION"));   //  description
                                                                              
        add(new PropertyDescriptorEx("minorFont", beanClass),
            FontEditor.class,
            getResourceString("MINORFONT_NAME"),
            getResourceString("MINORFONT_DESCRIPTION"));

        add(new PropertyDescriptorEx("decDig", beanClass),
            getResourceString("PRECISION_NAME"),
            getResourceString("PRECISION_DESCRIPTION"));

        add(new PropertyDescriptorEx("axisPen", beanClass),
            getResourceString("AXISPEN_NAME"),
            getResourceString("AXISPEN_DESCRIPTION"));

        add(new PropertyDescriptorEx("ticksPen", beanClass),
            getResourceString("TICKSPEN_NAME"),
            getResourceString("TICKSPEN_DESCRIPTION"));

        add(new PropertyDescriptorEx("tickSize", beanClass),
            getResourceString("TICKSIZE_NAME"),
            getResourceString("TICKSIZE_DESCRIPTION"));

        add(new PropertyDescriptorEx("textOffset", beanClass),
            getResourceString("TEXTOFFSET_NAME"),
            getResourceString("TEXTOFFSET_DESCRIPTION"));

        add(new PropertyDescriptorEx("step", beanClass, "getStep", null),
            getResourceString("STEP_NAME"),
            getResourceString("STEP_DESCRIPTION"));

        add(new PropertyDescriptorEx("ticks", beanClass, "getTicks", null),
            getResourceString("TICKS_NAME"),
            getResourceString("TICKS_DESCRIPTION"));
    }
}