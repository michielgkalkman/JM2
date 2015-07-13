package jmemorize.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jmemorize.core.learn.LearnSettings;

public class SettingsTest {
	@Test
	public void test() {

		final LearnSettings learnSettings = new LearnSettings();
		Settings.storeStrategy(learnSettings);
		
		final LearnSettings loadStrategy = Settings.loadStrategy();
		
		
		assertEquals(learnSettings.getAmountToTest(true), loadStrategy.getAmountToTest(true));
	}
}
