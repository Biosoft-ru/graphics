package ru.biosoft.graphics;

import java.util.ListResourceBundle;

/**
* Pen message bundle
*/
public class PenMessageBundle extends ListResourceBundle
{
    @Override
    protected Object[][] getContents()
    {
        return contents;
    }

    private Object[][] contents =
    {
        { "DISPLAY_NAME",       "Pen" },
        { "SHORT_DESCRIPTION",  "Properties of the pen" },

        { "COLOR_NAME",         "Color" },
        { "COLOR_DESCRIPTION",  "Color of the pen" },

        { "WIDTH_NAME",         "Width" },
        { "WIDTH_DESCRIPTION",  "Width of the pen" },

        { "STROKE_NAME",        "Stroke" },
        { "STROKE_DESCRIPTION", "Stroke of the pen" },

    };
}// end of class MessagesBundle
