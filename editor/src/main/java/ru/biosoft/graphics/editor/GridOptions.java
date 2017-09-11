package ru.biosoft.graphics.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import ru.biosoft.util.WeakPropertyChangeForwarder;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.Option;
import com.developmentontheedge.beans.Preferences;

public class GridOptions extends Option implements PropertyChangeListener
{
    public static final String GRID_OPTIONS = "GridOptions";

    public GridOptions()
    {
        this.useDefault = true;
        this.showGrid = true;
        this.gridStyle = GridStyle.POINTS;
        this.cellSize = 20;
        this.stepSize = 5;
    }

    public GridOptions(String str) throws JSONException
    {
        initFromJSON(new JSONObject(str));
    }

    public GridOptions(Preferences preferences)
    {
        this();
        this.showGrid = preferences.getBooleanValue("showGrid", this.showGrid);
        this.gridStyle = getStyleFromString(preferences.getStringValue("gridStyle", getStyleAsString(this.gridStyle)));
        this.cellSize = preferences.getIntValue("cellSize", this.cellSize);
        this.stepSize = preferences.getIntValue("stepSize", this.stepSize);
    }

    public Preferences getAsPreferences()
    {
        Preferences preferences = new Preferences();
        preferences.setSaveOrder(true);
        try
        {
            GridOptionsMessageBundle messageBundle = new GridOptionsMessageBundle();
            preferences.add(new DynamicProperty("showGrid", messageBundle.getString("SHOW_GRID_NAME"), messageBundle
                    .getString("SHOW_GRID_DESCRIPTION"), Boolean.class, showGrid));
            DynamicProperty styleProperty = new DynamicProperty("gridStyle", messageBundle.getString("GRID_STYLE_NAME"), messageBundle
                    .getString("GRID_STYLE_DESCRIPTION"), String.class, getStyleAsString(gridStyle));
            styleProperty.getDescriptor().setPropertyEditorClass(StyleEditor.class);
            preferences.add(styleProperty);
            preferences.add(new DynamicProperty("cellSize", messageBundle.getString("CELL_SIZE_NAME"), messageBundle
                    .getString("CELL_SIZE_DESCRIPTION"), Integer.class, cellSize));
            preferences.add(new DynamicProperty("stepSize", messageBundle.getString("STEP_SIZE_NAME"), messageBundle
                    .getString("STEP_SIZE_DESCRIPTION"), Integer.class, stepSize));
        }
        catch( Exception e )
        {
            log.error("Can not create grid preferences", e);
        }
        return preferences;
    }

    @Override
    public GridOptions clone()
    {
        return copy(this, new GridOptions());
    }

    public static GridOptions copy(GridOptions source, GridOptions target)
    {
        target.useDefault = source.useDefault;
        target.showGrid = source.showGrid;
        target.cellSize = source.cellSize;
        target.stepSize = source.stepSize;
        target.gridStyle = source.gridStyle;

        //target.firePropertyChgange("*", null, null);

        return target;
    }

    /////////////////////////////////////////////////////////////////
    // Properties
    //

    /** indicates whether clone of default grid options are used */
    private boolean useDefault;

    public boolean isUseDefault()
    {
        return useDefault;
    }

    public void setUseDefault(boolean useDefault)
    {
        boolean oldValue = this.useDefault;
        this.useDefault = useDefault;

        Preferences preferences = Application.getPreferences();
        if( preferences != null )
        {
            Preferences defaultInstance = (Preferences)preferences.getValue(GRID_OPTIONS);

            if( useDefault == false && defaultInstance != null )
                defaultInstance.removePropertyChangeListener(this);

            if( useDefault == true )
            {
                if( defaultInstance == null )
                {
                    try
                    {
                        GridOptionsMessageBundle messageBundle = new GridOptionsMessageBundle();
                        preferences.add(new DynamicProperty(GRID_OPTIONS, messageBundle.getString("DISPLAY_NAME"), messageBundle
                                .getString("SHORT_DESCRIPTION"), Preferences.class, getAsPreferences()));
                        defaultInstance = (Preferences)preferences.getValue(GRID_OPTIONS);
                    }
                    catch( Exception e )
                    {
                        log.error("Can not create grid properties", e);
                    }
                }
                if(defaultInstance != null)
                    new WeakPropertyChangeForwarder(this, defaultInstance);
            }
        }
        json = null;
        firePropertyChange("useDefault", oldValue, useDefault);
    }

    private boolean showGrid;
    public boolean isShowGrid()
    {
        return showGrid;
    }
    public void setShowGrid(boolean showGrid)
    {
        boolean oldValue = this.showGrid;
        this.showGrid = showGrid;
        firePropertyChange("showGrid", oldValue, showGrid);
        if( showGrid != oldValue )
            setUseDefault(false);
        json = null;
    }

    /** different styles to display a grid */
    enum GridStyle
    {
        BACKGROUND_GRID, FOREGROUND_GRID, POINTS
    }

    public String getStyleAsString(GridStyle style)
    {
        if( style == GridStyle.POINTS )
        {
            return "Points";
        }
        else if( style == GridStyle.BACKGROUND_GRID )
        {
            return "Background grid";
        }
        else if( style == GridStyle.FOREGROUND_GRID )
        {
            return "Foreground grid";
        }
        return null;
    }

    public GridStyle getStyleFromString(String style)
    {
        if( style.equals("Background grid") )
        {
            return GridStyle.BACKGROUND_GRID;
        }
        else if( style.equals("Foreground grid") )
        {
            return GridStyle.FOREGROUND_GRID;
        }
        else
        {
            return GridStyle.POINTS;
        }
    }

    private GridStyle gridStyle;
    public GridStyle getGridStyle()
    {
        return gridStyle;
    }

    public void setGridStyle(GridStyle gridStyle)
    {
        GridStyle oldValue = this.gridStyle;
        this.gridStyle = gridStyle;
        firePropertyChange("gridStyle", oldValue, gridStyle);
        if( gridStyle != oldValue )
            setUseDefault(false);
        json = null;
    }

    public String getGridStyleString()
    {
        String styleAsString = getStyleAsString( getGridStyle() );
        return styleAsString != null ? styleAsString : "Points";
    }
    public void setGridStyleFromString(String gridStyle)
    {
        setGridStyle( getStyleFromString( gridStyle ) );
    }

    private int cellSize;
    public int getCellSize()
    {
        return cellSize;
    }

    public void setCellSize(int cellSize)
    {
        int oldValue = this.cellSize;
        this.cellSize = cellSize;
        firePropertyChange("cellSize", oldValue, cellSize);
        if( cellSize != oldValue )
            setUseDefault(false);
        json = null;
    }

    private int stepSize;
    public int getStepSize()
    {
        return stepSize;
    }

    public void setStepSize(int stepSize)
    {
        int oldValue = this.stepSize;
        this.stepSize = stepSize;
        firePropertyChange("stepSize", oldValue, stepSize);
        if( stepSize != oldValue )
            setUseDefault(false);
        json = null;
    }

    /////////////////////////////////////////////////////////////////
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if( this.useDefault )
        {
            String propertyName = evt.getPropertyName();
            if( propertyName.equals("showGrid") )
            {
                this.showGrid = (Boolean)evt.getNewValue();
            }
            else if( propertyName.equals("gridStyle") )
            {
                String strStyle = (String)evt.getNewValue();
                this.gridStyle = getStyleFromString(strStyle);
            }
            else if( propertyName.equals("cellSize") )
            {
                this.cellSize = (Integer)evt.getNewValue();
            }
            else if( propertyName.equals("stepSize") )
            {
                this.stepSize = (Integer)evt.getNewValue();
            }
        }
    }


    public static class StyleEditor extends com.developmentontheedge.beans.editors.StringTagEditorSupport
    {
        private static final String[] TYPES = {"Points", "Background grid", "Foreground grid"};

        public StyleEditor()
        {
            super(TYPES);
        }
    }

    JSONObject json = null;
    public JSONObject toJSON() throws JSONException
    {
        if( json == null )
        {
            json = new JSONObject();
            json.put("useDefault", useDefault);
            json.put("showGrid", showGrid);
            json.put("cellSize", cellSize);
            json.put("stepSize", stepSize);
            json.put("gridStyle", gridStyle.toString());
        }
        return json;
    }

    private void initFromJSON(JSONObject from) throws JSONException
    {
        useDefault = from.getBoolean("useDefault");
        showGrid = from.getBoolean("showGrid");
        cellSize = from.getInt("cellSize");
        stepSize = from.getInt("stepSize");
        gridStyle = GridStyle.valueOf(from.getString("gridStyle"));
    }

    @Override
    public String toString()
    {
        try
        {
            return toJSON().toString();
        }
        catch( Exception ex )
        {
            return "";
        }
        //        return useDefault + ";" + showGrid + ";" + cellSize + ";" + stepSize + ";" + gridStyle;
    }

    @Override
    public boolean equals(Object obj)
    {
        if( ! ( obj instanceof GridOptions ) )
            return false;
        GridOptions options = (GridOptions)obj;
        if (this.showGrid != options.showGrid)
            return false;
        if (this.cellSize != options.cellSize)
            return false;
        if (this.gridStyle != options.gridStyle)
            return false;
        if (this.stepSize != options.stepSize)
            return false;

        return true;
    }
}
