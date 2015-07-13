package jmemorize.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;

import jmemorize.core.CardSide.CardSideObserver;

public class CardTest {

	@Test
	public void simpleTest() {
		final Card firstCard = new Card("0", "0");
		firstCard.setSides("1", "1");
		assertEquals("(1/1)", firstCard.toString());

		firstCard.setLearnedAmount(true, 1);
		assertEquals(1, firstCard.getLearnedAmount(true));

		firstCard.setLearnedAmount(false, 2);
		assertEquals(2, firstCard.getLearnedAmount(false));
	}

	@Test
	public void cardDatesTest() {
		final Date now = new DateTime().toDate();

		final Card card = new Card(now, "0", "0");

		{
			final Date dateCreated = card.getDateCreated();
			assertEquals(now, dateCreated);
			assertTrue(now != dateCreated);
			final Date dateModified = card.getDateModified();
			assertEquals(now, dateModified);
			assertTrue(now != dateModified);
			final Date dateTouched = card.getDateTouched();
			assertEquals(now, dateTouched);
			assertTrue(now != dateTouched);

			assertNull(card.getDateExpired());
			assertNull(card.getDateTested());
		}

		{
			final Date now2 = new DateTime().toDate();
			card.setDateCreated(now2);

			final Date dateCreated = card.getDateCreated();
			assertEquals(now2, dateCreated);
			assertTrue(now2 != dateCreated);
		}

		{
			final Date now2 = new DateTime().toDate();
			card.setDateModified(now2);

			final Date dateModified = card.getDateModified();
			assertEquals(now2, dateModified);
			assertTrue(now2 == dateModified);
		}

		{
			final Date now2 = new DateTime().toDate();
			card.setDateTouched(now2);

			final Date dateTouched = card.getDateTouched();
			assertEquals(now2, dateTouched);
			assertTrue(now2 != dateTouched);
		}

		{
			final Date now2 = new DateTime().toDate();
			card.setDateExpired(now2);

			final Date dateExpired = card.getDateExpired();
			assertEquals(now2, dateExpired);
			assertTrue(now2 != dateExpired);
		}

		{
			final Date now2 = new DateTime().toDate();
			card.setDateTested(now2);

			final Date dateTested = card.getDateTested();
			assertEquals(now2, dateTested);
			assertTrue(now2 != dateTested);

			final Date dateTouched = card.getDateTouched();
			assertEquals(now2, dateTouched);
			assertTrue(now2 != dateTouched);
		}
	}

	@Test(expected = NullPointerException.class)
	public void cardDateCreatedNullTest() {
		final Card card = new Card("0", "0");
		card.setSides("1", "1");

		card.setDateCreated(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cardDateModifiedBeforeDateCreatedTest() {
		final DateTime dateTime = new DateTime();
		final Date now = dateTime.toDate();

		final Card card = new Card(now, "0", "0");
		
		card.setDateModified(dateTime.minusHours(1).toDate());
	}

	@Test
	public void equalsCards() {
		final Card firstCard = new Card("0", "0");
		final Card secondCard = new Card("0", "0");
		assertEquals(firstCard, secondCard);
	}

	@Test
	public void testHashCode() {
		final Card firstCard = new Card("0", "0");
		final Card secondCard = new Card("1", "1");

		final Map<Card, Boolean> card2Boolean = new HashMap<Card, Boolean>();

		card2Boolean.put(firstCard, Boolean.TRUE);

		assertTrue(card2Boolean.containsKey(firstCard));
		assertFalse(card2Boolean.containsKey(secondCard));
	}

	@Test
	public void simpleTests() {
		final Lesson lesson = new Lesson(true);

		final Category rootCategory = lesson.getRootCategory();
		final Card firstCard = new Card("0", "0");
		rootCategory.addCard(firstCard);
		final Category category1 = new Category("category1");
		category1.addCard(new Card("1", "1"));
		rootCategory.addCategoryChild(category1);
		final Category category2 = new Category("category2");
		category1.addCard(new Card("2", "2"));
		rootCategory.addCategoryChild(category2);
		final Category category3 = new Category("category3");
		category3.addCard(new Card("3", "3"));
		category2.addCategoryChild(category3);

		assertEquals("(0/0)", firstCard.toString());

		assertEquals(0, rootCategory.getDepth());
		assertEquals(1, category1.getDepth());
		assertEquals(1, category2.getDepth());
		assertEquals(2, category3.getDepth());
		assertTrue(category1.equals(category1));
		assertTrue(category2.equals(category2));
		assertTrue(category3.equals(category3));
		assertFalse(category1.equals(category3));
		assertFalse(category3.equals(category2));
		assertFalse(category2.equals(category1));

		assertTrue(lesson.canSave());
		lesson.setCanSave(false);
		firstCard.setSides("0", "0");
		assertFalse(lesson.canSave());
		firstCard.setSides("0new", "0new");
		assertTrue(lesson.canSave());

		assertEquals(0, rootCategory.getDepth());
		assertEquals(1, category1.getDepth());
		assertEquals(1, category2.getDepth());
		assertEquals(2, category3.getDepth());
		assertTrue(category1.equals(category1));
		assertTrue(category2.equals(category2));
		assertTrue(category3.equals(category3));
		assertFalse(category1.equals(category3));
		assertFalse(category3.equals(category2));
		assertFalse(category2.equals(category1));

		assertEquals("0new", firstCard.getFrontSide().getText().getFormatted());
		assertEquals("0new", firstCard.getBackSide().getText().getUnformatted());

		firstCard.getFrontSide().setText(FormattedText.formatted("0new"));
		firstCard.getBackSide().setText(FormattedText.unformatted("0new"));

		assertEquals(0, rootCategory.getDepth());
		assertEquals(1, category1.getDepth());
		assertEquals(1, category2.getDepth());
		assertEquals(2, category3.getDepth());
		assertTrue(category1.equals(category1));
		assertTrue(category2.equals(category2));
		assertTrue(category3.equals(category3));
		assertFalse(category1.equals(category3));
		assertFalse(category3.equals(category2));
		assertFalse(category2.equals(category1));

		assertEquals("0new", firstCard.getFrontSide().getText().getFormatted());
		assertEquals("0new", firstCard.getBackSide().getText().getUnformatted());

		firstCard.getFrontSide().setText(FormattedText.formatted("0new"));
		firstCard.getBackSide().setText(FormattedText.unformatted("1new"));

		assertEquals(0, rootCategory.getDepth());
		assertEquals(1, category1.getDepth());
		assertEquals(1, category2.getDepth());
		assertEquals(2, category3.getDepth());
		assertTrue(category1.equals(category1));
		assertTrue(category2.equals(category2));
		assertTrue(category3.equals(category3));
		assertFalse(category1.equals(category3));
		assertFalse(category3.equals(category2));
		assertFalse(category2.equals(category1));

		assertEquals("0new", firstCard.getFrontSide().getText().getFormatted());
		assertEquals("1new", firstCard.getBackSide().getText().getUnformatted());

		firstCard.setSides("0new", "0new");

		assertEquals(0, rootCategory.getDepth());
		assertEquals(1, category1.getDepth());
		assertEquals(1, category2.getDepth());
		assertEquals(2, category3.getDepth());
		assertTrue(category1.equals(category1));
		assertTrue(category2.equals(category2));
		assertTrue(category3.equals(category3));
		assertFalse(category1.equals(category3));
		assertFalse(category3.equals(category2));
		assertFalse(category2.equals(category1));

		assertEquals("0new", firstCard.getFrontSide().getText().getFormatted());
		assertEquals("0new", firstCard.getBackSide().getText().getUnformatted());
	}

	@Test
	public void testSetSides() throws InterruptedException {
		final Lesson lesson = new Lesson(true);

		final String front = "0";
		final String back = "1";
		final Category rootCategory = lesson.getRootCategory();
		final Card firstCard = new Card(front, back);
		rootCategory.addCard(firstCard);

		// If card sides are set again, then setSides() must check if sides are
		// equal and return again speedily.
		final Date dateModified = firstCard.getDateModified();

		firstCard.getFrontSide().getText();
		firstCard.setSides(front, back);

		assertEquals(dateModified, firstCard.getDateModified());

		Thread.sleep(100);
		firstCard.setSides("2", back);
		assertFalse(dateModified.equals(firstCard.getDateModified()));

		final Date newDateModified = firstCard.getDateModified();

		Thread.sleep(100);

		firstCard.setSides("2", "3");
		assertFalse(newDateModified.equals(firstCard.getDateModified()));

	}

	@Test
	public void simpeleNotEquals() throws InterruptedException {
		final Date now = new Date();
		final Card card1 = new Card(now, "front", "back");
		Thread.sleep(1000);
		final Date then = new Date();
		final Card card2 = new Card(then, "front2", "back");

		assertFalse(now.equals(then));
		assertFalse(card1.equals(card2));
		assertFalse(card1.equals(then));
	}

	@Test
	public void simpeleEquals() throws InterruptedException {
		final Date now = new Date();
		final Card card1 = new Card(now, "front", "back");
		Thread.sleep(1000);
		final Date then = new Date();
		final Card card2 = new Card(then, "front", "back");

		assertFalse(now.equals(then));
		assertTrue(card1.equals(card2));
		assertFalse(card1.equals(then));

		final Category category = new Category("category");
		card1.setCategory(category);

		assertTrue(card1.equals(card2));

		card2.setCategory(category);

		assertTrue(card1.equals(card2));
	}

	@Test
	public void testClone() {
		final Date now = new Date();
		final Card card = new Card(now, "front", "back");
		final Card clone = (Card) card.clone();

		assertEquals(card, clone);
	}

	@Test
	public void testAttachCardSideObservers() {
		final Date now = new Date();
		final Card card1 = new Card(now, "front", "back");

		final CardSideObserver observer = new CardSideObserver() {

			@Override
			public void onTextChanged(final CardSide cardSide, final FormattedText text) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onImagesChanged(final CardSide cardSide, final List<String> imageIDs) {
				// TODO Auto-generated method stub

			}
		};

		card1.getFrontSide().addObserver(observer);
	}
	
	@Test
	public void testResults() {
		final Card card = new Card("front", "back");
		
		assertEquals( 0, card.getTestsTotal());
		assertEquals( 0, card.getTestsPassed());
		assertEquals( 0, card.getPassRatio());
		
		card.incStats(1, 1);
		
		assertEquals( 1, card.getTestsTotal());
		assertEquals( 1, card.getTestsPassed());
		assertEquals( 100, card.getPassRatio());
	}
	
	@Test
	public void testLearnedAmount() {
		final Card card = new Card("front", "back");
		
		assertEquals( 0, card.getLearnedAmount(true));
		assertEquals( 0, card.getLearnedAmount(false));

		card.setLearnedAmount( true, 5);
		card.setLearnedAmount( false, 3);
		
		assertEquals( 5, card.getLearnedAmount(true));
		assertEquals( 3, card.getLearnedAmount(false));
		
		card.resetLearnedAmount();
		
		assertEquals( 0, card.getLearnedAmount(true));
		assertEquals( 0, card.getLearnedAmount(false));
	}
}
