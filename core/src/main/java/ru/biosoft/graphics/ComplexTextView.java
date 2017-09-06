package ru.biosoft.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.Entity;
import javax.swing.text.html.parser.ParserDelegator;

import org.json.JSONException;
import org.json.JSONObject;

import ru.biosoft.graphics.font.ColorFont;

public class ComplexTextView extends CompositeView
{
    public static final int TEXT_ALIGN_LEFT = CompositeView.X_LL;
    public static final int TEXT_ALIGN_CENTER = CompositeView.X_CC;
    public static final int TEXT_ALIGN_RIGHT = CompositeView.X_RR;

    public ComplexTextView(String text, ColorFont fontNormal, Map<String, ColorFont> fontRegistry, int textAlignment, Graphics graphics,
            int maxPixelWidth)
    {
        int stringWidth = graphics.getFontMetrics(fontNormal.getFont()).stringWidth(text);
        int maxStringlength = stringWidth <= maxPixelWidth+2 ? text.length() : Math.max(4, ( maxPixelWidth * text.length() / stringWidth - 2));
        parse(text, new Point(0, 0), LEFT | BASELINE, fontNormal, fontRegistry, textAlignment, maxStringlength, graphics);
    }

    public ComplexTextView(String text, ColorFont fontNormal, Map<String, ColorFont> fontRegistry, int textAlignment, int maxStringlength,
            Graphics graphics)
    {
        this(text, new Point(0, 0), LEFT | BASELINE, fontNormal, fontRegistry, textAlignment, maxStringlength, graphics);
    }

    public ComplexTextView(String text, Point pt, int alignment, ColorFont fontNormal, Map<String, ColorFont> fontRegistry,
            int textAlignment, int maxStringlength, Graphics graphics)
    {
        parse(text, pt, alignment, fontNormal, fontRegistry, textAlignment, maxStringlength, graphics);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Utility methods related with text and simple HTML processing 
    //
    
    public static final String BOLD_TAG = "b";
    public static final String ITALIC_TAG = "i";
    public static final String BR_TAG = "br";
    public static final String FONT_TAG = "font";
    public static final String COLOR_ATTR = "color";
    public static final String SIZE_ATTR = "size";
    public static final String SUB_TAG = "sub";
    public static final String SUP_TAG = "sup";
    public static final String ALPHA_TAG = "alpha";
    public static final String BETA_TAG = "beta";
    public static final String GAMMA_TAG = "gamma";
    public static final String DELTA_TAG = "delta";

    /**
     * Works exactly like Pattern.compile(String.valueOf(delimiter), Pattern.LITERAL).split(string, -1),
     * or like org.apache.commons.lang.StringUtils.splitPreserveAllTokens but faster.
     */
    public static String[] split(String string, char delimiter)
    {
        int n = 1;
        int i = 0;
        while(true)
        {
            i=string.indexOf(delimiter, i);
            if(i == -1) break;
            n++;
            i++;
        }
        if(n == 1) return new String[] {string};
        
        String[] result = new String[n];
        n = 0;
        i = 0;
        int start = 0;
        while(true)
        {
            i = string.indexOf(delimiter, start);
            if(i == -1) break;
            result[n++] = string.substring(start, i);
            start = i+1;
        }
        result[n] = string.substring(start);
        return result;
    }

    private static DTD dtd;
    static
    {
        try
        {
            new ParserDelegator();  // to initialize DTD
            dtd = DTD.getDTD("html32");
        }
        catch(IOException e)
        {}
    }
    
    /**
     * Converts character entity value into character.
     * Example input: gt
     * Example output: >
	 *
     * @param entity - entity to convert
     */
    public static String convertEntity(String entity)
    {
        if(entity.startsWith("#"))
        {
            try
            {
                int code = entity.startsWith("#x")?Integer.parseInt(entity.substring(2), 16):Integer.parseInt(entity.substring(1));
                return new String(new int[] {code}, 0, 1);
            }
            catch( NumberFormatException e )
            {
                return null;
            }
        }

        Entity entityValue = dtd.getEntity(entity);
        if( entityValue != null ) 
        	return entityValue.getString();
        
        return null;
    }
    
    protected static Map<String, String> getTagAttributes(String tag)
    {
        Map<String, String> result = new HashMap<>();
        String[] parts = split(tag, ' ');
        for( String part : parts )
        {
            if( part.contains("=") )
            {
                String[] obj = split(part, '=');
                String key = obj[0].toLowerCase();
                String value = obj[1].toLowerCase();
                if( value.matches("'.*'") || value.matches("\".*\"") )
                {
                    value = value.substring(1, value.length() - 1);
                }
                result.put(key, value);
            }
        }
        return result;
    }

    protected void processTag(String tag, Map<String, ColorFont> fontRegistry, List<ColorFont> fontStack, ParseState parseState,
            Graphics graphics)
    {
        if( tag.equals(BOLD_TAG) )
        {
            ColorFont font = getFont(fontRegistry, fontStack.get(fontStack.size() - 1), BOLD_TAG);
            fontStack.add(font);
        }
        else if( tag.equals(ITALIC_TAG) )
        {
            ColorFont font = getFont(fontRegistry, fontStack.get(fontStack.size() - 1), ITALIC_TAG);
            fontStack.add(font);
        }
        else if( tag.equals(BR_TAG) )
        {
            changeLine(parseState);
        }
        else if( tag.startsWith(FONT_TAG) )
        {
            ColorFont font = getFont(fontRegistry, fontStack.get(fontStack.size() - 1), tag);
            fontStack.add(font);
        }
        else if( tag.equals("/" + BOLD_TAG) || tag.equals("/" + ITALIC_TAG) || tag.startsWith("/" + FONT_TAG) )
        {
            if( fontStack.size() > 1 )
                fontStack.remove(fontStack.size() - 1);
        }
        else if( tag.equals(SUB_TAG) )
        {
            ColorFont font = getFont(fontRegistry, fontStack.get(fontStack.size() - 1), SUB_TAG);
            fontStack.add(font);
            parseState.verticalOffset += font.getFont().getSize() / 2;
        }
        else if( tag.equals("/" + SUB_TAG) )
        {
            if( fontStack.size() > 1 )
            {
                ColorFont font = fontStack.remove(fontStack.size() - 1);
                parseState.verticalOffset -= font.getFont().getSize() / 2;
            }
        }
        else if( tag.equals(SUP_TAG) )
        {
            ColorFont font = getFont(fontRegistry, fontStack.get(fontStack.size() - 1), SUB_TAG);
            fontStack.add(font);
            parseState.verticalOffset -= font.getFont().getSize() / 2;
        }
        else if( tag.equals("/" + SUP_TAG) )
        {
            if( fontStack.size() > 1 )
            {
                ColorFont font = fontStack.remove(fontStack.size() - 1);
                parseState.verticalOffset += font.getFont().getSize() / 2;
            }
        }
        else if( tag.equals(ALPHA_TAG + "/") )
        {
            parseState.buffer.append('\u03B1');
        }
        else if( tag.equals(BETA_TAG + "/") )
        {
            parseState.buffer.append('\u03B2');
        }
        else if( tag.equals(GAMMA_TAG + "/") )
        {
            parseState.buffer.append('\u03B3');
        }
        else if( tag.equals(DELTA_TAG + "/") )
        {
            parseState.buffer.append('\u03B4');
        }
        else
        {
            //unknown tags process like simple text
            processText(tag, fontStack, parseState, graphics);
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    public static ColorFont getFont(Map<String, ColorFont> fontRegistry, ColorFont previous, String tag)
    {
        int size = previous.getFont().getSize();
        int style = previous.getFont().getStyle();
        String fontName = previous.getFont().getName();
        Color color = previous.getColor();
        if( tag.equals(BOLD_TAG) )
        {
            style |= Font.BOLD;
        }
        else if( tag.equals(ITALIC_TAG) )
        {
            style |= Font.ITALIC;
        }
        else if( tag.equals(SUB_TAG) || tag.equals(SUP_TAG) )
        {
            size = size - 2;
        }
        else if( tag.startsWith(FONT_TAG) )
        {
            Map<String, String> attr = getTagAttributes(tag);
            if( attr.containsKey(SIZE_ATTR) )
            {
                try
                {
                    size = Integer.parseInt(attr.get(SIZE_ATTR).trim());
                }
                catch( NumberFormatException e )
                {
                }
            }
            if( attr.containsKey(COLOR_ATTR) )
            {
                String colorName = attr.get(COLOR_ATTR).trim().toUpperCase();
                try
                {
                    Field colorField = java.awt.Color.class.getDeclaredField(colorName);
                    if( null != colorField )
                    {
                        color = (Color)colorField.get(null);
                    }
                }
                catch( Throwable t )
                {
                }
            }
        }
        String key = size + ":" + style + ":" + color.getRGB();
        if( fontRegistry.containsKey(key) )
        {
            return fontRegistry.get(key);
        }
        else
        {
            ColorFont font = new ColorFont(fontName, style, size, color);
            fontRegistry.put(key, font);
            return font;
        }
    }

    protected void parse(String text, Point pt, int alignment, ColorFont fontNormal, Map<String, ColorFont> fontRegistry,
            int textAlignment, int maxStringLength, Graphics graphics)
    {
        boolean readTag = false;
        boolean readCharacterEntity = false;

        ParseState parseState = new ParseState(pt, maxStringLength, alignment, textAlignment);

        //stack for using font
        List<ColorFont> fontStack = new ArrayList<>();
        fontStack.add(fontNormal);

        //main cycle
        for( int i = 0; i < text.length(); i++ )
        {
            char currentSymbol = text.charAt(i);
            if( currentSymbol == '&' && !readCharacterEntity)
            {
                if( parseState.buffer.length() > 0 )
                {
                    processText(parseState.buffer.toString(), fontStack, parseState, graphics);
                }
                parseState.buffer = new StringBuffer();
                readCharacterEntity = true;
            }
            else if( currentSymbol == ';' && readCharacterEntity )
            {
                String entity = parseState.buffer.toString();
                String entityValue = convertEntity(entity);
                if(entityValue != null)
                {
                    parseState.buffer = new StringBuffer(entityValue);
                } 
                else
                {
                    parseState.buffer = new StringBuffer("&"+entity+";");
                }

                processText(parseState.buffer.toString(), fontStack, parseState, graphics);
                parseState.buffer = new StringBuffer();
                readCharacterEntity = false;
            }
            else if( currentSymbol == '<' && !readTag )
            {
                if( parseState.buffer.length() > 0 )
                {
                    processText(parseState.buffer.toString(), fontStack, parseState, graphics);
                }
                parseState.buffer = new StringBuffer();
                readTag = true;
            }
            else if( currentSymbol == '>' && readTag )
            {
                String tag = parseState.buffer.toString().toLowerCase();
                parseState.buffer = new StringBuffer();
                if( tag.length() > 0 )
                {
                    processTag(tag, fontRegistry, fontStack, parseState, graphics);
                }
                readTag = false;
            }
            else
            {
                parseState.buffer.append(currentSymbol);
            }
        }

        //process last token
        if( parseState.buffer.length() > 0 )
        {
            processText(parseState.buffer.toString(), fontStack, parseState, graphics);
        }

        add(parseState.currentLine, parseState.textAlignment | CompositeView.Y_BT, parseState.offset);
    }

    protected void changeLine(ParseState parseState)
    {
        add(parseState.currentLine, parseState.textAlignment | CompositeView.Y_BT, parseState.offset);
        parseState.offset.y = 0;
        parseState.offset.x = 0;
        parseState.currentLine = new CompositeView();
        parseState.currentLineSize = 0;
    }

    protected void processText(String text, List<ColorFont> fontStack, ParseState parseState, Graphics graphics)
    {
        String part = text;
        while( parseState.currentLineSize + part.length() > parseState.maxStringLength )
        {
            int pos = -1;
            int tmp = -1;
            while( ( tmp = indexOfSplitter(part, tmp + 1) ) != -1 )
            {
                if( parseState.currentLineSize + tmp > parseState.maxStringLength )
                {
                    break;
                }
                pos = tmp;
            }
            if( pos <= 0 )
            {
                if( parseState.currentLineSize == 0 )
                {
                    ColorFont font = fontStack.get(fontStack.size() - 1);
                    TextView textView = new TextView(part.substring(0, parseState.maxStringLength), new Point(0, 0), parseState.alignment,
                            font, graphics);
                    parseState.currentLine.add(textView, CompositeView.X_RL, new Point(0, parseState.verticalOffset));
                    part = part.substring(parseState.maxStringLength);
                }
            }
            else if( pos > 0 )
            {
                ColorFont font = fontStack.get(fontStack.size() - 1);
                TextView textView = new TextView(part.substring(0, pos), new Point(0, 0), parseState.alignment, font, graphics);
                parseState.currentLine.add(textView, CompositeView.X_RL, new Point(0, parseState.verticalOffset));
                part = part.substring(pos);
            }
            changeLine(parseState);

            //delete spaces before string
            pos = 0;
            while( pos < part.length() && part.charAt(pos) == ' ' )
            {
                pos++;
            }
            part = part.substring(pos);
        }

        if( part.length() > 0 )
        {
            ColorFont font = fontStack.get(fontStack.size() - 1);
            TextView textView = new TextView(part, new Point(0, 0), parseState.alignment, font, graphics);
            parseState.currentLine.add(textView, CompositeView.X_RL, new Point(0, parseState.verticalOffset));
            parseState.currentLineSize += part.length();
        }
    }

    private static final char[] splitters = new char[] {' ', '-'};
    protected int indexOfSplitter(String string, int startPos)
    {
        int result = -1;
        for( char splitter : splitters )
        {
            int pos = string.indexOf(splitter, startPos);
            if( pos != -1 )
            {
                if( result == -1 || pos < result )
                {
                    result = pos;
                }
            }
        }
        return result;
    }


    @Override
    public JSONObject toJSON() throws JSONException
    {
        JSONObject result = super.toJSON();
        return result;
    }

    protected static class ParseState
    {
        public StringBuffer buffer;
        public Point offset;
        public CompositeView currentLine;
        public int currentLineSize;
        public int maxStringLength;
        public int verticalOffset;
        public int alignment;
        public int textAlignment;

        public ParseState(Point pt, int maxStringLength, int alignment, int textAlignment)
        {
            this.buffer = new StringBuffer();
            this.offset = pt;
            this.currentLine = new CompositeView();
            this.currentLineSize = 0;
            this.maxStringLength = Math.max(maxStringLength, 1);
            this.verticalOffset = 0;
            this.alignment = alignment;
            this.textAlignment = textAlignment;
        }
    }
}
