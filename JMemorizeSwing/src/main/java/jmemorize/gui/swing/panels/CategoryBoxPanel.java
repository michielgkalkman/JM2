package jmemorize.gui.swing.panels;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JToolBar;

import jmemorize.gui.swing.actions.edit.AddCategoryAction;
import jmemorize.gui.swing.widgets.CategoryComboBox;

public class CategoryBoxPanel extends JToolBar
{
    CategoryComboBox m_categoryBox = new CategoryComboBox();
    
    public CategoryBoxPanel(boolean allowCreateCategory)
    {
        setFloatable(false);
        initComponents(allowCreateCategory);
    }
    
    public CategoryComboBox getComboBox()
    {
        return m_categoryBox;
    }
    
    private void initComponents(boolean allowCreateCategory) 
    {
        JButton button = new JButton(new AddCategoryAction(m_categoryBox));
        button.setText(""); //$NON-NLS-1$
        
        setLayout(new BorderLayout());
        add(m_categoryBox, BorderLayout.CENTER);
        
        if (allowCreateCategory)
            add(button, BorderLayout.EAST);
    }
}
