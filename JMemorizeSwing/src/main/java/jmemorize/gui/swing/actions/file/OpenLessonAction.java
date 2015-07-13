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

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import jmemorize.core.Lesson;
import jmemorize.core.Localization;
import jmemorize.core.io.JMemorizeIO;
import jmemorize.gui.swing.actions.AbstractSessionDisabledAction;
import jmemorize.gui.swing.frames.MainFrame;

/**
 * An action that opens another lesson. Before dismissing the current lesson,
 * the user is asked for confirmation.
 * 
 * @see MainFrame#confirmCloseLesson()
 * 
 * @author djemili
 */
public class OpenLessonAction extends AbstractSessionDisabledAction
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3333714808183547996L;
	private final JMemorizeIO jMemorizeIO;
	private final JMemorizeUI jMemorizeUI;

	public OpenLessonAction( final JMemorizeIO jMemorizeIO, final JMemorizeUI jMemorizeUI)
    {
        this.jMemorizeIO = jMemorizeIO;
		this.jMemorizeUI = jMemorizeUI;
		setValues();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(java.awt.event.ActionEvent e)
    {
    	final File file = jMemorizeUI.determineLessonFile();
    	try {
			Lesson lesson = new Lesson(true);
			jMemorizeIO.load(file, lesson);
			jMemorizeUI.show(lesson);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
    
    private void setValues()
    {
        setName(Localization.get("MainFrame.OPEN")); //$NON-NLS-1$
        setDescription(Localization.get("MainFrame.OPEN_DESC")); //$NON-NLS-1$
        setIcon("/resource/icons/file_open.gif"); //$NON-NLS-1$
        setAccelerator(KeyEvent.VK_O, SHORTCUT_KEY);
        setMnemonic(1);
    }
}
