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
package jmemorize.gui.swing.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import jmemorize.core.Card;
import jmemorize.core.Category;
import jmemorize.core.CategoryObserver;
import jmemorize.core.Events;
import jmemorize.core.LC;
import jmemorize.core.Localization;
import jmemorize.gui.swing.SelectionProvider;

/**
 * A combobox that shows categories. The categories are indented in a way that
 * shows their hierarchy.
 * 
 * @author djemili
 */
public class CategoryComboBox extends JComboBox 
    implements CategoryObserver, SelectionProvider
{
    private class CatergoryRenderer extends BasicComboBoxRenderer
    {
        /* (non-Javadoc)
         * @see javax.swing.plaf.basic.BasicComboBoxRenderer
         */
        public Component getListCellRendererComponent(JList list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus)
        {
            Category cat = (Category)value;
            
            JLabel leftLabel = (JLabel)super.getListCellRendererComponent(list, 
                cat.getName(), index, isSelected, cellHasFocus);
            
            leftLabel.setIcon(FOLDER_ICON);
            
            // show items in combo list indented.
            int leftSpace = index >= 0 ? 20 * cat.getDepth() : 0;
            leftLabel.setBorder(new EmptyBorder(2, leftSpace, 2, 2));
            
            if (index < 0)
                leftLabel.setText(cat.getPath());
            
            JLabel rightLabel = new JLabel();
            rightLabel.setText(String.format("%d %s    ",
                cat.getCardCount(), Localization.get(LC.STATUS_CARDS)));
            
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(leftLabel, BorderLayout.CENTER);
            panel.add(rightLabel, BorderLayout.EAST);
            
            if (index >= 0)
            {
                panel.setBackground(leftLabel.getBackground());
                panel.setForeground(leftLabel.getForeground());
            }

            return panel;
        }
    }
    
    private final ImageIcon     FOLDER_ICON = new ImageIcon(getClass().
        getResource("/resource/icons/folder.gif")); //$NON-NLS-1$
    
    private Category                m_rootCategory;
    private List<SelectionObserver> m_observers = new LinkedList<SelectionObserver>();
    
    public CategoryComboBox()
    {
        setRenderer(new CatergoryRenderer());
        setMaximumRowCount(12);
    }
    
    public void setRootCategory(Category category)
    {
        if (m_rootCategory != null)
        {
            m_rootCategory.removeObserver(this);
        }
        m_rootCategory = category;
        m_rootCategory.addObserver(this);
        
        updateModel();
    }
    
    public Category getRootCategory()
    {
        return m_rootCategory;
    }
    
    public void setSelectedCategory(Category category)
    {
        setSelectedItem(category);
    }
    
    public Category getSelectedCategory()
    {
        return (Category)getModel().getSelectedItem();
    }
    
    /* (non-Javadoc)
     * @see jmemorize.core.CategoryObserver
     */
    public void onCategoryEvent(int type, Category category)
    {
        updateModel();
    }

    /* (non-Javadoc)
     * @see jmemorize.core.CategoryObserver
     */
    public void onCardEvent(int type, Card card, Category category, int deck)
    {
        if (!m_rootCategory.contains(category))
            return;
        
        switch (type)
        {
        case Events.ADDED_EVENT:
        case Events.REMOVED_EVENT:
        case Events.MOVED_EVENT:
            updateModel();
        }
    }
    
    public void addSelectionObserver(SelectionObserver observer)
    {
        m_observers.add(observer);
    }
    
    public void removeSelectionObserver(SelectionObserver observer)
    {
        m_observers.remove(observer);
    }

    public Category getCategory()
    {
        return getSelectedCategory();
    }

    public JComponent getDefaultFocusOwner()
    {
        return this;
    }

    public JFrame getFrame()
    {
        return null;
    }

    public List<Card> getRelatedCards()
    {
        return null;
    }

    public List<Card> getSelectedCards()
    {
        return null;
    }

    public List<Category> getSelectedCategories()
    {
        return null;
    }

    private void updateModel()
    {
        Object selected = getModel().getSelectedItem();
        List<Category> categoryList = m_rootCategory.getSubtreeList();
        DefaultComboBoxModel model = new DefaultComboBoxModel(categoryList.toArray());
        
        // if former selected object still there, select it again
        if (categoryList.contains(selected))
        {
            model.setSelectedItem(selected);
        }
    
        setModel(model);
    }
}
