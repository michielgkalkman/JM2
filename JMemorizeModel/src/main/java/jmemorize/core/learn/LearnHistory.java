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
package jmemorize.core.learn;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jmemorize.core.Localization;

/**
 * Stores the history of learn sessions and provides statistics.
 * 
 * @author djemili
 */
public class LearnHistory {
	public class SessionSummary implements Cloneable {
		private final Date m_start;
		private final Date m_end;
		private final int m_duration;

		private final float m_passed;
		private final float m_failed;
		private final float m_skipped;
		private final float m_relearned;

		private SessionSummary(final Date start, final Date end,
				final float passed, final float failed, final float skipped,
				final float relearned) {
			this(start, end,
					(int) ((end.getTime() - start.getTime()) / (1000 * 60)),
					passed, failed, skipped, relearned);
		}

		private SessionSummary(final Date start) {
			this(start, start, 0.0f, 0.0f, 0.0f, 0.0f);
		}

		private SessionSummary(final Date start, final Date end,
				final int duration, final float passed, final float failed,
				final float skipped, final float relearned) {
			m_start = start;
			m_end = end;
			m_duration = duration;

			m_passed = passed;
			m_failed = failed;
			m_skipped = skipped;
			m_relearned = relearned;
		}

		public Date getStart() {
			return (Date) m_start.clone();
		}

		public Date getEnd() {
			return (Date) m_end.clone();
		}

		public int getDuration() {
			return m_duration;
		}

		public float getPassed() {
			return m_passed;
		}

		public float getFailed() {
			return m_failed;
		}

		public float getSkipped() {
			return m_skipped;
		}

		public float getRelearned() {
			return m_relearned;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#clone()
		 */
		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof SessionSummary)) {
				return false;
			}

			final SessionSummary other = (SessionSummary) obj;

			return m_passed == other.m_passed && m_failed == other.m_failed
					&& m_skipped == other.m_skipped
					&& m_relearned == other.m_relearned;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return m_start.hashCode();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "summary(" + m_start + ", " + m_passed + "/" + m_failed
					+ ")";
		}
	}

	public abstract static class CalendarComparator implements
			Comparator<SessionSummary> {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator
		 */
		@Override
		public int compare(final SessionSummary s1, final SessionSummary s2) {
			final Calendar c1 = Calendar.getInstance();
			c1.setTime(s1.getStart());

			final Calendar c2 = Calendar.getInstance();
			c2.setTime(s2.getStart());

			final long v1 = toValue(c1);
			final long v2 = toValue(c2);

			return v1 == v2 ? 0 : v1 > v2 ? 1 : -1;
		}

		public abstract long toValue(Calendar c);

		public abstract DateFormat getFormat();

		public abstract boolean showRotated();

		public abstract void decCalendarValue(Calendar c);

	}

	private static class SimpleComparator extends CalendarComparator {
		@Override
		public long toValue(final Calendar c) {
			return c.getTimeInMillis();
		}

		@Override
		public DateFormat getFormat() {
			return Localization.SHORT_DATE_FORMATER;
		}

		@Override
		public boolean showRotated() {
			return true;
		}

		@Override
		public void decCalendarValue(final Calendar c) {
			throw new UnsupportedOperationException();
		}
	}

	private static class DateComparator extends CalendarComparator {
		@Override
		public long toValue(final Calendar c) {
			return c.get(Calendar.DAY_OF_YEAR) + 1000 * c.get(Calendar.YEAR);
		}

		@Override
		public DateFormat getFormat() {
			return DateFormat.getDateInstance(DateFormat.SHORT);
		}

		@Override
		public boolean showRotated() {
			return true;
		}

		@Override
		public void decCalendarValue(final Calendar c) {
			c.add(Calendar.DAY_OF_YEAR, -1);
		}
	}

	private static class WeekComparator extends CalendarComparator {
		@Override
		public long toValue(final Calendar c) {
			return c.get(Calendar.WEEK_OF_YEAR) + 1000 * c.get(Calendar.YEAR);
		}

		@Override
		public DateFormat getFormat() {
			return new SimpleDateFormat("w/yyyy");
		}

		@Override
		public boolean showRotated() {
			return true;
		}

		@Override
		public void decCalendarValue(final Calendar c) {
			c.add(Calendar.WEEK_OF_YEAR, -1);
		}
	}

	private static class MonthComparator extends CalendarComparator {
		@Override
		public long toValue(final Calendar c) {
			return c.get(Calendar.MONTH) + 1000 * c.get(Calendar.YEAR);
		}

		@Override
		public DateFormat getFormat() {
			return new SimpleDateFormat("M/yyyy");
		}

		@Override
		public boolean showRotated() {
			return true;
		}

		@Override
		public void decCalendarValue(final Calendar c) {
			c.add(Calendar.MONTH, -1);
		}
	}

	private static class YearComparator extends CalendarComparator {
		@Override
		public long toValue(final Calendar c) {
			return c.get(Calendar.YEAR);
		}

		@Override
		public DateFormat getFormat() {
			return new SimpleDateFormat("yyyy");
		}

		@Override
		public boolean showRotated() {
			return false;
		}

		@Override
		public void decCalendarValue(final Calendar c) {
			c.add(Calendar.YEAR, -1);
		}
	}

	public static final CalendarComparator SIMPLE_COMP = new SimpleComparator();
	public static final CalendarComparator DATE_COMP = new DateComparator();
	public static final CalendarComparator WEEK_COMP = new WeekComparator();
	public static final CalendarComparator MONTH_COMP = new MonthComparator();
	public static final CalendarComparator YEAR_COMP = new YearComparator();

	// TODO enforce that m_summaries is always sorted in descending date order
	private final List<SessionSummary> m_summaries = new ArrayList<SessionSummary>();

	private boolean m_isLoaded; // false, if created from scratch

	public LearnHistory() {
		this(null);
	}

	public LearnHistory(final File file) {
	}

	public void addSummary(final Date start, final Date end, final int passed,
			final int failed, final int skipped, final int relearned) {
		final SessionSummary sessionSummary = new SessionSummary(start, end,
				passed, failed, skipped, relearned);

		m_summaries.add(sessionSummary);
	}

	public void setIsLoaded(final boolean loaded) {
		m_isLoaded = loaded;
	}

	public boolean isLoaded() {
		return m_isLoaded;
	}

	public SessionSummary getLastSummary() {
		final int size = m_summaries.size();
        if (size == 0)
			return null;

		return m_summaries.get(size - 1);
	}

	public List<SessionSummary> getSummaries() {
		return m_summaries;
	}

	public List<SessionSummary> getSummaries(final int limit) {
		final int size = m_summaries.size();
        final int n = Math.min(limit, size);
		return m_summaries.subList(size - n, size);
	}

	public List<SessionSummary> getSummaries(final CalendarComparator comp) {
		final List<SessionSummary> list = new LinkedList<SessionSummary>();

		SessionSummary lastSummary = null;
		SessionSummary aggregatedSummary = null;

		// TODO refactor and use getSummary(date, comp)
		for (final SessionSummary summary : m_summaries) {
			if (lastSummary == null || comp.compare(summary, lastSummary) != 0) {
				if (aggregatedSummary != null)
					list.add(aggregatedSummary);

				try {
					aggregatedSummary = (SessionSummary) summary.clone();
				} catch (final CloneNotSupportedException e) {
					assert false;
				}
			} else {
				aggregatedSummary = new SessionSummary(
						aggregatedSummary.m_start, summary.m_end,
						aggregatedSummary.m_duration + summary.m_duration,
						aggregatedSummary.m_passed + summary.m_passed,
						aggregatedSummary.m_failed + summary.m_failed,
						aggregatedSummary.m_skipped + summary.m_skipped,
						aggregatedSummary.m_relearned + summary.m_relearned);
			}

			lastSummary = summary;
		}

		if (aggregatedSummary != null)
			list.add(aggregatedSummary);

		return list;
	}

	public List<SessionSummary> getSummaries(final CalendarComparator comp,
			final int limit, final boolean showEmpty) {
		if (showEmpty && comp != SIMPLE_COMP) {
			final List<SessionSummary> summaries = new ArrayList<SessionSummary>(
					limit);
			final Calendar c = Calendar.getInstance();
			Date date = c.getTime();

			int lastEntry = 0;
			for (int i = 0; i < limit; i++) {
				SessionSummary summary = getSummary(date, comp);

				if (summary == null)
					summary = new SessionSummary(date);
				else
					lastEntry = i;

				summaries.add(0, summary);

				comp.decCalendarValue(c);
				date = c.getTime();
			}

			final int size = summaries.size();
			lastEntry = Math.max(2, lastEntry); // always show at least 3
												// entries

			return summaries.subList(size - lastEntry - 1, size);
		} else {
			// TODO optimize this; remove version without limit argument
			final List<SessionSummary> summaries = getSummaries(comp);
			final int n = Math.min(limit, summaries.size());
			return summaries.subList(summaries.size() - n, summaries.size());
		}
	}

	public SessionSummary getAverage() {
		final float count = m_summaries.size();
		final SessionSummary summary = getSessionsSummary();

		if (count > 0) {
			return new SessionSummary(summary.getStart(), summary.getEnd(),
					(int) (summary.getDuration() / count), summary.getPassed()
							/ count, summary.getFailed() / count,
					summary.getSkipped() / count, summary.getRelearned()
							/ count);
		} else {
			final Date now = new Date();
			return new SessionSummary(now, now, 0, 0, 0, 0);
		}
	}

	/**
	 * @return a aggregated summary for given date and comparator.
	 */
	public SessionSummary getSummary(final Date date,
			final CalendarComparator comp) {
		final Calendar c1 = Calendar.getInstance();
		c1.setTime(date);

		final Calendar c2 = Calendar.getInstance();

		int duration = 0;
		int failed = 0;
		int passed = 0;
		int relearned = 0;
		int skipped = 0;
		boolean found = false;

		for (final SessionSummary summary : m_summaries) {
			c2.setTime(summary.m_start);

			if (comp.toValue(c1) == comp.toValue(c2)) {
				duration += summary.m_duration;
				failed += summary.m_failed;
				passed += summary.m_passed;
				relearned += summary.m_relearned;
				skipped += summary.m_skipped;

				found = true;
			}
		}

		return !found ? null : new SessionSummary(date, date, duration, passed,
				failed, skipped, relearned);
	}

	public SessionSummary getSessionsSummary() {
		final SessionSummary sessionSummary;

		if (m_summaries.isEmpty()) {
			sessionSummary = null;
		} else {

			int duration = 0;
			float passed = 0;
			float failed = 0;
			float skipped = 0;
			float relearned = 0;

			for (final SessionSummary summary : m_summaries) {
				duration += summary.getDuration();
				passed += summary.getPassed();
				failed += summary.getFailed();
				skipped += summary.getSkipped();
				relearned += summary.getRelearned();
			}

			final SessionSummary first = m_summaries.get(0);
			final SessionSummary last = m_summaries.get(m_summaries.size() - 1);

			sessionSummary = new SessionSummary(first.getStart(),
					last.getEnd(), duration, passed, failed, skipped, relearned);
		}
		return sessionSummary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof LearnHistory)) {
			return false;
		}

		final LearnHistory other = (LearnHistory) obj;

		return m_summaries.equals(other.m_summaries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return m_summaries.hashCode();
	}
}
