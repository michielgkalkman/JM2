package jmemorize.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

import org.junit.Test;

public class FormattedTextTest {

	@Test
	public void testEmptyDocument() {
		final StyledDocument styledDocument = new HTMLDocument();
		final FormattedText formatted = FormattedText.formatted(styledDocument);

		assertEquals("", formatted.toString());
		assertEquals("", formatted.getFormatted());
		assertEquals("", formatted.getUnformatted());
		final StyledDocument document = formatted.getDocument();
		assertEquals(1, document.getEndPosition().getOffset());
		assertEquals(0, document.getStartPosition().getOffset());
		assertEquals(0, document.getLength());
	}

	@Test
	public void testTextPaneDocument2() throws BadLocationException {
		final String text = "hallo";
		final FormattedText formatted = getFormattedText(text);

		assertEquals(text, formatted.toString());
		assertEquals(text, formatted.getFormatted());
		assertEquals(text, formatted.getUnformatted());
		final StyledDocument document = formatted.getDocument();
		assertEquals(6, document.getEndPosition().getOffset());
		assertEquals(0, document.getStartPosition().getOffset());
		assertEquals(5, document.getLength());

		final FormattedText otherText = getFormattedText("text");
		
		final Set<FormattedText> formattedTextSet = new HashSet<>();
		
		assertFalse(formattedTextSet.contains(formatted));
		formattedTextSet.add(formatted);
		formattedTextSet.add(otherText);
		assertTrue(formattedTextSet.contains(formatted));
		
		final Map<FormattedText, Boolean> map = new HashMap<>();
		assertNull(map.get(formatted));
		
		map.put(formatted, Boolean.FALSE);
		map.put(otherText, Boolean.FALSE);
		assertNotNull(map.get(formatted));
	}

	private FormattedText getFormattedText(final String text) {
		final JTextPane m_textPane       = new JTextPane();
		m_textPane.setText(text);
		final StyledDocument styledDocument = m_textPane.getStyledDocument();
		
		final FormattedText formatted = FormattedText.formatted(styledDocument);
		return formatted;
	}

	@Test
	public void testFormattedTextWithRange() throws BadLocationException {
		final JTextPane m_textPane       = new JTextPane();
		final String text = "hallo, M";
		final String expectedText = "hallo, <i>M</i>";
		m_textPane.setText(text);
		final StyledDocument styledDocument = m_textPane.getStyledDocument();
		

        final SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setItalic(sas, true);

		styledDocument.setCharacterAttributes(7, 2, sas, false);
		
		final FormattedText formatted = FormattedText.formatted(styledDocument, 0, 200);

		
		
		
		assertEquals(text, formatted.toString());
		assertEquals(expectedText, formatted.getFormatted());
		assertEquals(text, formatted.getUnformatted());
		final StyledDocument document = formatted.getDocument();
		assertEquals(9, document.getEndPosition().getOffset());
		assertEquals(0, document.getStartPosition().getOffset());
		assertEquals(8, document.getLength());
	}
}
