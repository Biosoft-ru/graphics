package ru.biosoft.graphics;

import java.beans.BeanDescriptor;
import java.beans.DefaultPersistenceDelegate;

import com.developmentontheedge.beans.BeanInfoEx;

public class BrushBeanInfo extends BeanInfoEx
{
    public BrushBeanInfo()
    {
        super(Brush.class, true);
        setBeanEditor(BrushEditor.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        addHidden("paint");
        add("color");
        add("gradient");
        addHidden("color2", "isGradientOptionsHidden");
        addHidden("angle", "isGradientOptionsHidden");
    }

    @Override
    public BeanDescriptor getBeanDescriptor()
    {
        BeanDescriptor descriptor = super.getBeanDescriptor();

        descriptor.setValue( "persistenceDelegate",
                new DefaultPersistenceDelegate(
                new String[] { "paint" } )
                {
                    @Override
                    protected boolean mutatesTo( Object oldInstance, Object newInstance )
                    {
                        return ( newInstance != null &&
                            oldInstance.getClass() == newInstance.getClass() );
                    }
                } );

        return descriptor;
    }
}