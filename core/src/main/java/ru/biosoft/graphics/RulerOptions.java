package ru.biosoft.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ru.biosoft.graphics.font.ColorFont;

/**
 * Class to store options for ruler painting.
 */
public class RulerOptions
{

    /**
     * Creates <CODE>RulerOptions</CODE> and initializes it to the specified font.
     *
     * @param font the specified font
     */
    public RulerOptions(ColorFont font)
    {
        this(font, font, new Dimension(0, 0), new Pen(1, Color.black), new Pen(1, Color.black), new Dimension(10, 5),
                new Dimension(5, 5));
    }

    /**
     * Creates <CODE>RulerOptions</CODE> and initializes it to the specified options.
     *
     * @param majorFont  font for major labels
     * @param minorFont  font for minor labels
     * @param decDig     precision (width - integer part, height - fractional part)
     * @param axisPen    pen to paint main axis of the ruler
     * @param ticksPen   pen to paint ticks of the ruler
     * @param tickSize   size of the ticks
     * @param textOffset shift of labels relative to ruler (width - in X coordinate, height - in Y coordinate)
     */
    public RulerOptions(ColorFont majorFont, ColorFont minorFont, Dimension decDig, Pen axisPen, Pen ticksPen,
            Dimension tickSize, Dimension textOffset)
    {
        this.majorFont  = majorFont;
        this.minorFont  = minorFont;
        this.decDig     = decDig;
        this.axisPen    = axisPen;
        this.ticksPen   = ticksPen;
        this.tickSize   = tickSize;
        this.textOffset = textOffset;
    }

    /**
     * <CODE>ColorFont</CODE> for major labels.
     */
    protected ColorFont majorFont;
    public ColorFont getMajorFont()
    {
        return majorFont;
    }
    public void setMajorFont(ColorFont majorFont)
    {
        ColorFont oldValue = this.majorFont;
        this.majorFont = majorFont;
        firePropertyChange("majorFont", oldValue, majorFont);
    }

    /**
     * <CODE>ColorFont</CODE> for minor labels.
     */
    protected ColorFont minorFont;
    public ColorFont getMinorFont()
    {
        return minorFont;
    }
    public void setMinorFont(ColorFont minorFont)
    {
        ColorFont oldValue = this.minorFont;
        this.minorFont = minorFont;
        firePropertyChange("minorFont", oldValue, minorFont);
    }

    /**
     * Precision of tabels, <CODE>Dimension.width</CODE> - integer part, <CODE>Dimension.height</CODE> - fractional part.
     */
    protected Dimension decDig;
    public Dimension getDecDig()
    {
        return decDig;
    }
    public void setDecDig(Dimension decDig)
    {
        Dimension oldValue = this.decDig;
        this.decDig = decDig;
        firePropertyChange("decDig", oldValue, decDig);
    }

    /**
     * <CODE>Pen</CODE> to paint main axis of the ruler.
     */
    protected Pen axisPen;
    public Pen getAxisPen()
    {
        return axisPen;
    }
    public void setAxisPen(Pen axisPen)
    {
        Pen oldValue = this.axisPen;
        this.axisPen = axisPen;
        firePropertyChange("axisPen", oldValue, axisPen);
    }

    /**
     * <CODE>Pen</CODE> to paint ticks of the ruler.
     */
    protected Pen ticksPen;
    public Pen getTicksPen()
    {
        return ticksPen;
    }
    public void setTicksPen(Pen ticksPen)
    {
        Pen oldValue = this.ticksPen;
        this.ticksPen = ticksPen;
        firePropertyChange("ticksPen", oldValue, ticksPen);
    }

    /**
     * Size of the ticks.
     */
    protected Dimension tickSize;
    public Dimension getTickSize()
    {
        return tickSize;
    }
    public void setTickSize(Dimension tickSize)
    {
        Dimension oldValue = this.tickSize;
        this.tickSize = tickSize;
        firePropertyChange("tickSize", oldValue, tickSize);
    }

    /**
     * Shift of labels relative to the ruler, <CODE>Dimension.width</CODE> - in X coordinate,
     * <CODE>Dimension.height</CODE> - in Y coordinate)
     */
    protected Dimension textOffset;
    public Dimension getTextOffset()
    {
        return textOffset;
    }
    public void setTextOffset(Dimension textOffset)
    {
        Dimension oldValue = this.textOffset;
        this.textOffset = textOffset;
        firePropertyChange("textOffset", oldValue, textOffset);
    }

    /**
     * Number of minor ticks between major labels.
    *
    * Package access because this parameter can be changed during ruler painting with
    * step=0. In this case painting algorithm calculate appropriate value for step and assigns
    * it without firing PropertyChange.
     */
    int ticks;
    public int getTicks()
    {
        return ticks;
    }
    
    int step;
    public int getStep()
    {
        return step;
    }

    public RulerOptions clone()
    {
        RulerOptions rulerOptions = new RulerOptions(new ColorFont(majorFont.getFont(), majorFont.getColor()), new ColorFont(
                minorFont.getFont(), minorFont.getColor()), new Dimension(decDig.width, decDig.height), new Pen((float)axisPen.width,
                axisPen.color), new Pen((float)ticksPen.width, ticksPen.color), new Dimension(tickSize.width, tickSize.height), new Dimension(
                textOffset.width, textOffset.height));
        return rulerOptions;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Listeners
    //
    
	protected PropertyChangeSupport pcSupport = null;

	public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if(pcSupport == null)
            pcSupport = new PropertyChangeSupport(this);
    
        pcSupport.addPropertyChangeListener(listener);
    }

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if(pcSupport == null)
            pcSupport = new PropertyChangeSupport(this);
    
        pcSupport.addPropertyChangeListener(propertyName, listener);
    }

	public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if(pcSupport != null)
            pcSupport.removePropertyChangeListener(listener);
    }

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if(pcSupport != null)
            pcSupport.removePropertyChangeListener(propertyName, listener);
    }

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if(pcSupport != null)
            pcSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
