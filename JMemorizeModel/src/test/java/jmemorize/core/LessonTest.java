package jmemorize.core;

import static jmemorize.util.Tap.tap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LessonTest {

    @Test
    public void cloneCategoriesTest() {
        final boolean canSave = true;
        final Lesson lesson = new Lesson(canSave);

        final Category category1 = tap(new Category("category1"), category -> category.addCard(new Card("1", "1")));
        final Category category3 = tap(new Category("category3"), category -> category.addCard(new Card("3", "3")));
        tap(lesson.getRootCategory(), root -> {
            root.addCard(new Card("0", "0"));
            root.addCategoryChild(category1);
            root.addCategoryChild(tap(new Category("category2"), category -> category.addCard(new Card("2", "2"))));
            root.addCategoryChild(category3);
        });

        assertTrue(lesson.canSave());

        lesson.setCanSave(false);

        assertFalse(lesson.canSave());

        {
            final Lesson clone = Lesson.cloneLesson(category1);

            assertEquals(lesson.getRootCategory(), clone.getRootCategory());
            assertEquals(0, clone.getRootCategory().getLocalCards().size());
            final Category childCategory = clone.getRootCategory().getChildCategory("category1");
            assertNotNull(childCategory);
            assertEquals(1, childCategory.getCardCount());
            assertEquals("1", childCategory.getCards().get(0).getFrontSide().toString());
            assertNull(clone.getRootCategory().getChildCategory("category2"));

            assertTrue(clone.canSave());
        }

        {
            final Lesson clone = Lesson.cloneLesson(category1, category3);

            assertEquals(lesson.getRootCategory(), clone.getRootCategory());
            assertEquals(0, clone.getRootCategory().getLocalCards().size());
            {
                final Category childCategory = clone.getRootCategory().getChildCategory("category1");
                assertNotNull(childCategory);
                assertEquals(1, childCategory.getLocalCards().size());
            }
            {
                final Category childCategory = clone.getRootCategory().getChildCategory("category2");
                assertNull(childCategory);
            }
            {
                final Category childCategory = clone.getRootCategory().getChildCategory("category3");
                assertNotNull(childCategory);
                assertEquals(1, childCategory.getLocalCards().size());
            }

            assertTrue(clone.canSave());
        }

        {
            final Lesson clone = lesson.cloneWithoutProgress();

            final Category rootCategory2 = clone.getRootCategory();

            assertCategoryWithoutProgress(rootCategory2);

            assertTrue(clone.canSave());
        }
    }

    private void assertCategoryWithoutProgress(final Category category) {
        category.getChildCategories().forEach(c -> assertCategoryWithoutProgress(c));

        category.getCards().forEach(c -> assertCardWithoutProgress(c));
    }

    private void assertCardWithoutProgress(final Card c) {
        assertEquals(0, c.getTestsTotal());
    }

    @Test
    public void equalsTest() {
        final boolean canSave = true;
        final Lesson lesson = new Lesson(canSave);

        final Category rootCategory = lesson.getRootCategory();
        rootCategory.addCard(new Card("0", "0"));
        final Category category1 = new Category("category1");
        category1.addCard(new Card("1", "1"));
        rootCategory.addCategoryChild(category1);
        final Category category2 = new Category("category2");
        category2.addCard(new Card("2", "2"));
        rootCategory.addCategoryChild(category2);
        final Category category3 = new Category("category3");
        category3.addCard(new Card("3", "3"));
        category2.addCategoryChild(category3);

        {
            final Lesson clone = lesson.cloneWithoutProgress();

            assertEquals(clone, lesson);
        }

        {
            final Lesson clone = Lesson.cloneLesson(rootCategory, category1, category2, category3);

            assertEquals(clone, lesson);
        }
    }

    @Test
    public void addCategoryTest() {
        final Category rootCatagory = new Category("someRootCategory");
        final Lesson lesson = new Lesson(rootCatagory, true);

        final Category childCategory = new Category("someChildCategory");
        rootCatagory.addCategoryChild(childCategory);

        assertEquals(childCategory, lesson.getRootCategory().getChildCategories().get(0));
    }

}
