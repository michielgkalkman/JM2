package jmemorize.core.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import jmemorize.core.Card;
import jmemorize.core.CardSide;
import jmemorize.core.Category;
import jmemorize.core.FormattedText;
import jmemorize.core.Lesson;
import jmemorize.core.io.XmlBuilder;
import jmemorize.core.media.MediaRepository;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

public class ReadJML {

	@Test
	public void testReadJmlWithImage() throws URISyntaxException, SAXException,
			IOException, ParserConfigurationException, TransformerException {
		final File file = new File(ReadJML.class.getResource("/picture.jml")
				.toURI());

		final Lesson lesson = new Lesson(false);
		XmlBuilder.loadFromXMLFile(file, lesson);

		final Category rootCategory = lesson.getRootCategory();

		final List<Card> rootCategoryCards = rootCategory.getCards();
		assertEquals(1, rootCategoryCards.size());

		final Card card = rootCategoryCards.get(0);

		{
			final CardSide frontSide = card.getFrontSide();
			final List<String> media = frontSide.getMedia();
			assertEquals(1, media.size());
			assertEquals("BerichtHandlers.gif", media.get(0));

			final FormattedText formattedText = frontSide.getText();
			assertEquals("", formattedText.getFormatted());
			assertEquals("", formattedText.getUnformatted());
			final StyledDocument document = formattedText.getDocument();
			assertEquals(0, document.getLength());
		}

		final CardSide backSide = card.getBackSide();
		final List<String> media = backSide.getMedia();
		assertTrue(media.isEmpty());

		final FormattedText formattedText = backSide.getText();
		assertEquals("Do you see a picture?", formattedText.getFormatted());
		assertEquals("Do you see a picture?", formattedText.getUnformatted());
		final StyledDocument document = formattedText.getDocument();
		assertEquals(21, document.getLength());

		// Save it.
		final File testDir = new File(SystemUtils.getJavaIoTmpDir(),
				"jmemorize-tests");
		testDir.mkdirs();
		final File file2 = new File(testDir, "picture2.jml");

		System.out.println("Write to " + file2.getAbsolutePath());

		XmlBuilder.saveAsXMLFile(file2, lesson);
	}

	@Test
	public void test() throws URISyntaxException, SAXException, IOException,
			ParserConfigurationException, TransformerException {
		final File file = new File(ReadJML.class.getResource(
				"/AlgemeneOpleidingBankbedrijf.jml").toURI());

		final Lesson lesson = new Lesson(false);
		XmlBuilder.loadFromXMLFile(file, lesson);

		final Category rootCategory = lesson.getRootCategory();

		assertEquals(43, rootCategory.getCardCount());
		assertEquals(0, rootCategory.getDepth());
		assertEquals(3, rootCategory.getNumberOfDecks());
		assertEquals("All", rootCategory.getPath());
		assertNull(rootCategory.getParent());
		assertEquals(12, rootCategory.getCardCount(1));

		{
			final String expected = "Wat is een andere term voor assuradeur?Wat zijn de regels voor cold calling?Noem 4 soorten verzekeringen die via het directe distributiekanaal worden afgezetWelke typen tussenpersonen en agenten zijn actief op de markt voor verzekeringsproducten?Noem de eigenschappen van een verbonden assurantiepersoonNoem eigenschappen van een gevolmachtigd agentNoem de eigenschappen van een gebonden assurantietussenpersoonWelke creditcard maatschappij maakt gebruik van directe distributie?Hoe bieden MasterCard en Visa hun producten aan?Wat is een financieringsmaatschappij?Wat betekent het dat een financieringsmaatschappij geen bankvergunning heeft?Hoe worden financieringsmaatschappijen vaak ingezet?Noem drie voorbeelden van bedrijven met een eigen financieringsdochterWaar staat GKB voor?Hoe treedt de overheid op als kredietverstrekker?Wat is de functie van GKB's?Aan wat voor soort mensen verstrekken GKBs krediet?Welke soort kredietbemiddelaars zijn er?Wat is de taak van een professionele kredietbemiddelaar?Wat betekent het als een professionele kredietbemiddelaar een volmacht krijgt van een financieringsmaatschappij?Wat is het verschil tussen assurantietussenpersonen en professionel kredietbemiddelaars?Hoe treden leveranciers van duurzame consumptiegoederen op als kredietbemiddelaars?Noem voorbeelden van leveranciers van duurzame consumptiegoederen die als kredietbemiddelaar optredenHoe werken winkelketens, warenhuizen en postorderbedrijven als kredietbemiddelaars?Waarvoor wordt hypothecair krediet gebruikt?Wat is het recht van hypotheek?Wat is indirecte distributie?Wat wordt met uitsluiting bedoeld?Wat wordt met insluiting bedoeld?Waar staat Wft voor?Wanneer is indirecte distribute geschikt?Noem een voorbeeld van een financieel product waarvoor indirecte distributie gehanteerd wordtWat is direct writing?Wanneer kies je voor directe distributie?Welke soorten tussenpersonen wordt door de Wet op het financieel toezicht onderkend?Wat is een andere naam voor een gevolmachtigd agentHoeveel % van zorgverzekeringen wordt via het direct distributiekanaal afgezet?Noem de eigenschappen van een ongebonden assurantietussenpersoonWat is het marktaandeel van direct writing?Wat is cold calling?Noem een voorbeeld waarin een combinatie van directe en indirecte distributie noodzakelijk isWat zijn voorbeelden van producten die via directe distribute verkocht worden?\nWat is directe distributie?";
			final List<Card> cards = rootCategory.getCards();

			assertEquals(expected, getCardString(cards));
		}

		{
			final String expected = "Wat is een andere term voor assuradeur?Wat zijn de regels voor cold calling?Noem 4 soorten verzekeringen die via het directe distributiekanaal worden afgezetWelke typen tussenpersonen en agenten zijn actief op de markt voor verzekeringsproducten?Noem de eigenschappen van een verbonden assurantiepersoonNoem eigenschappen van een gevolmachtigd agentNoem de eigenschappen van een gebonden assurantietussenpersoonWelke creditcard maatschappij maakt gebruik van directe distributie?Hoe bieden MasterCard en Visa hun producten aan?Wat is een financieringsmaatschappij?Wat betekent het dat een financieringsmaatschappij geen bankvergunning heeft?Hoe worden financieringsmaatschappijen vaak ingezet?Noem drie voorbeelden van bedrijven met een eigen financieringsdochterWaar staat GKB voor?Hoe treedt de overheid op als kredietverstrekker?Wat is de functie van GKB's?Aan wat voor soort mensen verstrekken GKBs krediet?Welke soort kredietbemiddelaars zijn er?Wat is de taak van een professionele kredietbemiddelaar?Wat betekent het als een professionele kredietbemiddelaar een volmacht krijgt van een financieringsmaatschappij?Wat is het verschil tussen assurantietussenpersonen en professionel kredietbemiddelaars?Hoe treden leveranciers van duurzame consumptiegoederen op als kredietbemiddelaars?Noem voorbeelden van leveranciers van duurzame consumptiegoederen die als kredietbemiddelaar optredenHoe werken winkelketens, warenhuizen en postorderbedrijven als kredietbemiddelaars?Waarvoor wordt hypothecair krediet gebruikt?Wat is het recht van hypotheek?Wat is indirecte distributie?Wat wordt met uitsluiting bedoeld?Wat wordt met insluiting bedoeld?Waar staat Wft voor?Wanneer is indirecte distribute geschikt?Noem een voorbeeld van een financieel product waarvoor indirecte distributie gehanteerd wordtWat is direct writing?Wanneer kies je voor directe distributie?Welke soorten tussenpersonen wordt door de Wet op het financieel toezicht onderkend?Wat is een andere naam voor een gevolmachtigd agentHoeveel % van zorgverzekeringen wordt via het direct distributiekanaal afgezet?Noem de eigenschappen van een ongebonden assurantietussenpersoonWat is het marktaandeel van direct writing?Wat is cold calling?Noem een voorbeeld waarin een combinatie van directe en indirecte distributie noodzakelijk isWat zijn voorbeelden van producten die via directe distribute verkocht worden?\nWat is directe distributie?";
			final List<Card> cards = rootCategory.getLearnableCards();

			assertEquals(expected, getCardString(cards));
		}

		{
			final String expected = "";
			final List<Card> cards = rootCategory.getLearnedCards();

			assertEquals(expected, getCardString(cards));
		}

		{
			final String expected = "Wat is indirecte distributie?Wat wordt met uitsluiting bedoeld?Wat wordt met insluiting bedoeld?Waar staat Wft voor?Wanneer is indirecte distribute geschikt?Noem een voorbeeld van een financieel product waarvoor indirecte distributie gehanteerd wordtWat is direct writing?Wanneer kies je voor directe distributie?Welke soorten tussenpersonen wordt door de Wet op het financieel toezicht onderkend?Wat is een andere naam voor een gevolmachtigd agentHoeveel % van zorgverzekeringen wordt via het direct distributiekanaal afgezet?Noem de eigenschappen van een ongebonden assurantietussenpersoonWat is het marktaandeel van direct writing?Wat is cold calling?Noem een voorbeeld waarin een combinatie van directe en indirecte distributie noodzakelijk isWat zijn voorbeelden van producten die via directe distribute verkocht worden?\nWat is directe distributie?";
			final List<Card> cards = rootCategory.getExpiredCards();

			assertEquals(expected, getCardString(cards));
		}

		{
			final String expected = "Wat is een andere term voor assuradeur?";
			final List<Card> cards = rootCategory.getLocalCards();

			assertEquals(expected, getCardString(cards));
		}

		{
			final String expected = "Wat is een andere term voor assuradeur?Wat zijn de regels voor cold calling?Noem 4 soorten verzekeringen die via het directe distributiekanaal worden afgezetWelke typen tussenpersonen en agenten zijn actief op de markt voor verzekeringsproducten?Noem de eigenschappen van een verbonden assurantiepersoonNoem eigenschappen van een gevolmachtigd agentNoem de eigenschappen van een gebonden assurantietussenpersoonWelke creditcard maatschappij maakt gebruik van directe distributie?Hoe bieden MasterCard en Visa hun producten aan?Wat is een financieringsmaatschappij?Wat betekent het dat een financieringsmaatschappij geen bankvergunning heeft?Hoe worden financieringsmaatschappijen vaak ingezet?Noem drie voorbeelden van bedrijven met een eigen financieringsdochterWaar staat GKB voor?Hoe treedt de overheid op als kredietverstrekker?Wat is de functie van GKB's?Aan wat voor soort mensen verstrekken GKBs krediet?Welke soort kredietbemiddelaars zijn er?Wat is de taak van een professionele kredietbemiddelaar?Wat betekent het als een professionele kredietbemiddelaar een volmacht krijgt van een financieringsmaatschappij?Wat is het verschil tussen assurantietussenpersonen en professionel kredietbemiddelaars?Hoe treden leveranciers van duurzame consumptiegoederen op als kredietbemiddelaars?Noem voorbeelden van leveranciers van duurzame consumptiegoederen die als kredietbemiddelaar optredenHoe werken winkelketens, warenhuizen en postorderbedrijven als kredietbemiddelaars?Waarvoor wordt hypothecair krediet gebruikt?Wat is het recht van hypotheek?";
			final List<Card> cards = rootCategory.getUnlearnedCards();

			assertEquals(expected, getCardString(cards));
		}

		{
			final StringBuilder stringBuilder = new StringBuilder();
			dumpCategories(stringBuilder, rootCategory);
			System.out.println(stringBuilder);
		}

		{
			final StringBuilder stringBuilder = new StringBuilder();
			dumpCategories(stringBuilder, rootCategory, 0, "--", "+");
			Assert.assertEquals(
					"All+--Algemene Opleiding Bankbedrijf+----Aanbieders en distributiekanalen+------3.1 Distributie van Verzekeringen+------4 Distibutie van consumptieve kredieten+------Directe distributie hypothecair krediet+",
					stringBuilder.toString());
		}

		// Save it.
		final String tempSubDir = "jmemorize-tests";
		final String fileName = "simpleSave.jml";
		final File file2 = saveAsXMLFile(lesson, tempSubDir, fileName);

		final Lesson newLesson = new Lesson(true);
		XmlBuilder.loadFromXMLFile(file2, newLesson);

		assertEquals(lesson, newLesson);
	}

	private File saveAsXMLFile(final Lesson lesson, final String tempSubDir,
			final String fileName) throws IOException, TransformerException,
			ParserConfigurationException {
		final File file2;
		final File testDir = new File(SystemUtils.getJavaIoTmpDir(), tempSubDir);
		testDir.mkdirs();

		file2 = new File(testDir, fileName);

		System.out.println("Write to " + file2.getAbsolutePath());

		XmlBuilder.saveAsXMLFile(file2, lesson);
		return file2;
	}

	@Test
	@Ignore
	public void testImage() throws IOException, TransformerException,
			ParserConfigurationException, SAXException {
		final File xmlFile;
		final ImageIcon imageIcon;

		final MediaRepository mediaRepository = MediaRepository.getInstance();
		{
			final Lesson lesson = new Lesson(true);
			final CardSide frontSide = new CardSide(new FormattedText());
			final CardSide backSide = new CardSide(new FormattedText());
			final List<String> ids = new ArrayList<>();
			final BufferedImage image = new BufferedImage(100, 50,
					BufferedImage.TYPE_INT_ARGB);
			imageIcon = new ImageIcon(image);
			imageIcon.setDescription("::filename.jpeg");

			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, "jpeg", os);
			final InputStream is = new ByteArrayInputStream(os.toByteArray());

			ids.add(mediaRepository.addImage(is, "filename.jpeg"));

			// ids.add(mediaRepository.addImage( imageIcon));
			frontSide.setMedia(ids);
			final Card card = new Card(new Date(), frontSide, backSide);
			lesson.getRootCategory().addCard(card);

			// Save it.
			final String tempSubDir = "jmemorize-tests";
			final String fileName = "imageSave.jml";

			xmlFile = saveAsXMLFile(lesson, tempSubDir, fileName);
		}

		mediaRepository.clear();

		final Lesson lesson = new Lesson(true);
		XmlBuilder.loadFromXMLFile(xmlFile, lesson);

		final CardSide frontSide = lesson.getRootCategory().getCards()
				.iterator().next().getFrontSide();
		final List<String> media = frontSide.getMedia();

		mediaRepository.toImageIcons(media);

		final ImageIcon imageIconRead = mediaRepository.getImage(media.get(0));

		assertEquals(imageIcon, imageIconRead);
	}

	@Test
	public void testImage2() throws IOException, TransformerException,
			ParserConfigurationException, SAXException {
		final File xmlFile;
		final File tempFile;
		final ImageIcon imageIcon;

		final MediaRepository mediaRepository = MediaRepository.getInstance();
		{
			final Lesson lesson = new Lesson(true);
			final CardSide frontSide = new CardSide(new FormattedText());
			final CardSide backSide = new CardSide(new FormattedText());
			final List<String> ids = new ArrayList<>();
			final BufferedImage image = new BufferedImage(100, 50,
					BufferedImage.TYPE_INT_ARGB);
			assertNotNull(image.getGraphics());
			tempFile = File.createTempFile("testImage2", ".jpeg",
					SystemUtils.getJavaIoTmpDir());

			ImageIO.write(image, "jpeg", tempFile);

			imageIcon = new ImageIcon(tempFile.toURI().toURL());

			ids.add(mediaRepository.addImage(imageIcon));

			frontSide.setMedia(ids);
			final Card card = new Card(new Date(), frontSide, backSide);
			lesson.getRootCategory().addCard(card);

			// Save it.
			final String tempSubDir = "jmemorize-tests";
			final String fileName = "imageSave.jml";

			xmlFile = saveAsXMLFile(lesson, tempSubDir, fileName);
		}

		mediaRepository.clear();

		final Lesson lesson = new Lesson(true);
		XmlBuilder.loadFromXMLFile(xmlFile, lesson);

		final CardSide frontSide = lesson.getRootCategory().getCards()
				.iterator().next().getFrontSide();
		final List<String> media = frontSide.getMedia();

		mediaRepository.toImageIcons(media);

		final ImageIcon imageIconRead = mediaRepository.getImage(media.get(0));

		final BufferedImage read = ImageIO.read(tempFile);

		System.out.println(imageIconRead.getImage().getClass().getName());

		// assertEquals(imageIconRead.getImage().getGraphics(),
		// read.getGraphics());

		assertEquals(imageIcon.getIconWidth(), imageIconRead.getIconWidth());
		// assertEquals(imageIcon.getIconHeight(),
		// imageIconRead.getIconHeight());
		// assertEquals(imageIcon.getDescription(),
		// imageIconRead.getDescription());
		// assertEquals(imageIcon.getImageLoadStatus(),
		// imageIconRead.getImageLoadStatus());
		// assertEquals(imageIcon.getImageObserver(),
		// imageIconRead.getImageObserver());
		// final Image image = imageIcon.getImage();
		// final Image imageRead = imageIconRead.getImage();
		// assertEquals(image.getGraphics(), imageRead.getGraphics());
		// assertEquals(imageIcon.getImage()., imageIconRead.getImage());
		// assertEquals(imageIcon, imageIconRead);
	}

	private String getCardString(final List<Card> cards) {
		final StringBuilder stringBuilder = new StringBuilder();
		for (final Card card : cards) {
			stringBuilder.append(card.getFrontSide().getText());
		}
		return stringBuilder.toString();
	}

	private void dumpCategories(final StringBuilder stringBuilder,
			final Category category) {
		dumpCategories(stringBuilder, category, 0);
	}

	private void dumpCategories(final StringBuilder stringBuilder,
			final Category category, final int depth) {
		final String bullet = "--";
		final String eol = "\n";
		dumpCategories(stringBuilder, category, depth, bullet, eol);
	}

	private void dumpCategories(final StringBuilder stringBuilder,
			final Category category, final int depth, final String bullet,
			final String eol) {
		stringBuilder.append(StringUtils.repeat(bullet, depth))
				.append(category.getName()).append(eol);
		final int newDepth = depth + 1;
		for (final Category childCategory : category.getChildCategories()) {
			dumpCategories(stringBuilder, childCategory, newDepth, bullet, eol);
		}
	}
}
