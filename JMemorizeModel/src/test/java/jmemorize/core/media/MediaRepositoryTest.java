package jmemorize.core.media;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import javax.swing.ImageIcon;

import org.junit.Test;

public class MediaRepositoryTest {
	@Test
	public void testMediaRepository() throws IOException {
		MediaRepository mediaRepository = MediaRepository.getInstance();
		assertNull( mediaRepository.getImage("abc"));
		// Test mediarepository is a singleton.
		// TODO: mediarepo MUST not be a singleton
		MediaRepository mediaRepository2 = MediaRepository.getInstance();
		assertTrue( mediaRepository == mediaRepository2);
		
		ImageIcon imageIcon = new ImageIcon(MediaRepositoryTest.class.getResource("/jmemorize/core/media/windmolen.jpg"));
		mediaRepository.addImage(imageIcon );
		
		String imageId = "windmolen.jpg";
		
		{
			Set<String> keys = mediaRepository.getKeys();
			
			assertEquals( 1, keys.size());
			assertEquals( imageId, keys.iterator().next());
		}
		
		ImageIcon imageIcon2 = mediaRepository.getImage(imageId);
		
		assertNotNull(imageIcon2);
		assertEquals(imageIcon2.getIconHeight(), imageIcon.getIconHeight());
		assertEquals(imageIcon2.getIconWidth(), imageIcon.getIconWidth());
		assertEquals(imageIcon2.getDescription(), imageIcon.getDescription());
		
		{
			Set<String> keys = mediaRepository.getKeys();
			
			assertEquals( 1, keys.size());
			assertEquals( imageId, keys.iterator().next());
		}
		

	}
}
