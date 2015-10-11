package jmemorize.core.learn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jmemorize.core.Card;
import jmemorize.core.Category;
import jmemorize.core.Lesson;
import jmemorize.core.LessonObserver;
import jmemorize.core.learn.LearnHistory.CalendarComparator;
import jmemorize.core.learn.LearnHistory.SessionSummary;
import jmemorize.provider.DefaultLearnSessionProviderImpl;
import jmemorize.provider.LearnLessonsSessionProvider;

public class DefaultLearnSessionTest {
	@Test
	public void testWithListener() {
		final Lesson lesson = new Lesson(true);

		final Category category = lesson.getRootCategory();

		final Card card = new Card("0", "0");
		category.addCard(card);

		final LearnSettings settings = new LearnSettings();

		final List<Card> selectedCards = new ArrayList<>();
		selectedCards.add(card);

		final boolean learnUnlearned = true;
		final boolean learnExpired = true;
		final LearnLessonsSessionProvider learnLessonsSessionProvider = new DefaultLearnSessionProviderImpl();

		final MockLessonObserver observer = new MockLessonObserver();
		learnLessonsSessionProvider.addLessonObserver(observer);

		learnLessonsSessionProvider.setLesson(lesson);

		learnLessonsSessionProvider.addLessonObserver(new LessonObserver() {

			@Override
			public void lessonSaved(final Lesson lesson) {
				fail();
			}

			@Override
			public void lessonModified(final Lesson lesson) {
				fail();
			}

			@Override
			public void lessonLoaded(final Lesson lesson) {
				fail();
			}

			@Override
			public void lessonClosed(final Lesson lesson) {
				fail();
			}
		});

		learnLessonsSessionProvider.startLearnSession(settings, selectedCards, category, learnUnlearned, learnExpired);

		observer.assertObserver(false, false, true, false);

	}

	@Test
	public void test() {
		final Lesson lesson = new Lesson(true);
		final Card card = new Card("0", "0");

		final LearnLessonsSessionProvider learnLessonsSessionProvider = learnStuff(lesson, card);

		learnLessonsSessionProvider.setLesson(lesson);

		learnLessonsSessionProvider.createNewLesson();

		final Lesson lesson2 = learnLessonsSessionProvider.getLesson();

		learnStuff(lesson2, card);
	}

	private LearnLessonsSessionProvider learnStuff(final Lesson lesson, final Card card) {
		final Category category = lesson.getRootCategory();
		category.addCard(card);

		final LearnSettings settings = new LearnSettings();
		final List<Card> selectedCards = new ArrayList<>();
		selectedCards.add(card);

		final boolean learnUnlearned = true;
		final boolean learnExpired = true;
		final LearnLessonsSessionProvider learnLessonsSessionProvider = new DefaultLearnSessionProviderImpl();

		learnLessonsSessionProvider.setLesson(lesson);

		learnLessonsSessionProvider.startLearnSession(settings, selectedCards, category, learnUnlearned, learnExpired);

		assertTrue(learnLessonsSessionProvider.isSessionRunning());

		final LearnHistory learnHistory = lesson.getLearnHistory();

		assertEquals(0, learnHistory.getSummaries().size());

		final SessionSummary average = learnHistory.getAverage();

		assertTrue(0 == average.getPassed());
		assertEquals(average.getStart(), average.getEnd());

		assertNull(learnHistory.getLastSummary());
		assertNull(learnHistory.getSessionsSummary());
		assertTrue(learnHistory.getSummaries(12).isEmpty());

		assertSummaries(learnHistory, LearnHistory.DATE_COMP);
		assertSummaries(learnHistory, LearnHistory.MONTH_COMP);
		assertSummaries(learnHistory, LearnHistory.WEEK_COMP);
		assertSummaries(learnHistory, LearnHistory.YEAR_COMP);

		final List<SessionSummary> summaries = learnHistory.getSummaries(LearnHistory.SIMPLE_COMP, 12, true);
		assertEquals(0, summaries.size());
		assertTrue(learnHistory.getSummaries(LearnHistory.SIMPLE_COMP, 12, false).isEmpty());
		assertTrue(learnHistory.getSummaries(LearnHistory.SIMPLE_COMP).isEmpty());
		return learnLessonsSessionProvider;
	}

	private void assertSummaries(final LearnHistory learnHistory, final CalendarComparator calendarComparator) {
		assertTrue(learnHistory.getSummaries(calendarComparator).isEmpty());
		assertTrue(learnHistory.getSummaries(calendarComparator, 12, false).isEmpty());

		final List<SessionSummary> summaries = learnHistory.getSummaries(calendarComparator, 12, true);
		assertEquals(3, summaries.size());
		assertEquals(0.0f, summaries.get(0).getFailed(), Float.POSITIVE_INFINITY);
		assertEquals(0.0f, summaries.get(0).getPassed(), Float.POSITIVE_INFINITY);
		assertEquals(0, summaries.get(0).getDuration());
	}

	@Test
	public void testCompare() {
		final Category category = new Category("SampleCatagory");
		final LearnSettings settings = new LearnSettings();
		final List<Card> selectedCards = new ArrayList<Card>();
		{
			final Card card = new Card("front", "back");
			category.addCard(card, 1);
			selectedCards.add(card);
		}
		{
			final Card card = new Card("front2", "back2");
			category.addCard(card, 2);
			selectedCards.add(card);
		}
		final boolean learnUnlearned = true;
		final boolean learnExpired = true;
		final LearnSessionProvider provider = new DefaultLearnSessionProviderImpl();
		final DefaultLearnSession defaultLearnSession = new DefaultLearnSession(category, settings, selectedCards,
				learnUnlearned, learnExpired, provider);

		assertNotNull(defaultLearnSession);
	}

	@Test
	public void testSkipCard() {
		final Lesson lesson = new Lesson(true);

		final Category category = lesson.getRootCategory();

		final Card card = new Card("0", "0");
		category.addCard(card);

		final LearnSettings settings = new LearnSettings();

		final List<Card> selectedCards = new ArrayList<>();
		selectedCards.add(card);

		final boolean learnUnlearned = true;
		final boolean learnExpired = true;
		final LearnLessonsSessionProvider learnLessonsSessionProvider = new DefaultLearnSessionProviderImpl();

		final MockLessonObserver observer = new MockLessonObserver();
		learnLessonsSessionProvider.addLessonObserver(observer);

		learnLessonsSessionProvider.setLesson(lesson);

		final TestLearnSessionObserver observer2 = new TestLearnSessionObserver();

		learnLessonsSessionProvider.addLearnSessionObserver(observer2);

		learnLessonsSessionProvider.startLearnSession(settings, selectedCards, category, learnUnlearned, learnExpired);

		final LearnSession session = observer2.getSession();
		assertNotNull(session);

		assertEquals(1, session.getCardsLeft().size());
		assertEquals(0, session.getSkippedCards().size());

		session.cardSkipped();

		assertEquals(1, session.getCardsLeft().size());
		assertEquals(1, session.getSkippedCards().size());
	}

	public static class TestLearnSessionObserver implements LearnSessionObserver {

		private LearnSession session;

		@Override
		public void sessionStarted(final LearnSession session) {
			this.setSession(session);
		}

		@Override
		public void sessionEnded(final LearnSession session) {
			// TODO Auto-generated method stub

		}

		public LearnSession getSession() {
			return session;
		}

		public void setSession(final LearnSession session) {
			this.session = session;
		}
	}

	@Test
	public void testRaiseCardLevel() {

		final Lesson lesson = new Lesson(true);

		final Category category = lesson.getRootCategory();

		final Card card = new Card("0", "0");
		category.addCard(card);

		final LearnSettings settings = new LearnSettings();

		final List<Card> selectedCards = new ArrayList<>();
		selectedCards.add(card);

		final boolean learnUnlearned = true;
		final boolean learnExpired = true;
		final LearnLessonsSessionProvider learnLessonsSessionProvider = new DefaultLearnSessionProviderImpl();

		final MockLessonObserver observer = new MockLessonObserver();
		learnLessonsSessionProvider.addLessonObserver(observer);

		learnLessonsSessionProvider.setLesson(lesson);

		final TestLearnSessionObserver observer2 = new TestLearnSessionObserver();

		learnLessonsSessionProvider.addLearnSessionObserver(observer2);

		learnLessonsSessionProvider.startLearnSession(settings, selectedCards, category, learnUnlearned, learnExpired);

		final LearnSession session = observer2.getSession();
		assertNotNull(session);

		assertEquals(1, session.getCardsLeft().size());
		assertEquals(0, session.getSkippedCards().size());
		assertEquals(1, session.getCheckedCards().size());
		assertNotNull(session.getCurrentCard());

		session.cardChecked(true, true);

		assertEquals(0, session.getCardsLeft().size());
		assertEquals(0, session.getSkippedCards().size());
		assertEquals(1, session.getCheckedCards().size());
		assertNotNull(session.getCurrentCard());
		
		
//		
//		
//		check dateTested, etc..card.clone().
	}

}
