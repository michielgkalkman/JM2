package jmemorize.gui.swing.actions.file;

import java.io.File;

import jmemorize.core.Lesson;

public interface JMemorizeUI {

	File determineLessonFile();

	void show(Lesson lesson);

}
