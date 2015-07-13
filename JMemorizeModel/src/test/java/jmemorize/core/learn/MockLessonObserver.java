package jmemorize.core.learn;

import static org.junit.Assert.assertEquals;
import jmemorize.core.Lesson;
import jmemorize.core.LessonObserver;

public class MockLessonObserver implements LessonObserver {
	boolean fLessonSaved = false;
	boolean fLessonModified = false;
	boolean fLessonLoaded = false;
	boolean fLessonClosed = false;

	public void reset() {
		fLessonSaved = false;
		fLessonModified = false;
		fLessonLoaded = false;
		fLessonClosed = false;
	}

	public void assertObserver(final boolean fLessonSaved,
			final boolean fLessonModified, final boolean fLessonLoaded,
			final boolean fLessonClosed) {
		assertEquals(this.fLessonSaved, fLessonSaved);
		assertEquals(this.fLessonModified, fLessonModified);
		assertEquals(this.fLessonLoaded, fLessonLoaded);
		assertEquals(this.fLessonClosed, fLessonClosed);
	}

	@Override
	public void lessonSaved(final Lesson lesson) {
		fLessonSaved = true;
	}

	@Override
	public void lessonModified(final Lesson lesson) {
		fLessonModified = true;
	}

	@Override
	public void lessonLoaded(final Lesson lesson) {
		fLessonLoaded = true;
	}

	@Override
	public void lessonClosed(final Lesson lesson) {
		fLessonClosed = true;
	}

}
