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
package jmemorize.gui.swing.actions.file;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import jmemorize.core.LC;
import jmemorize.core.Lesson;
import jmemorize.core.Localization;
import jmemorize.core.io.JMemorizeIO;
import jmemorize.gui.swing.frames.MainFrame;

public class ImportJMLAction extends AbstractImportAction
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -874333212294988208L;
	private final JMemorizeIO jMemorizeIO;

	public ImportJMLAction( JMemorizeIO jMemorizeIO)
    {
    	this.jMemorizeIO = jMemorizeIO;
		setValues();
    }
    
    /* (non-Javadoc)
     * @see jmemorize.gui.swing.actions.file.AbstractImportAction
     */
    protected void doImport(File file, Lesson lesson) throws IOException
    {
        try
        {
        	jMemorizeIO.loadFromXMLFile(file, lesson);
        } 
        catch (Exception e)
        {
            throw new IOException(e.getLocalizedMessage());
        }
    }

    protected FileFilter getFileFilter()
    {
        return MainFrame.FILE_FILTER;
    }
    
    private void setValues()
    {
        setName(Localization.get(LC.FILE_FILTER_DESC));
        setMnemonic(1);
        setIcon("/resource/icons/file_saveas.gif"); //$NON-NLS-1$
    }
}
