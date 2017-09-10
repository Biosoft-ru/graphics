package ru.biosoft.graphics.font;

import java.util.ListResourceBundle;

/**
* ColorFont message bundle
*/

public class ColorFontMessageBundle extends ListResourceBundle
{
    @Override
    protected Object[][] getContents()
    {
        return contents;
    }

    private Object[][] contents =
    {
        { "DISPLAY_NAME",       "Font" },
        { "SHORT_DESCRIPTION",  "Properties of the font" },
    };
}// end of class MessagesBundle
