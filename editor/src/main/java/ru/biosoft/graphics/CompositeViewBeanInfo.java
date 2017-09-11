package ru.biosoft.graphics;

import com.developmentontheedge.beans.BeanInfoEx;

public class CompositeViewBeanInfo extends BeanInfoEx
{
    public CompositeViewBeanInfo()
    {
        super(CompositeView.class, CompositeViewMessageBundle.class.getName());
        setBeanEditor(CompositeViewViewer.class);
    }
}
