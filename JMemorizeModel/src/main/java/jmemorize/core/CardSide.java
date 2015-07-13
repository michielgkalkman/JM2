/*
 * jMemorize - Learning made easy (and fun) - A Leitner flashcards tool
 * Copyright(C) 2004-2009 Riad Djemili and contributors
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

import java.util.LinkedList;
import java.util.List;

/**
 * A card is made up of two card sides which can contain various contents, the
 * most important being text.
 * 
 * @author djemili
 */
public class CardSide implements Cloneable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * Kept very simple for now.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof CardSide) {
			final CardSide other = (CardSide) obj;
			return other.getText().getUnformatted()
					.equals(getText().getUnformatted());
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 * 
	 * Kept very simple for now.
	 */
	@Override
	public int hashCode() {
		return getText().getUnformatted().hashCode();
	}

	public interface CardSideObserver {
		public void onTextChanged(CardSide cardSide, FormattedText text);

		public void onImagesChanged(CardSide cardSide, List<String> imageIDs);
	}

	private FormattedText m_text;
	private final List<String> m_mediaIDs = new LinkedList<String>();
	private final List<CardSideObserver> m_observers = new LinkedList<CardSideObserver>();

	public CardSide(final FormattedText text) {
		setText(text);
	}

	public FormattedText getText() {
		return m_text;
	}

	/**
	 * Note that using this method won't modify the modification date of the
	 * card. Use {@link Card#setSides(String, String)} instead for modifications
	 * done by the user.
	 */
	public void setText(final FormattedText text) {
		if (text.equals(m_text))
			return;

		m_text = text;

		for (final CardSideObserver observer : m_observers) {
			observer.onTextChanged(this, m_text);
		}
	}

	/**
	 * @return the IDs of all images of this card side.
	 */
	public List<String> getMedia() {
		return m_mediaIDs;
	}

	public void setMedia(final List<String> ids) {
		if (m_mediaIDs.equals(ids))
			return;

		m_mediaIDs.clear();
		m_mediaIDs.addAll(ids);

		for (final CardSideObserver observer : m_observers) {
			observer.onImagesChanged(this, m_mediaIDs);
		}
	}

	public void addObserver(final CardSideObserver observer) {
		m_observers.add(observer);
	}

	public void removeObserver(final CardSideObserver observer) {
		m_observers.remove(observer);
	}

	/**
	 * @return the unformatted string representation of the formatted text.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return m_text.getUnformatted();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		final CardSide cardSide = new CardSide((FormattedText) m_text.clone());
		cardSide.m_mediaIDs.addAll(m_mediaIDs);

		return cardSide;
	}
}
