package ru.biosoft.graphics;

import java.util.ListResourceBundle;

/**
* View message bundle
*/

public class CompositeViewMessageBundle extends ListResourceBundle
{
    @Override
    protected Object[][] getContents()
    {
        return contents;
    }

    private Object[][] contents =
    {
        { "DISPLAY_NAME",       "View" },
        { "SHORT_DESCRIPTION",  "View properties" },
    };
}// end of class MessagesBundle
