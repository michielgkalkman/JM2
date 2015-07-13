/*
 * jMemorize - Learning made easy (and fun) - A Leitner flashcards tool
 * Copyright(C) 2004-2006 Riad Djemili
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
package jmemorize.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

/**
 * A sime file filter for file choosers.
 * 
 * @author djemili
 */
public class ExtensionFileFilter extends FileFilter
{
    private List<String> m_extensions = new ArrayList<String>();
    private String       m_description;

    public ExtensionFileFilter(String extensions, String description)
    {
        String[] exts = extensions.split(" ");
        for (String ext : exts)
            m_extensions.add(ext);
        
        m_description = description;
    }

    /*
     * @see javax.swing.filechooser.FileFilter
     */
    public boolean accept(File f)
    {
        if (f.isDirectory())
            return true;
        
        for (String extension : m_extensions)
        {
             if (f.getName().endsWith(extension))
                 return true;
        }
        
        return false;
    }

    /*
     * @see javax.swing.filechooser.FileFilter
     */
    public String getDescription()
    {
        return m_description;
    }
    
    public String getExtension()
    {
        return m_extensions.get(0);
    }
}
