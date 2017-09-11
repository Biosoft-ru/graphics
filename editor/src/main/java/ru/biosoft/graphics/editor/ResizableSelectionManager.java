package ru.biosoft.graphics.editor;

/**
 *
 */
public class ResizableSelectionManager extends MultipleSelectionManager
{
    ////////////////////////////////////////////////////////////////////////////
    // Constructor
    //

    public ResizableSelectionManager(ViewPane viewPane, ViewEditorHelper helper)
    {
        super(viewPane);

        this.helper = helper;
    }
}
