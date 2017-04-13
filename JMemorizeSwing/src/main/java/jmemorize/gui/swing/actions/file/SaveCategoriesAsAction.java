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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;

import jmemorize.core.LC;
import jmemorize.core.Lesson;
import jmemorize.core.Localization;
import jmemorize.core.io.JMemorizeIO;
import jmemorize.gui.swing.Main;
import jmemorize.gui.swing.SelectionProvider;
import jmemorize.gui.swing.actions.AbstractAction2;
import jmemorize.gui.swing.dialogs.ErrorDialog;

/**
 * An action that opens up a file chooser and saves the lesson at that location.
 * 
 * @author djemili
 */
public class SaveCategoriesAsAction extends AbstractAction2
{
    private final SelectionProvider m_selectionProvider;
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -1009010078248410083L;

	private JMemorizeIO jMemorizeIO;

	public SaveCategoriesAsAction(final SelectionProvider selectionProvider, final JMemorizeIO jMemorizeIO)
    {
        m_selectionProvider = selectionProvider;
		this.jMemorizeIO = jMemorizeIO;
        setValues();
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(java.awt.event.ActionEvent e)
    {
        final Lesson lesson = Lesson.cloneLesson( m_selectionProvider.getSelectedCategories());

        try {
			jMemorizeIO.saveLesson(lesson);
        } catch (Exception exception) {
        	File file = jMemorizeIO.getFile();
            Object[] args = {file != null ? file.getName() : "?"};
            MessageFormat form = new MessageFormat(Localization.get(LC.ERROR_SAVE));
            String msg = form.format(args);
            Main.logThrowable(msg, exception);
           
            Main main = Main.getInstance();
            new ErrorDialog(main.getFrame(), msg, exception).setVisible(true);
        }
    }

	private void setValues()
    {
        setName(Localization.get("MainFrame.SAVE_AS")); //$NON-NLS-1$
        setDescription(Localization.get("MainFrame.SAVE_AS_DESC")); //$NON-NLS-1$
        setIcon("/resource/icons/file_saveas.gif"); //$NON-NLS-1$
        setAccelerator(KeyEvent.VK_S, SHORTCUT_KEY + InputEvent.SHIFT_MASK);
        setMnemonic(2);
    }
}