package jmemorize.gui.swing.actions.file;

import java.io.File;

import jmemorize.core.Lesson;
import jmemorize.core.io.JmlUI;

public interface JMemorizeUI extends JmlUI {

	File determineLessonFileToOpen();

	void show(Lesson lesson);

}
