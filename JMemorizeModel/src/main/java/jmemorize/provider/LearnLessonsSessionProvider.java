package jmemorize.provider;

import jmemorize.core.CategoryObserver;
import jmemorize.core.LessonProvider;
import jmemorize.core.learn.LearnSessionProvider;

public interface LearnLessonsSessionProvider extends LearnSessionProvider,
		LessonProvider, CategoryObserver {

}
