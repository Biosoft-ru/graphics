package ru.biosoft.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import ru.biosoft.graphics.editor.ViewPane;

import com.developmentontheedge.beans.editors.CustomEditorSupport;

public class CompositeViewViewer extends CustomEditorSupport
{
    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        Object value = getValue();
        CompositeView view = null;
        if( value instanceof CompositeView )
        {
            view = (CompositeView)value;
        }
        else
        {
            view = new CompositeView();
//            log.error("Incorrect view value: " + value);
        }
        ViewPane pane = new ViewPane();
        pane.setScrollPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pane.setBackground(Color.white);
        pane.setView(view);
        Rectangle bounds = view.getBounds();
        if( parent instanceof JTable )
        {
            double rowHeight = ( (JTable)parent ).getRowHeight();
            double scale = rowHeight / bounds.height;
            pane.scale(scale, scale);
        }
        pane.setPreferredSize(new Dimension(0, bounds.height));
        return pane;
    }

    @Override
    public Component getCustomEditor(Component parent, boolean isSelected)
    {
        return getCustomRenderer(parent, isSelected, false);
    }
}
