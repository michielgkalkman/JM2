/*
 * jMemorize - Learning made easy (and fun) - A Leitner flashcards tool
 * Copyright(C) 2004-2008 Riad Djemili and contributors
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package jmemorize.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This class handles encoding/decoding and displaying formatted/
 * unformatted text. The text is immutable.
 * 
 * Styled document 
 * Unformatted String <--> FormattedText class 
 * Encoding
 * 
 * @author djemili
 */
public class FormattedText implements Cloneable
{
    public class ParseException extends Exception
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = -6587121432754011857L;

		public ParseException(final String message)
        {
            super(message);
        }
    }
    
    // TODO add trimming at end
    // TODO check if reg exp that breaks at new lines is suffice
    // TODO replace direct StyledDocument reference by eclipse-style IAdapter pattern
    // TODO optimze the reg expr
    
    /**
     * An empty formatted text (immutable).
     */
    public static final FormattedText EMPTY = FormattedText.unformatted("");
    
    private static final String  TAGS = "<(/?(b|i|u|sub|sup)?)>";
    private static final Pattern TEXT_PATTERN = Pattern.compile(
        "(.*?)<(/?(b|i|u|sub|sup)?)>", Pattern.DOTALL);
    
    private static final String CONTENT_ELEMENT_NAME = "content";
    
    private String                     m_formattedText;
    private String                     m_unformattedText;

    private static Map<String, Object> stylesMap = new HashMap<String, Object>();
    
    static
    {
        setupStylesMap();
    }
    
    public static FormattedText formatted(final String formatted)
    {
        final FormattedText text = new FormattedText();
        text.m_formattedText = formatted;
        text.m_unformattedText = unescape(formatted.replaceAll(TAGS, "").replaceAll("<img .*?/>", ""));
        
        return text;
    }
    
    public static FormattedText formatted(final StyledDocument document)
    {
        final Element root = document.getDefaultRootElement();
        final String fText = removeRedundantTags(getFormattedText(
            root, 0, document.getLength()));

        return FormattedText.formatted(fText);
    }
    
    public static FormattedText formatted(final StyledDocument document, 
        final int start, final int end)
    {
        final Element root = document.getDefaultRootElement();
        final String fText = removeRedundantTags(getFormattedText(
            root, start, end));

        return FormattedText.formatted(fText);
    }    
    
    public static FormattedText unformatted(final String unformatted)
    {
        final FormattedText text = new FormattedText();
        text.m_formattedText = unformatted;
        text.m_unformattedText = unformatted;
        
        return text;
    }
    
//    public static void insertImage(Document doc, ImageIcon icon, int offset) 
//        throws BadLocationException
//    {
//        int iconWidth = icon.getIconWidth();
//        int iconHeight = icon.getIconHeight();
//        Dimension dim = new Dimension(iconWidth, iconHeight);
//        
//        SimpleAttributeSet sa = new SimpleAttributeSet();
//        
//        JLabel label = new JLabel(icon);
//        label.setMinimumSize(dim);
//        label.setPreferredSize(dim);
//        label.setMaximumSize(dim);
//        label.setSize(dim);
//        
//        StyleConstants.setComponent(sa, label);
//        doc.insertString(offset, " ", sa);
//    }
    
    public String getFormatted()
    {
        return m_formattedText;
    }

    public String getUnformatted()
    {
        return m_unformattedText;
    }
    
    // TODO rename to toStyledDocument
    public StyledDocument getDocument()
    {
        final DefaultStyledDocument doc = new DefaultStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength() + 1, // HACK 
            SimpleAttributeSet.EMPTY, true);
        
        try
        {
            decode(doc, m_formattedText, 0);
        } 
        catch (final Exception e)
        {
            Model.logThrowable("Error formatting card", e);
        }
        
        return doc;
    }
    
    public void insertIntoDocument(final StyledDocument doc, final int offset)
    {
        try
        {
            decode(doc, m_formattedText, offset);
        } 
        catch (final Exception e)
        {
            Model.logThrowable("Error formatting card", e);
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString()
    {
        return m_unformattedText;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
	public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) 
        {
            assert false;
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(final Object obj)
    {
        if (obj instanceof FormattedText)
        {
            final FormattedText other = (FormattedText)obj;
            return m_formattedText.equals(other.m_formattedText);
        }
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode()
    {
        return m_formattedText.hashCode();
    }
    
    private static String removeRedundantTags(String formattedText)
    {
        /*
         * StyledDocument merges styles in certain situations. To avoid that
         * this results in getting a different encoding after decoding to a
         * StyledDocument and back to an encoding again, we remove redundant
         * tags by ourself.
         */
        for (final String key : stylesMap.keySet())
        {
            final StringBuffer sb = new StringBuffer();
            sb.append("</").append(key).append("><").append(key).append(">");
            
            formattedText = formattedText.replaceAll(sb.toString(), "");
        }
        
        return formattedText;
    }

    private static String getFormattedText(final Element e, final int startSelection, 
        final int endSelection)
    {
        final StringBuffer sb = new StringBuffer();
        if (e.getName().equals(CONTENT_ELEMENT_NAME))
        {
            final Document doc = e.getDocument();
            
            int start = e.getStartOffset();
            int end = Math.min(e.getEndOffset(), doc.getLength());
            
            if (start > endSelection || end < startSelection)
                return sb.toString();
            
            try
            {
                start = Math.max(start, startSelection);
                end = Math.min(end, endSelection);
                
                final String text = doc.getText(start, end - start);
                sb.append(escape(text));
            } 
            catch (final BadLocationException e1)
            {
                e1.printStackTrace();
                Model.logThrowable("Error formatting text", e1);
            }
        } 
        else
        {
            for (int i = 0; i < e.getElementCount(); i++)
            {
                sb.append(getFormattedText(e.getElement(i), 
                    startSelection, endSelection));
            }
        }
        
        for (final String name : stylesMap.keySet())
        {
            final Object styleId = stylesMap.get(name);
            
            if (hasStyle(e.getAttributes(), styleId))
            {
                sb.insert(0, "<"+name+">");
                sb.append("</"+name+">");
            }
        }
        
        return sb.toString();
    }
    
    private static String escape(final String text)
    {
        return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
    
    private static String unescape(final String text)
    {
        return text.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
    }

    private static void setupStylesMap()
    {
        stylesMap.put("b", StyleConstants.Bold);
        stylesMap.put("i", StyleConstants.Italic);
        stylesMap.put("u", StyleConstants.Underline);
        stylesMap.put("sub", StyleConstants.Subscript);
        stylesMap.put("sup", StyleConstants.Superscript);
    }
    
    private void decode(final StyledDocument doc, final String text, int offset) 
        throws BadLocationException
    {
        final StringBuffer sb = new StringBuffer(text);
        
        final Matcher m = TEXT_PATTERN.matcher(sb);
        int end = 0;
        
        final SimpleAttributeSet attr = new SimpleAttributeSet();
        while (m.find())
        {
            final String pretext = m.group(1);
            String tag = m.group(2);
            
            final String unescapedPretext = unescape(pretext);
            doc.insertString(offset, unescapedPretext, attr);
            offset += unescapedPretext.length();
            
            boolean style = true;
            if (tag.startsWith("/"))
            {
                tag = tag.substring(1);
                style = false;
            }
            
            final Object styleId = stylesMap.get(tag); 
            attr.addAttribute(styleId, Boolean.valueOf(style));
            
            end = m.end();
        }
        
        final String restText = unescape(sb.substring(end));
        doc.insertString(offset, restText, new SimpleAttributeSet());
    }
    
    private static boolean hasStyle(final AttributeSet attr, final Object styleId)
    {
        final Boolean style = (Boolean)attr.getAttribute(styleId);
        return style != null && style.booleanValue();
    }
}
