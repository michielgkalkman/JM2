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
package jmemorize.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

import jmemorize.core.Card;
import jmemorize.core.Category;
import jmemorize.core.Lesson;

/**
 * @author jan stamer
 * @author djemili
 */
public class PdfRtfBuilder {
	public enum Mode {
		PDF_MODE
	}

	// These get set in export() prior to building the pdf/rtf
	private final Font frontFont;
	private final Font backFont;

	public PdfRtfBuilder(final Font frontFont, final Font backFont) {
		super();

		this.frontFont = frontFont;
		this.backFont = backFont;
	}

	public void export(final Lesson lesson, final Mode mode, final File file)
			throws IOException {

		try {
			final Document doc = new Document();
			final OutputStream out = new FileOutputStream(file);

				PdfWriter.getInstance(doc, out);

			doc.open();

			// add cards in subtrees
			final List<Category> subtree = lesson.getRootCategory()
					.getSubtreeList();
			for (final Category category : subtree) {
				writeCategory(doc, category);
			}

			doc.close();

		} catch (final Throwable t) {
			throw (IOException) new IOException("Could not export to PDF")
					.initCause(t);
		}
	}

	/**
	 * Adds given category to document
	 * 
	 * @param doc
	 *            document to add to
	 * @param category
	 *            given category
	 */
	private void writeCategory(final Document doc, final Category category)
			throws DocumentException {
		// ignore empty categories
		if (category.getLocalCards().size() == 0) {
			return;
		}

		writeCategoryHeader(doc, category);

		for (final Card card : category.getLocalCards()) {
			writeCard(doc, card);
		}
	}

	private static void writeCategoryHeader(final Document doc,
			final Category category) throws DocumentException {
		final Chunk chunk = new Chunk(category.getPath());
		chunk.setFont(new Font(FontFamily.HELVETICA, 12f, Font.BOLD));

		final Paragraph paragraph = new Paragraph(chunk);
		paragraph.setSpacingBefore(1f);

		doc.add(paragraph);
	}

	/**
	 * Adds given card to document
	 * 
	 * @param doc
	 *            document to add to
	 * @param card
	 *            given card
	 */
	private void writeCard(final Document doc, final Card card)
			throws DocumentException {
		final PdfPTable table = new PdfPTable(2);

		table.setPaddingTop(3f);
//		table.setBorderWidth(1.0f);
//		table.setTableFitsPage(true);
//		table.complete();

		final Phrase front = new Phrase(card.getFrontSide().getText()
				.getUnformatted(), frontFont);
		table.addCell(front);
		final Phrase back = new Phrase(card.getBackSide().getText()
				.getUnformatted(), backFont);
		table.addCell(back);

		doc.add(table);
	}
}