package com.comp30030.lifeviewer.data;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;


import com.comp30030.lifeviewer.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ImageDaoTest {

    private static final String  TEST_PATH          = "primary/test/path";
    private static final long    TEST_DATE_MODIFIED = 12;
    private static final boolean TEST_IS_FAVOURITE  = true;

    private static final Image testImage1
            = new Image(TEST_PATH, TEST_DATE_MODIFIED, TEST_IS_FAVOURITE);

    private static final Image testImage2
            = new Image(TEST_PATH + "/longer",
                        TEST_DATE_MODIFIED + 1, !TEST_IS_FAVOURITE);

    private static final Image testImage3
            = new Image(TEST_PATH + "/longer/again",
            TEST_DATE_MODIFIED + 2, TEST_IS_FAVOURITE);

    private static final Image testImage4
            = new Image(TEST_PATH + "/longer/again/even-more",
            TEST_DATE_MODIFIED + 3, TEST_IS_FAVOURITE);

    private static final Image testImage5
            = new Image(TEST_PATH + "/longer/mc-long",
            TEST_DATE_MODIFIED + 5, !TEST_IS_FAVOURITE);

    private AppDatabase db;
    private ImageDao imageDao;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        imageDao = db.imageDao();
    }

    @After
    public void teardown() {
        db.close();
    }

    @Test
    public void notInsertAndGetAllOrderByDateModifiedDescTest() throws InterruptedException {
        List<Image> images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertEquals(Collections.emptyList(), images);
    }

    @Test
    public void insertAndGetAllOrderByDateModifiedDescTest() throws InterruptedException {
        imageDao.insert(testImage1);
        List<Image> images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertNotNull(images);
        assertEquals(1, images.size());
        assertEquals(testImage1, images.get(0));
    }

    @Test
    public void insertMultipleTest() throws InterruptedException {
        imageDao.insert(testImage1);
        imageDao.insert(testImage2);
        imageDao.insert(testImage3);

        List<Image> images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertNotNull(images);
        assertEquals(3, images.size());
        assertTrue(images.containsAll(Arrays.asList(testImage1, testImage2, testImage3)));
    }

    @Test
    public void insertMultipleDescOrderTest() throws InterruptedException {
        imageDao.insert(testImage3);
        imageDao.insert(testImage1);
        imageDao.insert(testImage2);

        List<Image> images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertNotNull(images);
        assertEquals(3, images.size());
        assertTrue(images.containsAll(Arrays.asList(testImage1, testImage2, testImage3)));

        assertEquals(testImage3, images.get(0));
        assertEquals(testImage2, images.get(1));
        assertEquals(testImage1, images.get(2));
    }

    @Test
    public void insertMultipleOfTheSameTest() throws InterruptedException {
        imageDao.insert(testImage1);

        try {
            imageDao.insert(testImage1);
        }
        catch (Exception e) {
            assertTrue(e instanceof SQLiteConstraintException);
        }

        List<Image> images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertNotNull(images);
        assertEquals(1, images.size());
        assertEquals(testImage1, images.get(0));
    }

    @Test
    public void deleteAllWhileEmptyTest() throws InterruptedException {
        List<Image> images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertEquals(Collections.emptyList(), images);

        imageDao.deleteAll();

        images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertEquals(Collections.emptyList(), images);
    }

    @Test
    public void deleteAllTest() throws InterruptedException {
        List<Image> images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertEquals(Collections.emptyList(), images);

        imageDao.insert(testImage3);
        imageDao.insert(testImage1);
        imageDao.insert(testImage2);

        images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertNotNull(images);
        assertEquals(3, images.size());

        imageDao.deleteAll();

        images = LiveDataTestUtil.getValue(imageDao.getAllOrderByDateModifiedDesc());
        assertEquals(Collections.emptyList(), images);
    }

    @Test
    public void getFavouritesWhileEmptyTest() throws InterruptedException {
        List<Image> favouritesImages = LiveDataTestUtil.getValue(imageDao.getFavourites());
        assertEquals(Collections.emptyList(), favouritesImages);
    }

    @Test
    public void getFavouritesTest() throws InterruptedException {
        imageDao.insert(testImage1);
        imageDao.insert(testImage3);
        imageDao.insert(testImage4);

        List<Image> favouritesImages = LiveDataTestUtil.getValue(imageDao.getFavourites());
        assertNotNull(favouritesImages);
        assertEquals(3, favouritesImages.size());
    }

    @Test
    public void getFavouritesDescOrderTest() throws InterruptedException {
        imageDao.insert(testImage1);
        imageDao.insert(testImage3);
        imageDao.insert(testImage4);

        List<Image> favouritesImages = LiveDataTestUtil.getValue(imageDao.getFavourites());
        assertNotNull(favouritesImages);
        assertEquals(3, favouritesImages.size());

        assertEquals(testImage4, favouritesImages.get(0));
        assertEquals(testImage3, favouritesImages.get(1));
        assertEquals(testImage1, favouritesImages.get(2));
    }

    @Test
    public void getFavouritesNoFavouritesButNotEmptyTest() throws InterruptedException {
        imageDao.insert(testImage2);
        imageDao.insert(testImage5);

        List<Image> favouritesImages = LiveDataTestUtil.getValue(imageDao.getFavourites());
        assertEquals(Collections.emptyList(), favouritesImages);
    }

    @Test
    public void getFavouritesMixedTest() throws InterruptedException {
        imageDao.insert(testImage1);
        imageDao.insert(testImage2);
        imageDao.insert(testImage3);
        imageDao.insert(testImage4);
        imageDao.insert(testImage5);

        List<Image> favouritesImages = LiveDataTestUtil.getValue(imageDao.getFavourites());
        assertNotNull(favouritesImages);
        assertEquals(3, favouritesImages.size());

        assertEquals(testImage4, favouritesImages.get(0));
        assertEquals(testImage3, favouritesImages.get(1));
        assertEquals(testImage1, favouritesImages.get(2));
    }

    @Test
    public void updateIsFavouriteToFalseTest() throws InterruptedException {
        imageDao.insert(testImage1);

        List<Image> favouritesImages = LiveDataTestUtil.getValue(imageDao.getFavourites());
        assertNotNull(favouritesImages);
        assertEquals(1, favouritesImages.size());

        imageDao.update(testImage1.getPath(), false);

        favouritesImages = LiveDataTestUtil.getValue(imageDao.getFavourites());
        assertEquals(favouritesImages, Collections.emptyList());
    }

    @Test
    public void updateIsFavouriteToTrueTest() throws InterruptedException {
        imageDao.insert(testImage2);

        List<Image> favouritesImages = LiveDataTestUtil.getValue(imageDao.getFavourites());
        assertEquals(favouritesImages, Collections.emptyList());

        imageDao.update(testImage2.getPath(), true);

        favouritesImages = LiveDataTestUtil.getValue(imageDao.getFavourites());
        assertNotNull(favouritesImages);
        assertEquals(1, favouritesImages.size());
    }
}
