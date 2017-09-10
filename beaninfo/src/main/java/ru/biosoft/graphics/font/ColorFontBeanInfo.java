package ru.biosoft.graphics.font;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.editors.FontEditor;

public class ColorFontBeanInfo extends BeanInfoEx
{
    public ColorFontBeanInfo()
    {
        super(ColorFont.class, "ru.biosoft.graphics.font.ColorFontMessageBundle");

        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
        this.setBeanEditor(FontEditor.class);
    }
}