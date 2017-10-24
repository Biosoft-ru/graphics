
package ru.biosoft.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import com.developmentontheedge.beans.editors.ColorComboBox;
import com.developmentontheedge.beans.editors.ColorEditor;

import ru.biosoft.graphics.Brush;

public class BrushEditor extends ColorEditor
{
    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        Object obj = getValue();
        if( obj instanceof Brush )
        {
            Paint paint = ( (Brush)obj ).getPaint();
            if( paint instanceof Color )
            {
                return ColorComboBox.getValueRenderer((Color)paint);
            }
            else if( paint instanceof GradientPaint )
            {
                JPanel complexPanel = new JPanel(new GridLayout(1, 2));
                complexPanel.setBackground(Color.white);
                complexPanel.add(ColorComboBox.getValueRenderer( ( (GradientPaint)paint ).getColor1()));
                complexPanel.add(ColorComboBox.getValueRenderer( ( (GradientPaint)paint ).getColor2()));
                return complexPanel;
            }
        }
        return ColorComboBox.getValueRenderer(new Color(0, 0, 0));
    }

    @Override
    public Component getCustomEditor()
    {
        Color color1 = null;
        Color color2 = null;
        Object obj = getValue();
        if( obj instanceof Brush )
        {
            Paint paint = ( (Brush)obj ).getPaint();
            if( paint instanceof Color )
            {
                color1 = (Color)paint;
            }
            else if( paint instanceof GradientPaint )
            {
                color1 = ( (GradientPaint)paint ).getColor1();
                color2 = ( (GradientPaint)paint ).getColor2();
            }
        }
        else
        {
            color1 = new Color(0, 0, 0);
        }

        if( color2 == null )
        {
            ColorComboBox comboBox = ColorComboBox.getInstance(color1);
            comboBox.addColorPropertyChangeListener(new PropertyChangeListener()
            {
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    Color color = (Color)evt.getNewValue();
                    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                    setValue(new Brush(color));
                }
            });

            return comboBox;
        }
        else
        {
            final ColorComboBox comboBox1 = ColorComboBox.getInstance(color1);
            final ColorComboBox comboBox2 = ColorComboBox.getInstance(color2);

            comboBox1.addColorPropertyChangeListener(new PropertyChangeListener()
            {
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    Color color = (Color)evt.getNewValue();
                    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                    setValue(new Brush(color, (Color)comboBox2.getSelectedItem()));
                }
            });
            comboBox2.addColorPropertyChangeListener(new PropertyChangeListener()
            {
                @Override
                public void propertyChange(PropertyChangeEvent evt)
                {
                    Color color = (Color)evt.getNewValue();
                    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                    setValue(new Brush((Color)comboBox1.getSelectedItem(), color));
                }
            });

            JPanel complexPanel = new JPanel(new GridLayout(1, 2));
            complexPanel.setBackground(Color.white);
            complexPanel.add(comboBox1);
            complexPanel.add(comboBox2);

            return complexPanel;
        }
    }

    @Override
    protected Object processValue()
    {
        return null;
    }
}
