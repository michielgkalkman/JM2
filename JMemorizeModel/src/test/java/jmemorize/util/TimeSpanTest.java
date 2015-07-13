package jmemorize.util;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.junit.Assert;
import org.junit.Test;

public class TimeSpanTest {
	@Test
	public void test() {
		testTimeSpan("in 0 minutes", Period.ZERO);
		testTimeSpan("in 0 minutes", Period.ZERO.plusMillis(1));
		testTimeSpan("in one minute", Period.ZERO.plusMinutes(1));
		testTimeSpan("in 2 minutes", Period.ZERO.plusMinutes(2));
		testTimeSpan("in 59 minutes", Period.ZERO.plusMinutes(59));
		testTimeSpan("in 59 minutes", Period.ZERO.plusMinutes(59)
				.plusMillis(99));
		testTimeSpan("in one hour", Period.ZERO.plusHours(1));
		testTimeSpan("in one hour", Period.ZERO.plusHours(1).plusMinutes(1));
		testTimeSpan("in one hour", Period.ZERO.plusHours(1).plusMinutes(59));
		testTimeSpan("in 2 hours", Period.ZERO.plusHours(2));
		testTimeSpan("in one day", Period.ZERO.plusDays(1));
		testTimeSpan("in 2 days", Period.ZERO.plusDays(2));
		testTimeSpan("in 31 days", Period.ZERO.plusMonths(1));
		testTimeSpan("in 62 days", Period.ZERO.plusMonths(2));
		testTimeSpan("in 365 days", Period.ZERO.plusYears(1));
		testTimeSpan("in 730 days", Period.ZERO.plusYears(2));

		testTimeSpan("one day ago", Period.ZERO.minusDays(1));
		testTimeSpan("2 days ago", Period.ZERO.minusDays(2));
	}

	@Test
	public void testTimeSpan() {
		final DateTime now = new DateTime();
		final TimeSpan timeSpan = new TimeSpan(now.toDate(), now.plusDays(1)
				.toDate());
		Assert.assertEquals(86400000, timeSpan.getTicks());
	}

	private void testTimeSpan(final String expectedText,
			final ReadablePeriod period) {
		final DateTime now = new DateTime(2013, 12, 28, 15, 0);

		Assert.assertEquals(expectedText,
				TimeSpan.format(now.toDate(), now.plus(period).toDate()));
	}
}
