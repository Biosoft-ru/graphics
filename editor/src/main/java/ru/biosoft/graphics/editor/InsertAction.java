package ru.biosoft.graphics.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * The <code>InsertAction</code> class extends functionality of AbstractAction.
 *
 */
public class InsertAction extends AbstractAction
{
	private final static Logger log = Logger.getLogger(InsertAction.class.getName());
	
	/** Type of element to be created and inserted during this action processing.*/
    public static final String TYPE = "type";

    /** Initialise icon from the specified image file. */
    public static Icon getIcon(String imageFile)
    {
        if( imageFile != null )
        {
            try
            {
                URL url = imageFile.getClass().getResource(imageFile);
                return new ImageIcon(url);
            }
            catch( Exception e )
            {
                log.warning("Icon " + imageFile + " initialization error");
            }
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    //

    /**
     * Dummy constructor. It is suggessted that action will be initialised using ActionInitialiser.
     */
    public InsertAction()
    {
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * type of element to be inserted, and action name, long/short description string,
     * mnemonic key, icon and action listener.
     *
     * @param type       type of element to be created and inserted when this action is processed
     * @param name       name of action
     * @param shortDesc  shortDesc short description of action
     * @param longDesc   longDesc long description of action
     * @param mnemonic   mnemonic hot key
     * @param imageFile  image for icon  on button or menu items
     * @param listener   listener listener of action
     */
    public InsertAction(Object type, String name, String shortDesc, String longDesc, int mnemonic, String imageFile, ActionListener listener)
    {
        putValue(TYPE, type);
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION, shortDesc);
        putValue(LONG_DESCRIPTION, longDesc);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(SMALL_ICON, getIcon(imageFile));

        if( listener != null )
            addActionListener(listener);
    }

    /**
     * Creates InsertAction for the specified type.
     * Information for name and short descriptions are extracted from type BeanInfo.
     *
     * @param type type of element to be created and inserted during the action processing.
     * @param icon - iacon for this action
     */
    public InsertAction(Object type, Icon icon)
    {
        putValue(TYPE, type);
        putValue(SMALL_ICON, icon);

        try
        {
            if( type instanceof Class )
            {
                BeanInfo bi;
                BeanDescriptor bd;
                bi = Introspector.getBeanInfo((Class)type);
                bd = bi.getBeanDescriptor();
                putValue(NAME, bd.getDisplayName());
                putValue(SHORT_DESCRIPTION, "<html><b>" + bd.getDisplayName() + "</b>" + "<br>" + bd.getShortDescription());
            }
            else
            {
                putValue(NAME, type.toString());
                putValue(SHORT_DESCRIPTION, "<html><b>" + type.toString() + "</b>");
            }
        }
        catch( Throwable t )
        {
            log.log(Level.WARNING, "Can not initialise InsertAction for class " + type, t);
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Overridden method for translation action events to the all listeners
     * @param evt ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        fireActionPerformed(evt);
    }

    protected void fireActionPerformed(ActionEvent evt)
    {
        ActionEvent event = new ActionEvent(this, evt.getID(), evt.getActionCommand());
        if( actionListeners != null )
        {
            for( int i = actionListeners.size() - 1; i >= 0; i-- )
            {
                ActionListener l = actionListeners.get(i);
                if( l != null )
                    l.actionPerformed(event);
            }
        }
    }

    /** Action listener*/
    private Vector<ActionListener> actionListeners = null;

    /**
     * Adds the specified action listener to receive action event .
     */
    public void addActionListener(ActionListener l)
    {

        if( actionListeners == null )
            actionListeners = new Vector<>();

        actionListeners.add(l);
    }

    public void removeActionListeners(Class clazz)
    {
        for( int i = actionListeners.size() - 1; i >= 0; i-- )
        {
            ActionListener l = actionListeners.get(i);
            if( clazz == l.getClass() )
                actionListeners.remove(l);
        }
    }
}
