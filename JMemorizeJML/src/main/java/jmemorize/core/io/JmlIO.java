package jmemorize.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jmemorize.core.Lesson;
import jmemorize.core.media.MediaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JmlIO implements JMemorizeIO {

	// TODO: needs to be more abstract - could also be db or some other storage
	public File file;
	private final JmlUI jMemorizeUI;

	public JmlIO(final JmlUI jmlUI) {
		this.jMemorizeUI = jmlUI;
	}

	@Override
	public void load(final File file, final Lesson lesson) throws IOException {
		try {
			MediaRepository.getInstance().clear();

			// final Lesson lesson = new Lesson(false);
			XmlBuilder.loadFromXMLFile(file, lesson);
			lesson.setCanSave(false);
			// m_recentFiles.push(file.getAbsolutePath());

			// setLesson(lesson);
			// startExpirationTimer(); TODO expiration timer

			this.file = file;

		} catch (final Exception e) {
			// m_recentFiles.remove(file.getAbsolutePath());
			// logThrowable("Error loading lesson", e);

			log.error(e.getMessage(), e);
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public void save(final File file, final Lesson lesson) throws IOException {
		try {
			final File tempFile = new File(file.getAbsolutePath() + "~"); //$NON-NLS-1$
			XmlBuilder.saveAsXMLFile(tempFile, lesson);

			file.delete();
			copyFile(tempFile, file);

			lesson.setCanSave(false);
			// m_recentFiles.push(file.getAbsolutePath());

			// for (final LessonObserver observer : m_lessonObservers) {
			// observer.lessonSaved(lesson);
			// }

			this.file = file;
		} catch (final Throwable t) {
			throw new IOException(t.getMessage());
		}
	}

	private static void copyFile(final File in, final File out) throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;
		try {
			sourceChannel = new FileInputStream(in).getChannel();
			destinationChannel = new FileOutputStream(out).getChannel();

			sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		} finally {
			if (sourceChannel != null)
				sourceChannel.close();

			if (destinationChannel != null)
				destinationChannel.close();
		}
	}

	@Override
	public void exportLesson(final Lesson cleanLesson, final File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveLesson(final Lesson lesson) throws IOException {
		if (file == null) {
			file = jMemorizeUI.determineLessonFileToSave();
		}
		if (file != null) {
			save(file, lesson);
		}
	}

	@Override
	public void loadFromXMLFile(final File file, final Lesson lesson) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadLesson(final Lesson lesson) throws IOException {
		load(file, lesson);
	}

	@Override
	public void loadRecentLesson(final int m_id) {
		// TODO Auto-generated method stub

	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void reset() {
		file = null;
	}
}
