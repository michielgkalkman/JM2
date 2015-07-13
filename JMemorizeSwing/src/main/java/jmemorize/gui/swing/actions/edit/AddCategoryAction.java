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
package jmemorize.gui.swing.actions.edit;

import javax.swing.JOptionPane;

import jmemorize.core.Category;
import jmemorize.core.LC;
import jmemorize.core.Localization;
import jmemorize.gui.swing.SelectionProvider;
import jmemorize.gui.swing.actions.AbstractSessionDisabledAction;
import jmemorize.gui.swing.widgets.CategoryComboBox;

/**
 * An action for adding new categories.
 * 
 * @author djemili
 */
public class AddCategoryAction extends AbstractSessionDisabledAction
{
    private SelectionProvider m_provider;
    
    public AddCategoryAction(SelectionProvider provider)
    {
        m_provider = provider;
        setValues();        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener
     */
    public void actionPerformed(java.awt.event.ActionEvent e)
    {
        String name = JOptionPane.showInputDialog(
            m_provider.getFrame(),
            Localization.get(LC.ACTION_ADD_CATEGORY_INPUT),
            Localization.get(LC.ACTION_ADD_CATEGORY),
            JOptionPane.INFORMATION_MESSAGE
            );

        if (name != null && name.trim().length() > 0)
        {
            Category parentCategory = m_provider.getCategory();
            Category newCategory = new Category(name.trim());
            
            parentCategory.addCategoryChild(newCategory);
            
            if (m_provider instanceof CategoryComboBox)
                ((CategoryComboBox)m_provider).setSelectedCategory(newCategory);
        }
    }
    
    private void setValues()
    {
        setName(Localization.get(LC.ACTION_ADD_CATEGORY));
        setDescription(Localization.get(LC.ACTION_ADD_CATEGORY_DESC));
        setIcon("/resource/icons/category_add.gif"); //$NON-NLS-1$
    }
}