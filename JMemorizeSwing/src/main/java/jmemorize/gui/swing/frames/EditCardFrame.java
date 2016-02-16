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
package jmemorize.gui.swing.frames;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.ButtonBarFactory;

import jmemorize.core.Card;
import jmemorize.core.CardSide;
import jmemorize.core.Category;
import jmemorize.core.CategoryObserver;
import jmemorize.core.FormattedText;
import jmemorize.core.LC;
import jmemorize.core.Localization;
import jmemorize.core.Settings;
import jmemorize.core.media.MediaRepository;
import jmemorize.gui.swing.Main;
import jmemorize.gui.swing.SelectionProvider;
import jmemorize.gui.swing.actions.AbstractAction2;
import jmemorize.gui.swing.actions.edit.AddCardAction;
import jmemorize.gui.swing.actions.edit.RemoveAction;
import jmemorize.gui.swing.actions.edit.ResetCardAction;
import jmemorize.gui.swing.panels.CardHeaderPanel;
import jmemorize.gui.swing.panels.CardPanel;
import jmemorize.gui.swing.panels.TwoSidesCardPanel;
import jmemorize.gui.swing.widgets.CategoryComboBox;
import jmemorize.util.EscapableFrame;

/**
 * The window that is used to edit cards. Note this is a singleton class. The
 * same window will be reused for all editting.
 * 
 * @author djemili
 */
public class EditCardFrame extends EscapableFrame implements CategoryObserver, SelectionProvider {
	private class NextCardAction extends AbstractAction2 {
		public NextCardAction() {
			setName(Localization.get(LC.NEXT_CARD));
			setDescription(Localization.get(LC.NEXT_CARD_DESC));
			setIcon("/resource/icons/card_next.gif"); //$NON-NLS-1$
			setMnemonic(1);
		}

		@Override
		public void actionPerformed(final java.awt.event.ActionEvent e) {
			try {
				if (confirmCardSides())
					showNext();
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private class PreviousCardAction extends AbstractAction2 {
		public PreviousCardAction() {
			setName(Localization.get(LC.PREV_CARD));
			setDescription(Localization.get(LC.PREV_CARD_DESC));
			setIcon("/resource/icons/card_prev.gif"); //$NON-NLS-1$
			setMnemonic(1);
		}

		@Override
		public void actionPerformed(final java.awt.event.ActionEvent e) {
			try {
				if (confirmCardSides())
					showPrevious();
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private static final int MAX_TITLE_LENGTH = 80;
	private static final String FRAME_ID = "editcard"; //$NON-NLS-1$

	private final List<SelectionObserver> m_selectionObservers = new ArrayList<SelectionObserver>();

	private final Action m_nextCardAction = new NextCardAction();
	private final Action m_previousCardAction = new PreviousCardAction();

	private Card m_currentCard;
	private int m_currentCardIndex;
	private ArrayList<Card> m_cards;
	private Category m_category;

	// swing elements
	private final JButton m_applyButton = new JButton(Localization.get(LC.APPLY));

	private final CardHeaderPanel m_headerPanel = new CardHeaderPanel();
	private final TwoSidesCardPanel m_cardPanel = new TwoSidesCardPanel(true);

	private static EditCardFrame m_instance;

	/**
	 * @return The singleton instance.
	 */
	public static EditCardFrame getInstance() {
		if (m_instance == null) {
			m_instance = new EditCardFrame();
		}

		return m_instance;
	}

	/**
	 * Shows the Edit Card Frame and allows user to edit the card card.
	 * 
	 * @param card
	 *            The card that is to be shown and editted.
	 * @throws IOException
	 */
	public void showCard(final Card card) throws IOException {
		final List<Card> cards = new ArrayList<Card>(1);
		cards.add(card);
		showCard(card, cards, card.getCategory());
	}

	/**
	 * Shows the Edit Card Frame and allows user to edit the card card.
	 * 
	 * @param card
	 *            the card that is to be shown and editted.
	 * 
	 * @param cards
	 *            the cards that belong to the context of the card that is to be
	 *            edited. These cards are used to allow browsing to next and
	 *            previous card. Therefore the card given on the former
	 *            parameter is usually also part of this list. The usual mode is
	 *            to show the currently selected card and to give all other
	 *            cards that are part of the same card table/learn history etc.
	 *            as additional cards.
	 * 
	 * @param category
	 *            The category that includes all cards from former parameters.
	 * @throws IOException
	 */
	public void showCard(final Card card, final List<Card> cards, final Category category) throws IOException {
		showCard(card, cards, category, null, 0, true); // HACK
	}

	public void showCard(final Card card, final List<Card> cards, final Category category, final String searchText,
			final int side, final boolean ignoreCase) throws IOException {
		if (isVisible() && !confirmCardSides())
			return;

		m_currentCard = card;
		m_currentCardIndex = cards.indexOf(card);
		m_cards = new ArrayList<Card>(cards);

		if (m_category != null) {
			m_category.removeObserver(this);
		}
		m_category = category;
		if (m_category != null) {
			category.addObserver(this);
		}

		updatePanel();
		setVisible(true);
	}

	/**
	 * @return True if window was closed. False if this was prevented by user
	 *         option.
	 */
	@Override
	public boolean close() {
		try {
			if (confirmCardSides()) {
				hideFrame();
				return true;
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.core.CategoryObserver
	 */
	@Override
	public void onCategoryEvent(final int type, final Category category) {
		if (type == REMOVED_EVENT) {
			// if current card was in a deleted category branch
			if (category.contains(m_currentCard.getCategory())) {
				hideFrame();
			}

			// delete all cards that are part of a deleted category branch
			for (final Card card : m_cards) {
				if (category.contains(card.getCategory())) {
					m_cards.remove(card);
				}
			}

			m_currentCardIndex = m_cards.indexOf(m_currentCard);
			updateActions();
		} else if (type == EDITED_EVENT) {
			updateCardHeader();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.core.CategoryObserver
	 */
	@Override
	public void onCardEvent(final int type, final Card card, final Category category, final int deck) {
		if (type == DECK_EVENT && m_currentCard == card) {
			updateCardHeader();
		}

		if (type == REMOVED_EVENT) {
			if (m_currentCard == card) {
				if (hasNext()) {
					showNext();
				} else if (hasPrevious()) {
					showPrevious();
				} else {
					hideFrame();
				}
			}

			if (m_cards.remove(card)) // is this card is relevant
			{
				// we need to update index because cards changed
				m_currentCardIndex = m_cards.indexOf(m_currentCard);
				updateActions();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.SelectionProvider
	 */
	@Override
	public void addSelectionObserver(final SelectionObserver observer) {
		m_selectionObservers.add(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.SelectionProvider
	 */
	@Override
	public void removeSelectionObserver(final SelectionObserver observer) {
		m_selectionObservers.remove(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.SelectionProvider
	 */
	@Override
	public Category getCategory() {
		return m_currentCard.getCategory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.SelectionProvider
	 */
	@Override
	public JComponent getDefaultFocusOwner() {
		return null; // HACK
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.SelectionProvider
	 */
	@Override
	public JFrame getFrame() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.SelectionProvider
	 */
	@Override
	public List<Card> getRelatedCards() {
		return m_cards;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.SelectionProvider
	 */
	@Override
	public List<Card> getSelectedCards() {
		final ArrayList<Card> list = new ArrayList<Card>(1);
		list.add(m_currentCard);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.SelectionProvider
	 */
	@Override
	public List<Category> getSelectedCategories() {
		return null;
	}

	private void hideFrame() {
		Settings.storeFrameState(this, FRAME_ID);
		setVisible(false);
	}

	/**
	 * If the content of the text panes differ from the currently saved card
	 * entries, this will bring up a dialog that asks if the user wants to save
	 * the changes. If yes is selected the card sides are saved.
	 * 
	 * This should be called everytime there is the chance of losing card
	 * informations.
	 * 
	 * @return True if operation wasnt aborted by user.
	 * @throws IOException
	 */
	private boolean confirmCardSides() throws IOException {
		if (isChanged()) {
			final int n = JOptionPane.showConfirmDialog(this, Localization.get("EditCard.MODIFIED_WARN"), //$NON-NLS-1$
					Localization.get("EditCard.MODIFIED_WARN_TITLE"), //$NON-NLS-1$
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

			if (n == JOptionPane.CANCEL_OPTION) {
				return false;
			}

			if (n == JOptionPane.YES_OPTION) {
				return saveCard();
			}
		}

		// if no changes or NO chosen
		return true;
	}

	/**
	 * Creates new form EditCardFrame
	 */
	private EditCardFrame() {
		initComponents();
		addChangeObservers();
		Settings.loadFrameState(this, FRAME_ID);
	}

	private void addChangeObservers() {
		m_cardPanel.addObserver(new CardPanel.CardPanelObserver() {
			@Override
			public void onTextChanged() {
				updateApplyButton();
			}

			@Override
			public void onImageChanged() {
				updateApplyButton();
			}
		});

		m_cardPanel.getCategoryComboBox().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				updateApplyButton();
			}
		});
	}

	private void updateApplyButton() {
		m_applyButton.setEnabled(isChanged());
	}

	private boolean isChanged() {
		final boolean categoryChanged = !m_currentCard.getCategory()
				.equals(m_cardPanel.getCategoryComboBox().getSelectedCategory());

		if (categoryChanged)
			return true;

		final CardSide frontSide = m_currentCard.getFrontSide();
		final CardSide backSide = m_currentCard.getBackSide();

		final boolean textChanged = !m_cardPanel.getFrontText().equals(frontSide.getText())
				|| !m_cardPanel.getBackText().equals(backSide.getText());

		if (textChanged)
			return true;

		if (!MediaRepository.equals(m_cardPanel.getFrontImages(), frontSide.getMedia()))
			return true;

		if (!MediaRepository.equals(m_cardPanel.getBackImages(), backSide.getMedia()))
			return true;

		return false;
	}

	private void updatePanel() {
		updateTitle();

		final CardSide frontSide = m_currentCard.getFrontSide();
		final CardSide backSide = m_currentCard.getBackSide();

		// set sides
		m_cardPanel.setTextSides(frontSide.getText(), backSide.getText());
		m_cardPanel.setImages(frontSide.getMedia(), backSide.getMedia());

		highlightSearchText();
		updateActions();
		updateCardHeader();

		final Category rootCategory = Main.getInstance().getLesson().getRootCategory();
		final CategoryComboBox categoryComboBox = m_cardPanel.getCategoryComboBox();
		categoryComboBox.setRootCategory(rootCategory);
		categoryComboBox.setSelectedCategory(m_currentCard.getCategory());

		updateApplyButton();
	}

	/**
	 * Update the title of this frame.
	 */
	private void updateTitle() {
		// set title
		String title = m_currentCard.getFrontSide().getText().getUnformatted();
		title = title.replace('\n', ' ');
		if (title.length() > MAX_TITLE_LENGTH) {
			title = title.substring(0, MAX_TITLE_LENGTH) + "..."; //$NON-NLS-1$
		}
		setTitle(title);

		// Date dateExpired = m_currentCard.getDateExpired();
		// ImageIcon icon =
		// CardStatusIcons.getInstance().getCardIcon(dateExpired);
		// setIconImage(icon.getImage());
	}

	/**
	 * Updates the actions of this EditCardFrame i.e. enabling/disabling certain
	 * buttons.
	 */
	private void updateActions() {
		if (m_cards == null) {
			m_nextCardAction.setEnabled(false);
			m_previousCardAction.setEnabled(false);
		} else {
			m_previousCardAction.setEnabled(hasPrevious());
			m_nextCardAction.setEnabled(hasNext());
		}
	}

	/**
	 * @return <code>true</code> if there is another card left after this one.
	 */
	private boolean hasNext() {
		return m_currentCardIndex < m_cards.size() - 1;
	}

	/**
	 * @return <code>true</code> if there is a another card before this one.
	 */
	private boolean hasPrevious() {
		return m_currentCardIndex > 0;
	}

	/**
	 * Show the next card of the card list of this EditCardFrame.
	 */
	private void showNext() {
		m_currentCard = m_cards.get(++m_currentCardIndex);
		updatePanel();
	}

	/**
	 * Show the previous card of the card list of this EditCardFrame.
	 */
	private void showPrevious() {
		m_currentCard = m_cards.get(--m_currentCardIndex);
		updatePanel();
	}

	private boolean saveCard() throws IOException {
		if (isChanged()) {
			if (m_cardPanel.isValidCard()) {
				final FormattedText frontText = m_cardPanel.getFrontText();
				final FormattedText backText = m_cardPanel.getBackText();

				final MediaRepository repo = MediaRepository.getInstance();

				final List<String> frontIDs = repo.addImages(m_cardPanel.getFrontImages());
				final List<String> backIDs = repo.addImages(m_cardPanel.getBackImages());

				m_currentCard.setSides(frontText, backText);
				m_currentCard.getFrontSide().setMedia(frontIDs);
				m_currentCard.getBackSide().setMedia(backIDs);

				final CategoryComboBox categoryComboBox = m_cardPanel.getCategoryComboBox();
				final Category newCategory = categoryComboBox.getSelectedCategory();
				if (!newCategory.equals(m_currentCard.getCategory())) {
					Category.moveCard(m_currentCard, newCategory);
				}

				updateTitle();
				updateCardHeader();
				updateApplyButton();

				return true;
			} else {
				JOptionPane.showMessageDialog(this, Localization.get(LC.EMPTY_SIDES_ALERT),
						Localization.get(LC.EMPTY_SIDES_ALERT_TITLE), JOptionPane.ERROR_MESSAGE);

				return false;
			}
		} else {
			return true;
		}
	}

	private void updateCardHeader() {
		m_headerPanel.setCard(m_currentCard);
	}

	private void initComponents() {
		getContentPane().add(buildToolBar(), BorderLayout.NORTH);
		getContentPane().add(buildHeaderPanel(), BorderLayout.CENTER);
		getContentPane().add(buildBottomButtonBar(), BorderLayout.SOUTH);

		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resource/icons/card_edit.gif"))); //$NON-NLS-1$
		pack();
	}

	private JPanel buildHeaderPanel() {
		m_headerPanel.setBorder(new EtchedBorder());
		m_cardPanel.setBorder(Borders.DIALOG_BORDER);

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(m_headerPanel, BorderLayout.NORTH);
		panel.add(m_cardPanel, BorderLayout.CENTER);

		return panel;
	}

	private JToolBar buildToolBar() {
		final JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(new JButton(new AddCardAction(this)));
		toolBar.add(new JButton(m_previousCardAction));
		toolBar.add(new JButton(m_nextCardAction));
		toolBar.add(new JButton(new ResetCardAction(this)));
		toolBar.add(new JButton(new RemoveAction(this)));

		return toolBar;
	}

	private JPanel buildBottomButtonBar() {
		final JButton okayButton = new JButton(Localization.get(LC.OKAY));
		okayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				try {
					saveCard();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				close();
			}
		});

		final JButton cancelButton = new JButton(Localization.get(LC.CANCEL));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				hideFrame();
			}
		});

		m_applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				try {
					saveCard();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		final JPanel buttonPanel = ButtonBarFactory.buildOKCancelApplyBar(okayButton, cancelButton, m_applyButton);
		buttonPanel.setBorder(new EmptyBorder(3, 3, 3, 3));

		getRootPane().setDefaultButton(okayButton);

		return buttonPanel;
	}

	private void highlightSearchText() {
		// if (m_searchText != null)
		// {
		// List frontPositions = null;
		// if (m_searchSide == SearchTool.FRONT_SIDE || m_searchSide ==
		// SearchTool.BOTH_SIDES)
		// {
		// frontPositions = SearchTool.search(m_currentCard.getFrontSide(),
		// m_searchText, m_searchSide, m_searchCase);
		// }
		//
		// List backPositions = null;
		// if (m_searchSide == SearchTool.FLIP_SIDE|| m_searchSide ==
		// SearchTool.BOTH_SIDES)
		// {
		// backPositions = SearchTool.search(m_currentCard.getBackSide(),
		// m_searchText, m_searchSide, m_searchCase);
		// }
		//
		// m_cardPanel.highlight(frontPositions, backPositions,
		// m_searchText.length());
		// }
	}
}
