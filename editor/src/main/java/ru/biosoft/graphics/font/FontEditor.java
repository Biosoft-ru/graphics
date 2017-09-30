package ru.biosoft.graphics.font;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.developmentontheedge.beans.editors.CustomEditorSupport;

public class FontEditor extends CustomEditorSupport
{
    JLabel label;

    @Override
    public Component getCustomEditor()
    {
        label = createLabel();

        label.addMouseListener(new MouseAdapter()
                               {
                                   /**
                                    * Invoked when the mouse has been clicked on a component.
                                    */
                                   @Override
                                   public void mousePressed(MouseEvent e)
                                   {
                                       ColorFont colorFont = FontChooser.showDialog(JOptionPane.getRootFrame(), null, (ColorFont)getValue());
                                       Font font = colorFont.getFont();

                                       setValue(colorFont);
                                       JLabel source = (JLabel)e.getSource();
                                       source.setFont(font);
                                       source.setForeground(colorFont.getColor());
                                       source.setText(font.getFamily()+", "+font.getSize());
                                   }
                               });
        return label;
    }

    private JLabel createLabel()
    {
        ColorFont colorFont = (ColorFont)getValue();
        Font font = colorFont.getFont();
        String info = font.getFamily()+", "+font.getSize();
        JLabel label = new JLabel(info, SwingConstants.CENTER);
        label.setForeground(colorFont.getColor());
        label.setBackground(Color.white);
        label.setOpaque(true);
        label.setFont(font);
        
        return label;
    }

    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        if (label==null)
            label = createLabel();

        if (isSelected && parent instanceof JTable)
        {
            label.setBackground(((JTable)parent).getSelectionBackground() );
        }

        return label;
    }


    /** This method should return Object that is result of editing in custom editor. */
    @Override
    protected Object processValue()
    {
        Font value = (Font)getValue();

        if (label == null)
        {
            value = new Font("Courier", Font.PLAIN, 14);
        }
        return value;
    }
}
