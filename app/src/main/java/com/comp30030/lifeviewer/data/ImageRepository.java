package com.comp30030.lifeviewer.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ImageRepository {


    private ImageDao imageDao;
    private LiveData<List<Image>> allImages;
    private LiveData<List<Image>> favouriteImages;

    // TODO: Unit test.
    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ImageRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        imageDao = db.imageDao();
        allImages = imageDao.getAllOrderByDateModifiedDesc();
        favouriteImages = imageDao.getFavourites();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Image>> getAllImages() {
        return allImages;
    }

    public LiveData<List<Image>> getFavouriteImages() { return favouriteImages; }

    public boolean hasFavourites() {
        List<Image> favourites = getFavouriteImages().getValue();

        return favourites != null && !favourites.isEmpty();
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            imageDao.deleteAll();
        });
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Image image) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            imageDao.insert(image);
        });
    }

    public void update(String path, boolean isFavourite) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            imageDao.update(path, isFavourite);
        });
    }
}
