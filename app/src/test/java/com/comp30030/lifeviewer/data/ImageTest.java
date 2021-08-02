package com.comp30030.lifeviewer.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ImageTest {

    private static final String  TEST_PATH                = "primary/test/path";
    private static final long    TEST_DATE_MODIFIED       = 12;
    private static final boolean TEST_IS_FAVOURITE_FALSE  = false;
    private static final boolean TEST_IS_FAVOURITE_TRUE   = true;

    private Image imageIsFavouriteFalse;
    private Image imageIsFavouriteTrue;

    @Before
    public void setup() {
        imageIsFavouriteFalse = new Image(TEST_PATH, TEST_DATE_MODIFIED,
                                          TEST_IS_FAVOURITE_FALSE);
        imageIsFavouriteTrue = new Image(TEST_PATH, TEST_DATE_MODIFIED,
                                         TEST_IS_FAVOURITE_TRUE);
    }

    @Test
    public void getPathTest() {
        assertEquals(TEST_PATH, imageIsFavouriteFalse.getPath());
    }

    @Test
    public void getDateModifiedTest() {
        assertEquals(TEST_DATE_MODIFIED, imageIsFavouriteFalse.getDateModified());
    }

    @Test
    public void getIsFavouriteFalseTest() {
        assertEquals(TEST_IS_FAVOURITE_FALSE, imageIsFavouriteFalse.getIsFavourite());
    }

    @Test
    public void getIsFavouriteTrueTest() {
        assertEquals(TEST_IS_FAVOURITE_TRUE, imageIsFavouriteTrue.getIsFavourite());
    }

    @Test
    public void toggleIsFavouriteStartFalseTest() {
        assertEquals(TEST_IS_FAVOURITE_FALSE, imageIsFavouriteFalse.getIsFavourite());
        imageIsFavouriteFalse.toggleIsFavourite();
        assertEquals(TEST_IS_FAVOURITE_TRUE, imageIsFavouriteFalse.getIsFavourite());
    }

    @Test
    public void toggleIsFavouriteStartTrueTest() {
        assertEquals(TEST_IS_FAVOURITE_TRUE, imageIsFavouriteTrue.getIsFavourite());
        imageIsFavouriteTrue.toggleIsFavourite();
        assertEquals(TEST_IS_FAVOURITE_FALSE, imageIsFavouriteTrue.getIsFavourite());
    }

    @Test
    public void equalsDifferentObjectTypesTest() {
        assertNotEquals(imageIsFavouriteFalse, new Object());
    }

    @Test
    public void equalsSameTypeDifferentObjectsTest() {
        assertNotEquals(imageIsFavouriteFalse, imageIsFavouriteTrue);
    }

    @Test
    public void equalsTwoOfSameObjectTest() {
        assertEquals(imageIsFavouriteFalse, new Image(TEST_PATH, TEST_DATE_MODIFIED,
                                                         TEST_IS_FAVOURITE_FALSE));
    }

    @Test
    public void equalsSameObjectTest() {
        assertEquals(imageIsFavouriteFalse, imageIsFavouriteFalse);
    }
}
