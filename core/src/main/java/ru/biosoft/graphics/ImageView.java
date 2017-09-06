package ru.biosoft.graphics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Paints GIF or JPEG image.
 *
 * You can use {@link AffineTransform} to scale the image.
 */
public class ImageView extends View implements ImageObserver
{
    /** Image. */
    protected Image image;
    protected String path;

    ////////////////////////////////////////
    // Constructors
    //

    /**
     * Constructs ImageView object for the specified Image object.
     *
     * If width or height less then 1, then they will be initialized
     * using {@link Image#getWidth} and {@link Image#getHeight} methods.
     */
    public ImageView(Image image, int x, int y, int width, int height)
    {
        super(new Rectangle(x, y, width, height));
        this.image = image;

        validateImageSize();
        at.setToTranslation(x, y);
    }

    public ImageView(Image image, int x, int y)
    {
        this(image, x, y, 0, 0);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Utility methods
    //
    /**
     * Implements the image observer interface.
     */
    @Override
    public boolean imageUpdate(Image img, int info,
                               int x, int y, int width, int height)
    {
        // stub
        if( info != ALLBITS )
            return true;

        return false;
    }

    protected void validateImageSize()
    {
        Rectangle rect = (Rectangle)shape;
        if(rect.width < 1 || rect.height < 1)
        {
            rect.width  = image.getWidth(this);
            rect.height = image.getHeight(this);
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // View interface implementation
    //

    /**
     * Move the image (left top corner) to specified point.
     *
     * @param p specified point.
     */
    @Override
    public void move(int tx, int ty)
    {
        Rectangle rect = (Rectangle)shape;
        rect.setFrame(rect.getX() + tx, rect.getY() + ty, rect.getWidth(), rect.getHeight());

//        at.setToTranslation(rect.x, rect.y);
        at.setTransform(at.getScaleX(), at.getShearX(), at.getShearY(), at.getScaleY(), rect.x,  rect.y); // restore scale also
    }

    @Override
    public void paint(Graphics2D g2)
    {
        if ( isVisible() )
        {
            g2.drawImage(image, at, this);
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        Rectangle pos = (Rectangle)shape;
        result.put("x", pos.x);
        result.put("y", pos.y);
        result.put("width", pos.width);
        result.put("height", pos.height);
        result.put("path", path);
        return result;
    }
}
