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
package jmemorize.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.Preferences;

import jmemorize.core.learn.LearnHistory;
import jmemorize.core.learn.LearnSession;
import jmemorize.core.learn.LearnSessionObserver;
import jmemorize.core.learn.LearnSettings;
import jmemorize.util.RecentItems;


/**
 * The main class of the application.
 * 
 * @author djemili
 */
public class Model {
	
	public interface ProgramEndObserver {
		/**
		 * This method is notified when the program ends.
		 */
		public void onProgramEnd();
	}

	public static final Properties PROPERTIES = new Properties();
	public static final Preferences USER_PREFS = Preferences.userRoot().node(
			"de/riad/jmemorize"); //$NON-NLS-1$

	private static final String PROPERTIES_PATH = "/resource/jMemorize.properties"; //$NON-NLS-1$

	public static final File STATS_FILE = new File(
			System.getProperty("user.home") + "/.jmemorize-stats.xml"); //$NON-NLS-1$ //$NON-NLS-2$

	private final RecentItems m_recentFiles = new RecentItems(5,
			USER_PREFS.node("recent.files")); //$NON-NLS-1$

	private Lesson m_lesson;
	private LearnSettings m_learnSettings;
	private LearnHistory m_globalLearnHistory;
	private int m_runningSessions = 0;

	// observers
	private final List<LessonObserver> m_lessonObservers = new LinkedList<LessonObserver>();
	private final List<LearnSessionObserver> m_learnSessionObservers = new LinkedList<LearnSessionObserver>();
	private final List<ProgramEndObserver> m_programEndObservers = new LinkedList<ProgramEndObserver>();

	// simple logging support
	private static final Logger logger = Logger.getLogger("jmemorize");
	private static Throwable m_lastLoggedThrowable;

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LessonProvider
	 */
	public Lesson getLesson() {
		return m_lesson;
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LessonProvider
	 */
	public RecentItems getRecentLessonFiles() {
		return m_recentFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.core.LessonProvider
	 */
	public void addLessonObserver(final LessonObserver observer) {
		m_lessonObservers.add(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.core.LessonProvider
	 */
	public void removeLessonObserver(final LessonObserver observer) {
		m_lessonObservers.remove(observer);
	}

	/**
	 * Adds a ProgramEndObserver that will be fired when this program closes.
	 * 
	 * @param observer
	 */
	public void addProgramEndObserver(final ProgramEndObserver observer) {
		m_programEndObservers.add(observer);
	}

	/**
	 * @see #addProgramEndObserver(jmemorize.Model.swing.Main.ProgramEndObserver)
	 */
	public void removeProgramEndObserver(final ProgramEndObserver observer) {
		m_programEndObservers.remove(observer);
	}

	/**
	 * Notifies all program end observers and exists the application.
	 */
	public void exit() {
		for (final ProgramEndObserver observer : m_programEndObservers) {
			observer.onProgramEnd();
		}

		System.exit(0);
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LearnSessionProvider
	 */
	public void sessionEnded(final LearnSession session) {
		m_runningSessions--;

		if (session.isRelevant()) {
			final LearnHistory history = m_lesson.getLearnHistory();
			history.addSummary(session.getStart(), session.getEnd(), session
					.getPassedCards().size(), session.getFailedCards().size(),
					session.getSkippedCards().size(), session
							.getRelearnedCards().size());
		}

		for (final LearnSessionObserver observer : m_learnSessionObservers) {
			observer.sessionEnded(session);
		}
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LearnSessionProvider
	 */
	public boolean isSessionRunning() {
		return m_runningSessions > 0;
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LearnSessionProvider
	 */
	public void addLearnSessionObserver(final LearnSessionObserver observer) {
		m_learnSessionObservers.add(observer);
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LearnSessionProvider
	 */
	public void removeLearnSessionObserver(final LearnSessionObserver observer) {
		m_learnSessionObservers.remove(observer);
	}

	/**
	 * @return currently loaded learn strategy.
	 */
	public LearnSettings getLearnSettings() {
		return m_learnSettings;
	}

	/**
	 * @return the statistics for jMemorize.
	 */
	public LearnHistory getGlobalLearnHistory() {
		return m_globalLearnHistory;
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.CategoryObserver
	 */
	public void onCardEvent(final int type, final Card card,
			final Category category, final int deck) {
		fireLessonModified(m_lesson);
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.CategoryObserver
	 */
	public void onCategoryEvent(final int type, final Category category) {
		fireLessonModified(m_lesson);
	}

	public Model() {
		InputStream propertyStream = null;

		try {
			// TODO - make this adjustable
			// Note that the limit might not be enough for finer.
			final Handler fh = new FileHandler("%t/jmemorize%g.log", 10000, 3);
			fh.setLevel(Level.WARNING);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
			final URL resource = getClass().getResource(PROPERTIES_PATH);

			// PROPERTIES.load(resource.openStream());

			if (resource != null) {
				propertyStream = resource.openStream();
				PROPERTIES.load(propertyStream);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			logThrowable("Initialization problem", e);
		} finally {
			try {
				if (propertyStream != null)
					propertyStream.close();
			} catch (final IOException e) {
				e.printStackTrace();
				logThrowable("Initialization problem", e);
			}
		}
	}

	/**
	 * @return <code>true</code> if this is the devel version running.
	 *         <code>false</code> if it is the release version. This can be used
	 *         for new and expiremental features.
	 */
	public static boolean isDevel() {
		final String property = PROPERTIES.getProperty("project.release"); //$NON-NLS-1$
		return !Boolean.valueOf(property).booleanValue();
	}

	/*
	 * Logging utilities
	 */
	public static Logger getLogger() {
		return logger;
	}

	// note that we cache the throwable so that we only log it the first time.
	// This allows us to put a catch all call to this function in ErrorDialog.
	// Ideally, exceptions should be logged where they are first caught, because
	// we have more information about the exception there.
	public static void logThrowable(final String msg, final Throwable t) {
		if (t != null && m_lastLoggedThrowable != t) {
			m_lastLoggedThrowable = t;
			logger.severe(msg);

			// TODO, consider writing these to the log file only once?
			final String java = System.getProperty("java.version");
			final String os = System.getProperty("os.name");
			final String version = Model.PROPERTIES
					.getProperty("project.version");
			final String buildId = Model.PROPERTIES.getProperty("buildId");
			final String txt = "Ver " + version + " (" + buildId + ") - Java "
					+ java + " , OS " + os;
			logger.severe(txt);

			final StringWriter strWriter = new StringWriter();
			final PrintWriter prWriter = new PrintWriter(strWriter);
			t.printStackTrace(prWriter);
			logger.severe(strWriter.toString());
		}
	}

	public static void clearLastThrowable() {
		m_lastLoggedThrowable = null;
	}

	private void fireLessonModified(final Lesson lesson) {
		if (lesson.canSave()) {
			for (final LessonObserver observer : m_lessonObservers) {
				observer.lessonModified(lesson);
			}
		}
	}
}
