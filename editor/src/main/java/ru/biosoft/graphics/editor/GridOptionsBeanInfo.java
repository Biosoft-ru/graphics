package ru.biosoft.graphics.editor;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class GridOptionsBeanInfo extends BeanInfoEx
{
    private static final String[] STYLES = new String[] {"Points", "Background grid", "Foreground grid"};

    public GridOptionsBeanInfo()
    {
        super(GridOptions.class, "ru.biosoft.graphics.editor.GridOptionsMessageBundle");
        beanDescriptor.setDisplayName( getResourceString( "DISPLAY_NAME" ) );
        beanDescriptor.setShortDescription( getResourceString( "SHORT_DESCRIPTION" ) );
    }

    @Override
    public void initProperties() throws Exception
    {
        property( "useDefault" ).hidden().title( "USE_DEFAULT_NAME" ).description( "USE_DEFAULT_DESCRIPTION" ).add();

        property( "showGrid" ).title( "SHOW_GRID_NAME" ).description( "SHOW_GRID_DESCRIPTION" ).add();

        PropertyDescriptorEx pde = new PropertyDescriptorEx( "gridStyle", beanClass, "getGridStyleString", "setGridStyleFromString" );
        pde.setHideChildren( true );
        property( pde ).tags( STYLES ).title( "GRID_STYLE_NAME" ).description( "GRID_STYLE_DESCRIPTION" ).add();

        property( "cellSize" ).title( "CELL_SIZE_NAME" ).description( "CELL_SIZE_DESCRIPTION" ).add();

        property( "stepSize" ).title( "STEP_SIZE_NAME" ).description( "STEP_SIZE_DESCRIPTION" ).add();
    }
}
