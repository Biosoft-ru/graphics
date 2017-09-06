package ru.biosoft.graphics;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.DecimalFormat;

import ru.biosoft.graphics.font.ColorFont;

/**
 * Ruler.
 */
public class Ruler extends CompositeView
{
    /**
     * Ruler type: HORIZONTAL/VERTICAL
     */
    public static final int HORIZONTAL = 1;

    /**
     * Ruler type: GENE/ USUAL.
     * If GENE, the position zero absent.
     */
    public static final int GENE = 2;

    /**
     * Ruler type: TICKS_MAJOR_UP/ABSENT.
     * For vertical ruler this is mean: TICKS_MAJOR_LEFT/ABSENT.
     */
    public static final int TICKS_MAJOR_UP = 4;

    /**
    * Ruler type: TICKS_MAJOR_DOWN/ABSENT.
    * For vertical ruler this is mean: TICKS_MAJOR_RIGHT/ABSENT.
    */
    public static final int TICKS_MAJOR_DOWN = 8;

    /**
     * Ruler type: TICKS_MINOR_UP/ABSENT.
     * For vertical ruler this is mean: TICKS_MINOR_LEFT/ABSENT.
     */
    public static final int TICKS_MINOR_UP = 16;

    /**
    * Ruler type: TICKS_MINOR_DOWN/ABSENT.
    * For vertical ruler this is mean: TICKS_MINOR_RIGHT/ABSENT.
    */
    public static final int TICKS_MINOR_DOWN = 32;

    /**
    * Ruler type: LABELS_MAJOR_SHOW/ABSENT.
    */
    public static final int LABELS_MAJOR_SHOW = 64;

    /**
    * Ruler type: LABELS_MAJOR_UP/LABELS_MAJOR_DOWN.
    * For vertical ruler this is mean: LABELS_MAJOR_LEFT/LABELS_MAJOR_RIGHT
    */
    public static final int LABELS_MAJOR_UP = 128;

    /**
    * Ruler type: LABELS_MINOR_SHOW/ABSENT.
    */
    public static final int LABELS_MINOR_SHOW = 256;

    /**
    * Ruler type: LABELS_MINOR_UP/LABELS_MINOR_DOWN.
    * For vertical ruler this is mean: LABELS_MINOR_LEFT/LABELS_MINOR_RIGHT
    */
    public static final int LABELS_MINOR_UP = 512;

    /**
     * Ruler type.
     */
    protected int type;

    /**
     * Minimum ruler value.
     */
    protected double min;

    /**
     * Maximum ruler value.
     */
    protected double max;

    /**
     * Ruler step (for major ticks).
     */
    protected double step;

    /**
     * Number of minor ticks for one interval
     * before major ticks.
     */
    protected int tPerT;

    /**
     * The scale: number of pixels according to interaval
     * by length 1.0
     */
    protected double scale;

    /**
     * True, if scale direction is reversed,
     * that is min > max value.
     */
    protected boolean isReversed;

    /**
     * Point, that corresponds min value on the ruler axis.
     */
    protected Point anchor;


    /**
     * Options for painting.
     */
    protected RulerOptions options;

    /**
     * pixel/nucleotide for label painting
     */
    protected double density;

    /**
     *
     *
     * (pens, brushes, Fonts, margins etc.) needed
     * to ruler painting. <p>
     *
     * Used GraphicContext attributes: <p>
     *
     * <pre>
     *
     * Pen              Pen_Ruler_Axis
     * Pen              Pen_Ruler_Ticks
     *
     * ColorFont            Font_Ruler_Labels_Major
     * ColorFont            Font_Ruler_Labels_Minor
     *
     * Dimension        Margin_Ruler_Text
     *
     * Dimension        Size_Ruler_TickSize*
     * Dimension        Size_Ruler_DecDig*
     *
     * * number of signs after comma
     *      width  - for major labels
     *      height - for minor labels
     *
     * </pre>
     *
     * 
     */
    public Ruler(int type, Point anchor, double scale, int min, int max, RulerOptions rulerOptions, double density, Graphics graphics)
    {
        /*
         * 1. Initialization
         * 2. min, max & step value checking
         * 3. if step undefined (step == 0), then step & tPerT autodetection
         *   3.1. determs maximum label size
         *   3.2. intervals number = axis length / (maximum label size*2)
         *        assumption: font for major labels is bigger than for
         *        minor labels
         *   3.3. The step 10^n is selected from the left side of exact step and adjusted by multiplyuing
         *   by 2,4,5
         */
        if( min > max )
        {
            isReversed = true;
            int temp = min;
            min = max;
            max = temp;
        }

        // 2.
        this.type = type;
        this.anchor = anchor;
        this.scale = scale;
        this.min = min;
        this.max = max;
        this.options = rulerOptions;
        this.density = density;
        this.tPerT = options.getTicks();

        // 3.
        FontMetrics fmMajor = graphics.getFontMetrics(options.getMajorFont().getFont());
        FontMetrics fmMinor = graphics.getFontMetrics(options.getMinorFont().getFont());

        // 3.1.
        Dimension decDig = options.getDecDig();
        String lMin = valueOf(min, decDig.width);
        String lMax = valueOf(max, decDig.height);

        int labelMaxSize;
        if( ( type & HORIZONTAL ) != 0 )
            labelMaxSize = Math.max(Math.max(fmMajor.stringWidth(lMin), fmMajor.stringWidth(lMax)),
                    Math.max(fmMinor.stringWidth(lMin), fmMinor.stringWidth(lMax)));
        else
            labelMaxSize = Math.max(fmMajor.getHeight(), fmMinor.getHeight());

        // 3.2.
        double length = max - min;
        int intervalNumber = (int) ( length * scale / ( labelMaxSize * 2 ) ) + 1;

        // 3.3.
        double stepApr = length / intervalNumber;
        step = 1;

        if( stepApr > step )
        {
            while( step < stepApr )
            {
                step *= 2;
                tPerT = 1;
                if( step > stepApr )
                    break;
                step *= 2.5;
                tPerT = 4;
                if( step > stepApr )
                    break;
                step *= 2;
                tPerT = 9;
            }
        }
        else
        {
            while( step > stepApr )
            {
                step /= 2;
                tPerT = 1;
                if( step < stepApr )
                    break;
                step /= 2.5;
                tPerT = 4;
                if( step < stepApr )
                    break;
                step /= 2;
                tPerT = 9;
            }
        }
        rulerOptions.step = (int)step;
        rulerOptions.ticks = tPerT;
        initSize(graphics);
    }


    /**
     * Init the ruler.
     */
    protected void initSize(Graphics graphics)
    {
        Dimension d = new Dimension(0, 0);
        if( !isVisible() )
            return;

        // 1.
        Pen pAxis = options.getAxisPen();
        Pen pTicks = options.getTicksPen();
        Dimension dText = options.getTextOffset();
        Dimension dTickSize = options.getTickSize();
        Dimension decDig = options.getDecDig();

        // 2.
        Point pFrom = new Point(anchor.x - d.width, anchor.y - d.height);
        Point pTo = new Point(pFrom.x, pFrom.y);
        int axisLen = (int) ( ( max - min ) * scale );

        if( ( type & GENE ) != 0 && min < 0 && max > 0 )
            axisLen -= scale;

        if( ( type & HORIZONTAL ) != 0 )
            pTo.x += axisLen;
        else
            pTo.y -= axisLen;

        LineView axis = new LineView(pAxis, pFrom.x, pFrom.y, pTo.x, pTo.y);
        if( children.size() == 0 )
        {
            rect = (Rectangle)axis.getBounds().clone();
        }
        else
        {
            rect.add(axis.getBounds());
        }

        // 3.
        int TickUB, tickUB; // ticks upper(left)  boundary
        int TickDB, tickDB; // ticks down (right) boundary

        if( ( type & HORIZONTAL ) != 0 )
            TickUB = pFrom.y;
        else
            TickUB = pFrom.x;

        TickDB = tickDB = tickUB = TickUB;

        if( ( type & TICKS_MAJOR_UP ) != 0 )
        {
            TickUB -= dTickSize.width;
            tickUB -= dTickSize.height;
        }

        if( ( type & TICKS_MAJOR_DOWN ) != 0 )
        {
            TickDB += dTickSize.width;
            tickDB += dTickSize.height;
        }

        if( ( type & ( TICKS_MAJOR_UP | TICKS_MAJOR_DOWN | LABELS_MAJOR_SHOW ) ) != 0 )
        {
            initSizeUsingTicksAndLabels(step, 0, pFrom, pTicks, options.getMajorFont(), graphics, TickUB, TickDB, decDig.width, dText,
                    ( type & ( TICKS_MAJOR_UP | TICKS_MAJOR_DOWN ) ) != 0, ( type & LABELS_MAJOR_SHOW ) != 0,
                    ( type & LABELS_MAJOR_UP ) != 0);
        }

        // 5
        if( ( type & ( TICKS_MINOR_UP | TICKS_MINOR_DOWN | LABELS_MINOR_SHOW ) ) != 0 )
        {
            initSizeUsingTicksAndLabels(step / ( tPerT + 1 ), step, pFrom, pTicks, options.getMinorFont(), graphics, tickUB, tickDB,
                    decDig.height, dText, ( type & ( TICKS_MINOR_UP | TICKS_MINOR_DOWN ) ) != 0, ( type & LABELS_MINOR_SHOW ) != 0,
                    ( type & LABELS_MINOR_UP ) != 0);
        }
    }


    protected void initSizeUsingTicksAndLabels(double step, double stepMajor, Point pFrom, Pen pTicks, ColorFont fLabel, Graphics graphics,
            int tickUB, int tickDB, int decDig, Dimension textOffset, boolean ticksShow, boolean labelsShow, boolean labelsUp)
    {
        FontMetrics fm = graphics.getFontMetrics(fLabel.getFont());

        double cur = step * ( min / step );

        Point pCur = new Point(0, 0);
        if( ( type & HORIZONTAL ) != 0 )
            pCur.y = pFrom.y;
        else
            pCur.x = pFrom.x;


        String label;

        Point pText = new Point(pCur.x, pCur.y);
        if( ( type & HORIZONTAL ) != 0 )
        {
            if( labelsUp )
                pText.y = tickUB - fm.getDescent() - textOffset.height;
            else
                pText.y = tickDB + fm.getAscent() + textOffset.height;
        }
        else
        {
            if( !labelsUp )
                pText.x = tickDB + textOffset.width;
        }

        double shift;
        double curTick = 0;
        if( cur <= min )
        {
            curTick = min;
        }
        else if( cur > max )
        {
            curTick = max;
        }
        else
        {
            curTick = cur - 1;
        }

        if( !isReversed )
            shift = scale * ( curTick - min );
        else
            shift = scale * ( max - curTick );

        if( ( type & GENE ) != 0 && curTick > 0 && min < 0 )
            shift -= scale;

        if( ( type & HORIZONTAL ) != 0 )
            pCur.x = pFrom.x + (int)shift;
        else
            pCur.y = pFrom.y - (int)shift;


        if( ticksShow )
        {
            if( ( type & HORIZONTAL ) != 0 )
            {
                LineView axis = new LineView(pTicks, pCur.x, tickUB, pCur.x, tickDB);
                rect.add(axis.getBounds());
            }
            else
            {
                LineView axis = new LineView(pTicks, tickUB, pCur.y, tickDB, pCur.y);
                rect.add(axis.getBounds());
            }
        }

        if( labelsShow )
        {
            label = valueOf(curTick, decDig);

            if( ( type & HORIZONTAL ) != 0 )
                pText.x = pCur.x - fm.stringWidth(label) / 2;
            else
            {
                pText.y = pCur.y;
                if( labelsUp )
                    pText.x = tickDB - fm.stringWidth(label) - textOffset.width;
            }

            if( pText.x < 0 )
            {
                pText.x = 0;
            }

            TextView text = new TextView(label, new Point(pText.x, pText.y), View.LEFT, fLabel, graphics);
            rect.add(text.getBounds());
        }
    }


    /**
     * Moves the ruler (left/bottom axis end) to the specified point.
     *
     * @param p the specified point.
     */
    @Override
    public void move(Point p)
    {
        rect.setLocation(p.x, p.y);
        anchor.move(p.x, p.y);
    }

    /**
     * Shifts the ruler image by the specified distance.
     *
     * @param d the distance.
     */
    public void offset(Dimension d)
    {
        rect.x += d.width;
        rect.y += d.height;

        anchor.x += d.width;
        anchor.y += d.height;
    }

    protected void paintTicksAndLabels(double step, double stepMajor, Point pFrom, Pen pTicks, ColorFont fLabel, Graphics graphics,
            int tickUB, int tickDB, int decDig, Dimension textOffset, boolean ticksShow, boolean labelsShow, boolean labelsUp,
            boolean shortLabelFormat)
    {
        FontMetrics fm = graphics.getFontMetrics(fLabel.getFont());
        double cur = stepMajor * Math.floor ( min / stepMajor );

        Point pCur = new Point(0, 0);
        if( ( type & HORIZONTAL ) != 0 )
        {
            pCur.y = pFrom.y;
        }
        else
        {
            pCur.x = pFrom.x;
        }

        String label;

        Point pText = new Point(pCur.x, pCur.y);
        if( ( type & HORIZONTAL ) != 0 )
        {
            if( labelsUp )
                pText.y = tickUB - fm.getDescent() - textOffset.height;
            else
                pText.y = tickDB + fm.getAscent() + textOffset.height;
        }
        else
        {
            if( !labelsUp )
                pText.x = tickDB + textOffset.width;
        }

        double shift;
        Rectangle labelBounds = null;
        for( ; cur <= max + step + 1; cur += step )
        {
            double curTick = 0;
            if( cur <= min )
            {
                curTick = min;
            }
            else if( cur > max )
            {
                curTick = max;
            }
            else
            {
                curTick = cur;
            }

            if( ( type & GENE ) != 0 && curTick == 0 )
            {
                continue;
            }

            if( !isReversed )
            {
                shift = scale * ( curTick - min );
            }
            else
            {
                shift = scale * ( max - curTick );
            }

            if( ( type & GENE ) != 0 && curTick > 0 && min < 0 )
                shift -= scale;

            if( ( type & HORIZONTAL ) != 0 )
                pCur.x = pFrom.x + (int)shift;
            else
                pCur.y = pFrom.y - (int)shift;

            if( labelsShow )
            {
                //process Mb = mega base if needed
                if( shortLabelFormat )
                {
                    label = valueOf(curTick / 1000000.0, decDig + 2) + "Mb";
                }
                else
                {
                    label = valueOf(curTick, decDig);
                }

                if( ( type & HORIZONTAL ) != 0 )
                    pText.x = pCur.x - fm.stringWidth(label) / 2;
                else
                {
                    pText.y = pCur.y;
                    if( labelsUp )
                        pText.x = tickDB - fm.stringWidth(label) - textOffset.width;
                }

                Rectangle curLabelBounds = new Rectangle(pText.x, pText.y, fm.stringWidth(label) + fm.stringWidth("0") / 2, fm.getHeight());
                if( labelBounds != null && labelBounds.intersects(curLabelBounds) )
                {
                    continue;
                }
                graphics.setColor(fLabel.getColor());
                graphics.setFont(fLabel.getFont());
                graphics.drawString(label, pText.x, pText.y);
                labelBounds = curLabelBounds;
            }

            if( ticksShow )
            {
                ( (Graphics2D)graphics ).setStroke(pTicks.getStroke());
                graphics.setColor(pTicks.getColor());
                if( ( type & HORIZONTAL ) != 0 )
                {
                    graphics.drawLine(pCur.x, tickUB, pCur.x, tickDB);
                }
                else
                {
                    graphics.drawLine(tickUB, pCur.y, tickDB, pCur.y);
                }
            }
        }
    }

    /**
     * Paint the ruler.
     */
    @Override
    public void paint(Graphics2D graphics)
    {
        super.paint(graphics);
        Point loc = getLocation();
        Rectangle clip = graphics.getClipBounds();
        if( clip == null )
        {
            clip = new Rectangle(0, 0, 0, 0); //Integer.MAX_VALUE
        }
        Dimension d = new Dimension( -loc.x, - ( loc.y + rect.height ));
        if( !isVisible() )
            return;

        // 1.
        Pen pAxis = options.getAxisPen();
        Pen pTicks = options.getTicksPen();
        Dimension dText = options.getTextOffset();
        Dimension dTickSize = options.getTickSize();
        Dimension decDig = options.getDecDig();

        // 2.
        Point pFrom = new Point(anchor.x - d.width, anchor.y - d.height);

        double _min = min;
        double _max = max;

        // setup min
        double delta = ( clip.x - pFrom.x ) / scale;
        double num = (float) ( Math.ceil(delta / step) - 1 );
        min = min + num * step;
        if( min < _min )
        {
            min = _min;
        }
        else
        {
            int diff = (int) ( num * step * scale );
            pFrom.x = pFrom.x + diff;
        }

        // setup max
        delta = clip.width / scale + step;
        num = (float) ( Math.ceil(delta / step) );
        max = min + ( num + 1 ) * step;
        if( max > _max )
        {
            max = _max;
        }

        Point pTo = new Point(pFrom.x, pFrom.y);
        int axisLen = (int) ( ( max - min ) * scale );

        if( ( type & GENE ) != 0 && min < 0 && max > 0 )
            axisLen -= scale;

        if( ( type & HORIZONTAL ) != 0 )
            pTo.x += axisLen;
        else
            pTo.y -= axisLen;

        graphics.setStroke(pAxis.getStroke());
        graphics.setColor(pAxis.getColor());
        graphics.drawLine(pFrom.x, pFrom.y, pTo.x, pTo.y);

        // 3.
        int TickUB, tickUB; // ticks upper(left)  boundary
        int TickDB, tickDB; // ticks down (right) boundary

        if( ( type & HORIZONTAL ) != 0 )
            TickUB = pFrom.y;
        else
            TickUB = pFrom.x;

        TickDB = tickDB = tickUB = TickUB;

        if( ( type & TICKS_MAJOR_UP ) != 0 )
        {
            TickUB -= dTickSize.width;
            tickUB -= dTickSize.height;
        }

        if( ( type & TICKS_MAJOR_DOWN ) != 0 )
        {
            TickDB += dTickSize.width;
            tickDB += dTickSize.height;
        }

        boolean shortLabelFormat = ( density < 0.01 ); //use short label format (example, 125.20 Mb) if pixel/nucleotide less than 0.01
        if( ( type & ( TICKS_MAJOR_UP | TICKS_MAJOR_DOWN | LABELS_MAJOR_SHOW ) ) != 0 )
        {
            paintTicksAndLabels(step, step, pFrom, pTicks, options.getMajorFont(), graphics, TickUB, TickDB, decDig.width, dText,
                    ( type & ( TICKS_MAJOR_UP | TICKS_MAJOR_DOWN ) ) != 0, ( type & LABELS_MAJOR_SHOW ) != 0,
                    ( type & LABELS_MAJOR_UP ) != 0, shortLabelFormat);
        }

        // 5
        if( ( type & ( TICKS_MINOR_UP | TICKS_MINOR_DOWN | LABELS_MINOR_SHOW ) ) != 0 )
        {
            paintTicksAndLabels(step / ( tPerT + 1 ), step, pFrom, pTicks, options.getMinorFont(), graphics, tickUB, tickDB, decDig.height,
                    dText, ( type & ( TICKS_MINOR_UP | TICKS_MINOR_DOWN ) ) != 0, ( type & LABELS_MINOR_SHOW ) != 0,
                    ( type & LABELS_MINOR_UP ) != 0, shortLabelFormat);
            min = _min;
            max = _max;
        }
    }

    /**
     * Returns string, that represents double value with specified number of decimal digits.
     *
     * <p>If the value indeed is the integer then the decimal digits are omitted.
     *
     * @param value double value
     * @param decDig number of decimal digits
     */
    public static String valueOf(double value, int decDig)
    {
        StringBuilder decs = new StringBuilder("");
        if( decDig > 0 )
        {
            decs.append('.');
            for( int i = 0; i < decDig; i++ )
                decs.append('#');
        }

        return new DecimalFormat("##,###,###,###,###,###,###,###,###,###,###" + decs).format(value);
    }
}
