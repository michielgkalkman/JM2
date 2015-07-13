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
package jmemorize.gui.swing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import jmemorize.core.Lesson;
import jmemorize.core.Localization;
import jmemorize.core.Settings;
import jmemorize.gui.swing.frames.MainFrame;
import jmemorize.provider.DefaultLearnSessionProviderImpl;
import jmemorize.util.TimeSpan;

/**
 * The main class of the application.
 * 
 * @author djemili
 */
public class Main extends DefaultLearnSessionProviderImpl {

	// simple logging support
	private static final Logger logger = Logger.getLogger("jmemorize");

	private static Main m_instance;

	private MainFrame m_frame;

	/**
	 * @return the singleton instance of Main.
	 */
	public static Main getInstance() {
		if (m_instance == null) {
			m_instance = new Main();
		}

		return m_instance;
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

		if (m_frame != null) // TODO remove call
		{
			m_frame.setLesson(lesson);
		}

		fireLessonLoaded(lesson);
	}

	/**
	 * @return the main frame.
	 */
	public MainFrame getFrame() {
		return m_frame;
	}

	public Main() {
		InputStream propertyStream = null;

		try {
			// TODO - make this adjustable
			// Note that the limit might not be enough for finer.
			final Handler fh = new FileHandler("%t/jmemorize%g.log", 10000, 3);
			fh.setLevel(Level.WARNING);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
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

	private void run(final File file) {
		createNewLesson();
		startStats();

		m_frame = new MainFrame();
		m_learnSettings = Settings.loadStrategy();
		m_frame.setVisible(true);

//		if (file != null) {
//			m_frame.loadLesson(file);
//		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(final String args[]) {
		final File file = args.length >= 1 ? new File(args[0]) : null;
		Main.getInstance().run(file);
	}

	/**
	 * @return A string that represents the timespan in a nice and readable
	 *         format.
	 */
	public static String format(final Date dateNow, final Date dateOther) {
		final TimeSpan span = new TimeSpan(dateNow, dateOther);
		final StringBuffer result = new StringBuffer();

		// if one or more days left
		final long d = Math.abs(span.getDays());
		if (d >= 1) {
			result.append(d == 1 ? Localization.get("Time.ONE_DAY") : //$NON-NLS-1$
					d + " " + Localization.get("Time.DAYS")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// if today
		else {
			final int h = Math.abs(span.getHours());
			if (h >= 1) {
				result.append(h == 1 ? Localization.get("Time.ONE_HOUR") : //$NON-NLS-1$
						h + " " + Localization.get("Time.HOURS")); //$NON-NLS-1$ //$NON-NLS-2$ 
			}
			// if less then one hour left
			else {
				final int m = Math.abs(span.getMinutes());
				result.append(m == 1 ? Localization.get("Time.ONE_MINUTE") : //$NON-NLS-1$
						m + " " + Localization.get("Time.MINUTES")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		if (span.getTicks() >= 0) // future
		{
			final MessageFormat form = new MessageFormat(
					Localization.get("Time.IN")); //$NON-NLS-1$
			return form.format(new Object[] { result.toString() });
		} else // past
		{
			final MessageFormat form = new MessageFormat(
					Localization.get("Time.AGO")); //$NON-NLS-1$
			return form.format(new Object[] { result.toString() });
		}
	}
}
