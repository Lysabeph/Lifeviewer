package com.comp30030.lifeviewer.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.comp30030.lifeviewer.data.Image;
import com.comp30030.lifeviewer.data.ImageRepository;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {
   private ImageRepository repository;

   private LiveData<List<Image>> allImages;
   private LiveData<List<Image>> favouriteImages;

   public ImageViewModel(Application application) {
       super(application);
       repository = new ImageRepository(application);

       allImages       = repository.getAllImages();
       favouriteImages = repository.getFavouriteImages();
   }

   public LiveData<List<Image>> getAllImages() { return allImages; }
   public LiveData<List<Image>> getFavouriteImages() { return favouriteImages; }

   public void deleteAll() { repository.deleteAll(); }

   public void insert(Image image) { repository.insert(image); }

   public void insert(List<Image> images) {
       for(Image image : images) {
           insert(image);
       }
   }

   public void update(String path, boolean isFavourite) { repository.update(path, isFavourite); }
}
