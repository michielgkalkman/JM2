package jmemorize.core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SearchToolTest {
	@Test
	public void testSearch() {
		final String text = "hallo";
		final boolean matchCase = true;
		final List<Card> cards = new ArrayList<>();

		{
			final List<Card> searchResults = SearchTool.search(text, SearchTool.BOTH_SIDES, matchCase, cards);
			assertEquals(0, searchResults.size());
		}

		final Card firstCard = new Card( "hallo1", "hallo2");
		final Card secondCard = new Card( "test1", "test2");

		cards.add(firstCard);
		cards.add(secondCard);

		{
			final List<Card> searchResults = SearchTool.search("hallo", SearchTool.BOTH_SIDES, true, cards);
			assertEquals(1, searchResults.size());
		}
		
		{
			final List<Card> searchResults = SearchTool.search("hallo", SearchTool.FRONT_SIDE, true, cards);
			assertEquals(1, searchResults.size());
		}
		
		{
			final List<Card> searchResults = SearchTool.search("HALLO", SearchTool.BOTH_SIDES, false, cards);
			assertEquals(1, searchResults.size());
		}
		
		{
			final List<Card> searchResults = SearchTool.search("HALLO", SearchTool.FRONT_SIDE, false, cards);
			assertEquals(1, searchResults.size());
		}
		
		{
			final List<Card> searchResults = SearchTool.search("HALLO", SearchTool.BOTH_SIDES, true, cards);
			assertEquals(0, searchResults.size());
		}
		
		{
			final List<Card> searchResults = SearchTool.search("HALLO", SearchTool.FRONT_SIDE, true, cards);
			assertEquals(0, searchResults.size());
		}
		
		{
			final List<Card> searchResults = SearchTool.search("hallo1", SearchTool.FLIP_SIDE, true, cards);
			assertEquals(0, searchResults.size());
		}
		
		{
			final List<Card> searchResults = SearchTool.search("HALLO1", SearchTool.FRONT_SIDE, false, cards);
			assertEquals(1, searchResults.size());
		}
	}
}
