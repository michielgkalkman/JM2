package jmemorize.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jmemorize.core.Lesson;
import jmemorize.core.media.MediaRepository;

public class JmlIO implements JMemorizeIO {

	@Override
	public void load(final File file, final Lesson lesson) throws IOException {
		try {
			MediaRepository.getInstance().clear();

//			final Lesson lesson = new Lesson(false);
			XmlBuilder.loadFromXMLFile(file, lesson);
			lesson.setFile(file);
			lesson.setCanSave(false);
//			m_recentFiles.push(file.getAbsolutePath());

//			setLesson(lesson);
			// startExpirationTimer(); TODO expiration timer
		} catch (final Exception e) {
//			m_recentFiles.remove(file.getAbsolutePath());
//			logThrowable("Error loading lesson", e);
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public void save( final File file, final Lesson lesson)
			throws IOException {
		try {
			final File tempFile = new File(file.getAbsolutePath() + "~"); //$NON-NLS-1$
			XmlBuilder.saveAsXMLFile(tempFile, lesson);

			file.delete();
			copyFile(tempFile, file);

			lesson.setFile(file); // note: sets file only if no exception
			lesson.setCanSave(false);
//			m_recentFiles.push(file.getAbsolutePath());

//			for (final LessonObserver observer : m_lessonObservers) {
//				observer.lessonSaved(lesson);
//			}
		} catch (final Throwable t) {
			throw new IOException(t.getMessage());
		}
	}
	
	private static void copyFile(final File in, final File out)
			throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;
		try {
			sourceChannel = new FileInputStream(in).getChannel();
			destinationChannel = new FileOutputStream(out).getChannel();

			sourceChannel.transferTo(0, sourceChannel.size(),
					destinationChannel);
		} finally {
			if (sourceChannel != null)
				sourceChannel.close();

			if (destinationChannel != null)
				destinationChannel.close();
		}
	}

	@Override
	public void exportLesson(Lesson cleanLesson, File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveLesson(Lesson lesson) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFromXMLFile(File file, Lesson lesson) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadLesson(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadRecentLesson(int m_id) {
		// TODO Auto-generated method stub
		
	}
}
