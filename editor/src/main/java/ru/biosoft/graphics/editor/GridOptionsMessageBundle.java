package ru.biosoft.graphics.editor;

import java.util.ListResourceBundle;

public class GridOptionsMessageBundle extends ListResourceBundle
{
    @Override
    protected Object[][] getContents()
    {
        return contents;
    }

    private Object[][] contents =
    {
        { "DISPLAY_NAME",       "Grid options" },
        { "SHORT_DESCRIPTION",  "Diagram editor grid options" },

        { "USE_DEFAULT_NAME",         "Is default" },
        { "USE_DEFAULT_DESCRIPTION",  "Use grid options from the application properties" },
        
        { "SHOW_GRID_NAME",           "Show grid" },
        { "SHOW_GRID_DESCRIPTION",    "Show and use grid for diagram editor" },

        { "GRID_STYLE_NAME",          "Grid style" },
        { "GRID_STYLE_DESCRIPTION",   "Style of grid lines. Possible values:" +
                "<br>-<i>Points</i> - use points for grid " +
                "<br>-<i>Background grid</i> - use background lines for grid " +
                "<br>-<i>Foreground grid</i> - use foreground lines for grid" },
        
        { "CELL_SIZE_NAME",           "Cell size" },
        { "CELL_SIZE_DESCRIPTION",    "Size of grid cell" },
        
        { "STEP_SIZE_NAME",           "Step size" },
        { "STEP_SIZE_DESCRIPTION",    "Minimal step for diagram element editing" },
    };
}// end of class MessagesBundle