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
import java.text.MessageFormat;

import jmemorize.core.LC;
import jmemorize.core.Lesson;
import jmemorize.core.LessonObserver;
import jmemorize.core.Localization;
import jmemorize.core.io.JMemorizeIO;
import jmemorize.gui.swing.Main;
import jmemorize.gui.swing.actions.AbstractSessionDisabledAction;
import jmemorize.gui.swing.dialogs.ErrorDialog;

/**
 * An action that saves the currently opened lesson.
 * 
 * @author djemili
 */
public class SaveLessonAction extends AbstractSessionDisabledAction 
    implements LessonObserver
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5616352017690792462L;
	private final JMemorizeIO jMemorizeIO;

	public SaveLessonAction( final JMemorizeIO jMemorizeIO)
    {
        this.jMemorizeIO = jMemorizeIO;
		setValues();
        
        Main.getInstance().addLessonObserver(this);
        updateEnablement();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(java.awt.event.ActionEvent e)
    {
        Main main = Main.getInstance();
        
        Lesson lesson = main.getLesson();

        try {
			jMemorizeIO.saveLesson(lesson);
        } catch (Exception exception) {
        	File file = jMemorizeIO.getFile();
            Object[] args = {file != null ? file.getName() : "?"};
            MessageFormat form = new MessageFormat(Localization.get(LC.ERROR_SAVE));
            String msg = form.format(args);
            Main.logThrowable(msg, exception);
           
            new ErrorDialog(main.getFrame(), msg, exception).setVisible(true);
        }
    }
    
    /* (non-Javadoc)
     * @see jmemorize.core.LessonObserver
     */
    public void lessonLoaded(Lesson newLesson)
    {
        updateEnablement();
    }

    /* (non-Javadoc)
     * @see jmemorize.core.LessonObserver
     */
    public void lessonModified(Lesson lesson)
    {
        updateEnablement();
    }

    /* (non-Javadoc)
     * @see jmemorize.core.LessonObserver
     */
    public void lessonSaved(Lesson lesson)
    {
        updateEnablement();
    }

    /* (non-Javadoc)
     * @see jmemorize.core.LessonObserver
     */
    public void lessonClosed(Lesson lesson)
    {
        updateEnablement();
    }

    /* (non-Javadoc)
     * @see jmemorize.gui.swing.actions.AbstractSessionDisabledAction
     */
    protected void updateEnablement()
    {
        Main main = Main.getInstance();
        Lesson lesson = main.getLesson();
        
        setEnabled(!main.isSessionRunning() && lesson.canSave());
    }

    private void setValues()
    {
        setName(Localization.get("MainFrame.SAVE")); //$NON-NLS-1$
        setDescription(Localization.get("MainFrame.SAVE_DESC")); //$NON-NLS-1$
        setIcon("/resource/icons/file_save.gif"); //$NON-NLS-1$
        setAccelerator(KeyEvent.VK_S, SHORTCUT_KEY);
        setMnemonic(1);
    }
}
