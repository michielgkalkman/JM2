package jmemorize.core.io;

import java.io.File;
import java.io.IOException;

import jmemorize.core.Lesson;

public interface JMemorizeIO {
	public void load(final File file, final Lesson lesson) throws IOException;
	
	public void save(final File file, final Lesson lesson) throws IOException;

	public void exportLesson(Lesson cleanLesson, File file);

	public void saveLesson(Lesson lesson);

	public void loadFromXMLFile(File file, Lesson lesson);

	public void loadLesson(Object object);

	public void loadRecentLesson(int m_id);
	
}
