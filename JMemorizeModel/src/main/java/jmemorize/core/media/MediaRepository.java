/*
 * jMemorize - Learning made easy (and fun) - A Leitner flashcards tool
 * Copyright(C) 2004-2009 Riad Djemili and contributors
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
package jmemorize.core.media;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

public class MediaRepository {
	private static final int MAX_CACHED_IMAGES = 10;

	public static final String MEDIA_ID_PREFIX = "::";

	private static MediaRepository m_instance;

	private final Map<String, ImageItem> m_mediaMap = new HashMap<String, ImageItem>();
	private final LinkedList<ImageIcon> m_imageCache = new LinkedList<ImageIcon>();

	private static final Pattern FILE_PATTERN = Pattern.compile("(.*)_(\\d+)");

	/**
	 * The base class for all media types that can be associated with a card.
	 */
	public class MediaItem {
		private final String m_sourceFile;
		private final byte[] m_bytes;
		private final String m_id;

		public MediaItem(final InputStream in, final String filename)
				throws IOException {
			m_sourceFile = filename;
			m_id = createId(filename);
			m_bytes = readFile(in);
		}

		public String getId() {
			return m_id;
		}

		public String getFile() {
			return m_sourceFile;
		}

		public byte[] getBytes() {
			return m_bytes;
		}

		@Override
		public String toString() {
			return m_id;
		}

		private String createId(final String filename) {
			final int dotPos = filename.lastIndexOf(".");
			final String extension = filename.substring(dotPos);
			String purename = filename.substring(0, dotPos);

			while (getKeys().contains(purename + extension)) {
				int num = 0;

				final Matcher m = FILE_PATTERN.matcher(purename);
				if (m.matches() && m.groupCount() == 2) {
					num = Integer.valueOf(m.group(2));
					num++;

					purename = m.group(1);
				}

				purename = purename + "_" + num;
			}

			return purename + extension;
		}

		private byte[] readFile(final InputStream in) throws IOException {
			final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

			final byte[] bytes = new byte[1024];
			int numRead = 0;

			while ((numRead = in.read(bytes, 0, bytes.length)) >= 0) {
				bytesOut.write(bytes, 0, numRead);
			}

			return bytesOut.toByteArray();
		}
	}

	public class ImageItem extends MediaItem {
		public ImageItem(final InputStream in, final String filename)
				throws IOException {
			super(in, filename);
		}

		public ImageIcon getImage() {
			final ImageIcon image = new ImageIcon(getBytes());
			image.setDescription(MEDIA_ID_PREFIX + getId());

			return image;
		}
	}

	// TODO remove singleton pattern and make this referenced from lesson

	public static MediaRepository getInstance() {
		if (m_instance == null)
			m_instance = new MediaRepository();

		return m_instance;
	}

	public Set<String> getKeys() {
		return m_mediaMap.keySet();
	}

	public Collection<ImageItem> getImageItems() // TODO dont give imageItem to
													// outside
	{
		return m_mediaMap.values();
	}

	public ImageIcon getImage(final String imageId) {
		for (final ImageIcon icon : m_imageCache) {
			if (equals(icon, imageId)) {
				m_imageCache.remove(icon);
				m_imageCache.addFirst(icon);

				return icon;
			}
		}

		final ImageItem imageItem = m_mediaMap.get(imageId);

		if (imageItem == null)
			return null;

		final ImageIcon icon = imageItem.getImage();
		m_imageCache.addFirst(icon);

		if (m_imageCache.size() > MAX_CACHED_IMAGES) // HACK check for memory
														// usage instead
			m_imageCache.removeLast();

		return icon;
	}

	public String addImage(final InputStream in, final String filename)
			throws IOException {
		// TOOD check if image already in our map
		// for (ImageItem item : m_imageMap.values())
		// {
		// if (item.getFile().equals(filename))
		// return item.getId();
		// }

		final ImageItem item = new ImageItem(in, filename);
		final String id = item.getId();
		m_mediaMap.put(id, item);

		return id;
	}

	public String addImage(final ImageIcon icon) throws IOException {
		final String description = icon.getDescription();

		String id = "";
		if (description.startsWith(MEDIA_ID_PREFIX)) {
			id = description.substring(MEDIA_ID_PREFIX.length());
		} else {
			InputStream in;
			String name = "";

			try {
				final URL url = new URL(description);
				name = new File(url.getPath()).getName();
				in = url.openStream();
			} catch (final MalformedURLException ex) {
				name = new File(description).getName();
				in = new FileInputStream(description);

				// fallthrough expected
			}

			id = addImage(in, name);
			icon.setDescription(MEDIA_ID_PREFIX + id);
		}

		return id;
	}

	/**
	 * Converts the given list of image icons into a list of image IDs. This is
	 * done by using the description field of ImageIcon. If the image icon was
	 * already loaded from the image repository, the description will begin with
	 * IMG_ID_PREFIX, otherwise it will be a new image that wasn't added to the
	 * repository yet.
	 * 
	 * @throws IOException
	 */
	public List<String> addImages(final List<ImageIcon> images)
			throws IOException {
		final List<String> imageIDs = new LinkedList<String>();
		for (final ImageIcon icon : images) {
			imageIDs.add(addImage(icon));
		}

		return imageIDs;
	}

	/**
	 * Retains all images with given IDs. All other images are removed.
	 */
	public void retain(final Set<String> retainIDs) {
		final Set<String> toBeRemoved = new HashSet<String>(m_mediaMap.keySet());

		for (final String id : retainIDs)
			toBeRemoved.remove(id);

		for (final String id : toBeRemoved)
			m_mediaMap.remove(id);
	}

	public static boolean equals(final ImageIcon image, final String id) {
		final String description = image.getDescription();
		return (description.startsWith(MEDIA_ID_PREFIX) && description
				.substring(MEDIA_ID_PREFIX.length()).equals(id));
	}

	public static boolean equals(final List<ImageIcon> images,
			final List<String> ids) {
		if (images.size() != ids.size())
			return false;

		for (final ImageIcon icon : images) {
			String id = "";
			final String description = icon.getDescription();

			if (description.startsWith(MEDIA_ID_PREFIX)) {
				id = description.substring(MEDIA_ID_PREFIX.length());

				if (!ids.contains(id))
					return false;
			} else {
				return false;
			}
		}

		return true;
	}

	public List<ImageIcon> toImageIcons(final List<String> ids) {
		final List<ImageIcon> images = new LinkedList<ImageIcon>();

		if (ids == null)
			return images;

		for (final String id : ids) {
			final ImageIcon image = getImage(id);
			if (image != null)
				images.add(image);
		}

		return images;
	}

	public void clear() {
		m_mediaMap.clear();
	}

	private MediaRepository() // singleton
	{
	}
}
