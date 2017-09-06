package ru.biosoft.graphics;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;

import org.json.JSONException;
import org.json.JSONObject;

import ru.biosoft.graphics.font.ColorFont;

public class HtmlView extends View
{
    /** This component is used to create view using BasicHTML routines. */
    private static final JComponent component = new JLabel();

    protected javax.swing.text.View view;

    protected String text;

    protected Point pt;

    protected ColorFont cf;

    public HtmlView(String text, ColorFont cf, Point pt)
    {
        this(text, cf, pt, null);
    }

    public HtmlView(String text, ColorFont cf, Point pt, Dimension preferredSize)
    {
        super(null);

        component.setFont(cf.getFont());
        component.setForeground(cf.getColor());

        view = BasicHTML.createHTMLView(component, text);

        this.text = text;
        this.cf = cf;
        this.pt = pt;

        if( preferredSize == null )
        {
            preferredSize = new Dimension();
            preferredSize.width = Math.round(view.getPreferredSpan(javax.swing.text.View.X_AXIS));
            preferredSize.height = Math.round(view.getPreferredSpan(javax.swing.text.View.Y_AXIS));
        }

        preferredSize.width = Math.round(Math.max(preferredSize.width, view.getMinimumSpan(javax.swing.text.View.X_AXIS)));
        preferredSize.height = Math.round(Math.max(preferredSize.height, view.getMinimumSpan(javax.swing.text.View.Y_AXIS)));
        view.setSize(preferredSize.width, preferredSize.height);
        shape = new Rectangle(pt, preferredSize);
    }

    @Override
    public boolean equals(Object obj)
    {
        if( obj == null || ! ( obj instanceof HtmlView ) )
            return false;
        if( obj == this )
            return true;
        if( !super.equals(obj) )
            return false;
        HtmlView v = (HtmlView)obj;
        if( ( text == null && v.text != null ) || ( text != null && !text.equals(v.text) ) )
            return false;
        if( ( pt == null && v.pt != null ) || ( pt != null && !pt.equals(v.pt) ) )
            return false;
        if( !cf.getColor().equals(v.cf.getColor()) || !cf.getFont().equals(v.cf.getFont()) )
            return false;
        return true;
    }

    @Override
    public void paint(Graphics2D g2)
    {
        if( isVisible() )
        {
            Shape oldClip = g2.getClip();
            g2.setClip(shape);
            view.paint(g2, shape);
            g2.setClip(oldClip);
        }
    }

    @Override
    public void move(int sx, int sy)
    {
        Rectangle rect = (Rectangle)shape;
        rect.setFrame(rect.getX() + sx, rect.getY() + sy, rect.getWidth(), rect.getHeight());
    }

    public void resize(int sx, int sy)
    {
        Rectangle rect = (Rectangle)shape;
        rect.setFrame(rect.getX(), rect.getY(), rect.getWidth() + sx, rect.getHeight() + sy);
    }

    @Override
    public JSONObject toJSON() throws JSONException
    {
        //return getAsComplexTextView().toJSON();
        JSONObject result = super.toJSON();
        result.put("text", text);
        result.put("font", cf.toJSON());
        result.put("x", shape.getBounds().x);
        result.put("y", shape.getBounds().y);
        result.put("width", shape.getBounds().width);
        result.put("height", shape.getBounds().height);
        return result;
    }
}
