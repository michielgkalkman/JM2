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
package jmemorize.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Properties;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jmemorize.core.Card;
import jmemorize.core.Category;
import jmemorize.core.Lesson;
import jmemorize.core.LessonObserver;
import jmemorize.core.Model;
import jmemorize.core.Model.ProgramEndObserver;
import jmemorize.core.learn.DefaultLearnSession;
import jmemorize.core.learn.LearnHistory;
import jmemorize.core.learn.LearnSession;
import jmemorize.core.learn.LearnSessionObserver;
import jmemorize.core.learn.LearnSettings;
import jmemorize.core.media.MediaRepository;
import jmemorize.util.RecentItems;

/**
 * The main class of the application.
 * 
 * @author djemili
 */
public class DefaultLearnSessionProviderImpl extends Observable implements
		LearnLessonsSessionProvider {
	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultLearnSessionProviderImpl.class); 
	
	public static final Properties PROPERTIES = new Properties();
	public static final Preferences USER_PREFS = Preferences.userRoot().node(
			"de/riad/jmemorize"); //$NON-NLS-1$

	protected static final String PROPERTIES_PATH = "/resource/jMemorize.properties"; //$NON-NLS-1$

	public static final File STATS_FILE = new File(
			System.getProperty("user.home") + "/.jmemorize-stats.xml"); //$NON-NLS-1$ //$NON-NLS-2$

	private final RecentItems m_recentFiles = new RecentItems(5,
			USER_PREFS.node("recent.files")); //$NON-NLS-1$

	protected Lesson m_lesson;
	protected LearnSettings m_learnSettings;
	private LearnHistory m_globalLearnHistory;
	private int m_runningSessions = 0;

	// observers
	private final List<LessonObserver> m_lessonObservers = new LinkedList<LessonObserver>();
	private final List<LearnSessionObserver> m_learnSessionObservers = new LinkedList<LearnSessionObserver>();
	private final List<ProgramEndObserver> m_programEndObservers = new LinkedList<ProgramEndObserver>();

	// simple logging support
	private static Throwable m_lastLoggedThrowable;

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LessonProvider
	 */
	@Override
	public void createNewLesson() {
		MediaRepository.getInstance().clear();
		setLesson(new Lesson(false));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.core.LessonProvider
	 */
	@Override
	public void setLesson(final Lesson lesson) {
		final Lesson oldLesson = m_lesson;
		m_lesson = lesson;

		if (oldLesson != null) {
			fireLessonClosed(oldLesson);
		}

		fireLessonLoaded(m_lesson);
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LessonProvider
	 */
//	@Override
//	public void loadLesson(final File file) throws IOException {
//		try {
//			MediaRepository.getInstance().clear();
//
//			final Lesson lesson = new Lesson(false);
//			XmlBuilder.loadFromXMLFile(file, lesson);
//			lesson.setFile(file);
//			lesson.setCanSave(false);
//			m_recentFiles.push(file.getAbsolutePath());
//
//			setLesson(lesson);
//			// startExpirationTimer(); TODO expiration timer
//		} catch (final Exception e) {
//			m_recentFiles.remove(file.getAbsolutePath());
//			logThrowable("Error loading lesson", e);
//			throw new IOException(e.getMessage());
//		}
//	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LessonProvider
	 */
//	@Override
//	public void saveLesson(final Lesson lesson, final File file)
//			throws IOException {
//		try {
//			final File tempFile = new File(file.getAbsolutePath() + "~"); //$NON-NLS-1$
//			XmlBuilder.saveAsXMLFile(tempFile, lesson);
//
//			file.delete();
//			copyFile(tempFile, file);
//
//			lesson.setFile(file); // note: sets file only if no exception
//			lesson.setCanSave(false);
//			m_recentFiles.push(file.getAbsolutePath());
//
//			for (final LessonObserver observer : m_lessonObservers) {
//				observer.lessonSaved(lesson);
//			}
//		} catch (final Throwable t) {
//			throw new IOException(t.getMessage());
//		}
//	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LessonProvider
	 */
	@Override
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
	@Override
	public void addLessonObserver(final LessonObserver observer) {
		m_lessonObservers.add(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.core.LessonProvider
	 */
	@Override
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
	@Override
	public void startLearnSession(final LearnSettings settings,
			final List<Card> selectedCards, final Category category,
			final boolean learnUnlearned, final boolean learnExpired) {
		final LearnSession session = new DefaultLearnSession(category,
				settings, selectedCards, learnUnlearned, learnExpired, this);

		m_runningSessions++;

		for (final LearnSessionObserver observer : m_learnSessionObservers) {
			observer.sessionStarted(session);
		}

		// this needs to be called after notifying the observers so that they
		// don't miss the first card
		session.startLearning();
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LearnSessionProvider
	 */
	@Override
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
	@Override
	public boolean isSessionRunning() {
		return m_runningSessions > 0;
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LearnSessionProvider
	 */
	@Override
	public void addLearnSessionObserver(final LearnSessionObserver observer) {
		m_learnSessionObservers.add(observer);
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.LearnSessionProvider
	 */
	@Override
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
	@Override
	public void onCardEvent(final int type, final Card card,
			final Category category, final int deck) {
		fireLessonModified(m_lesson);
	}

	/*
	 * (non-Javadoc) Declared in jmemorize.core.CategoryObserver
	 */
	@Override
	public void onCategoryEvent(final int type, final Category category) {
		fireLessonModified(m_lesson);
	}

	public DefaultLearnSessionProviderImpl() {
		InputStream propertyStream = null;

		try {
			final URL resource = getClass().getResource(PROPERTIES_PATH);

			if (resource != null) {
				propertyStream = resource.openStream();
				PROPERTIES.load(propertyStream);
			}

			final InputStream resourceAsStream = getClass()
					.getResourceAsStream(
							"/META-INF/org.taHjaj.wo/jMemorizeSwing/pom.properties");
			if (resourceAsStream != null) {
				PROPERTIES.load(resourceAsStream);
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


	// note that we cache the throwable so that we only log it the first time.
	// This allows us to put a catch all call to this function in ErrorDialog.
	// Ideally, exceptions should be logged where they are first caught, because
	// we have more information about the exception there.
	public static void logThrowable(final String msg, final Throwable t) {
		if (t != null && m_lastLoggedThrowable != t) {
			m_lastLoggedThrowable = t;
			LOGGER.error(msg);

			// TODO, consider writing these to the log file only once?
			final String java = System.getProperty("java.version");
			final String os = System.getProperty("os.name");
			final String version = Model.PROPERTIES
					.getProperty("project.version");
			final String buildId = Model.PROPERTIES.getProperty("buildId");
			final String txt = "Ver " + version + " (" + buildId + ") - Java "
					+ java + " , OS " + os;
			LOGGER.error(txt);

			final StringWriter strWriter = new StringWriter();
			final PrintWriter prWriter = new PrintWriter(strWriter);
			t.printStackTrace(prWriter);
			LOGGER.error(strWriter.toString());
		}
	}

	public static void clearLastThrowable() {
		m_lastLoggedThrowable = null;
	}

	protected void fireLessonLoaded(final Lesson lesson) {
		lesson.getRootCategory().addObserver(this);

		for (final LessonObserver observer : m_lessonObservers) {
			observer.lessonLoaded(lesson);
		}
	}

	protected void fireLessonClosed(final Lesson lesson) {
		lesson.getRootCategory().removeObserver(this);

		for (final LessonObserver observer : m_lessonObservers) {
			observer.lessonClosed(lesson);
		}
	}

	private void fireLessonModified(final Lesson lesson) {
		if (lesson.canSave()) {
			for (final LessonObserver observer : m_lessonObservers) {
				observer.lessonModified(lesson);
			}
		}
	}

	protected void startStats() {
		m_globalLearnHistory = new LearnHistory(STATS_FILE);
	}
}
