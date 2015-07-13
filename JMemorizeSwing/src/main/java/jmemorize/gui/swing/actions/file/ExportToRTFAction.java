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
package jmemorize.gui.swing.actions.file;

import java.io.File;
import java.io.IOException;

import jmemorize.core.Lesson;
import jmemorize.core.Localization;
import jmemorize.core.Model;
import jmemorize.core.io.PdfRtfBuilder;
import jmemorize.core.io.PdfRtfBuilder.Mode;
import jmemorize.gui.swing.CardFont.FontType;
import jmemorize.gui.swing.Settings;
import jmemorize.util.ExtensionFileFilter;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;

/**
 * An action that exports the current lesson to RTF.
 */
public class ExportToRTFAction extends AbstractExportAction {
	public ExportToRTFAction() {
		setValues();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.actions.AbstractExportAction
	 */
	@Override
	protected void doExport(final Lesson lesson, final File file)
			throws IOException {
		final Font frontFont;
		final Font backFont;

		FontFactory.registerDirectories();
		// set up the fonts we will use to write the front and back of cards
		frontFont = getFrontFont();

		backFont = getBackFront();

		new PdfRtfBuilder(frontFont, backFont).export(lesson, Mode.RTF_MODE,
				file);
	}

	private Font getBackFront() {
		final Font backFont;
		final String backFontName = Settings.loadFont(FontType.CARD_FLIP)
				.getFont().getFamily();
		backFont = FontFactory.getFont(backFontName, BaseFont.IDENTITY_H,
				BaseFont.EMBEDDED);

		if (backFont == null) {
			Model.getLogger().warning(
					"FontFactory returned null (back) font for: "
							+ backFontName);
		}
		return backFont;
	}

	private Font getFrontFont() {
		final Font frontFont;
		final String frontFontName = Settings.loadFont(FontType.CARD_FRONT)
				.getFont().getFamily();
		frontFont = FontFactory.getFont(frontFontName, BaseFont.IDENTITY_H,
				BaseFont.EMBEDDED);

		if (frontFont == null) {
			Model.getLogger().warning(
					"FontFactory returned null (front) font for: "
							+ frontFontName);
		}
		return frontFont;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.gui.swing.actions.AbstractExportAction
	 */
	@Override
	protected ExtensionFileFilter getFileFilter() {
		return new ExtensionFileFilter("rtf", "RTF - Rich Text Format");
	}

	private void setValues() {
		setName(Localization.get("MainFrame.EXPORT_RTF")); //$NON-NLS-1$
		setDescription(Localization.get("MainFrame.EXPORT_RTF_DESC")); //$NON-NLS-1$
		setIcon("/resource/icons/rtf.png"); //$NON-NLS-1$
		setMnemonic(1);
	}
}
