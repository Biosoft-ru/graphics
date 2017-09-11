package ru.biosoft.graphics;

import java.util.ListResourceBundle;

/**
 * RulerOptions message bundle
 */
public class RulerOptionsMessageBundle extends ListResourceBundle
{
    @Override
    protected Object[][] getContents()
    {
        return contents;
    }
    
    private Object[][] contents =
    {
        { "DISPLAY_NAME",           "Ruler options" },
        { "SHORT_DESCRIPTION",      "Properties of ruler view" },

        { "MAJORFONT_NAME",         "Major labels font" },
        { "MAJORFONT_DESCRIPTION",  "Font used for painting major labels" },

        { "MINORFONT_NAME",         "Minor labels font" },
        { "MINORFONT_DESCRIPTION",  "Font used for painting minor labels" },

        { "PRECISION_NAME",         "Precision" },
        { "PRECISION_DESCRIPTION",  "Precision of the labels labels" },

        { "AXISPEN_NAME",           "Axis pen" },
        { "AXISPEN_DESCRIPTION",    "Pen used for painting ruler axis" },

        { "TICKSPEN_NAME",          "Ticks pen" },
        { "TICKSPEN_DESCRIPTION",   "Pen used for painting ruler ticks" },

        { "TICKSIZE_NAME",          "Ticks size" },
        { "TICKSIZE_DESCRIPTION",   "Size of ticks in the ruler" },

        { "TEXTOFFSET_NAME",        "Labels offset" },
        { "TEXTOFFSET_DESCRIPTION", "Additional distance between labels and ticks" },

        { "STEP_NAME",              "Step" },
        { "STEP_DESCRIPTION",       "Scale factor of the ruler (number of nucleotides between major ticks)" },

        { "TICKS_NAME",             "Ticks" },
        { "TICKS_DESCRIPTION",      "Number of minor ticks between major ticks)" },
    };
}// end of class MessagesBundle
