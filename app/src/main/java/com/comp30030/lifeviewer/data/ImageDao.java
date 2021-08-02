package com.comp30030.lifeviewer.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDao {

    @Insert()
    void insert(Image image);

    @Query("DELETE FROM image_table")
    void deleteAll();

    @Query("SELECT * FROM image_table ORDER BY date_modified DESC")
    LiveData<List<Image>> getAllOrderByDateModifiedDesc();

    @Query("SELECT * FROM image_table WHERE is_favourite = 1 ORDER BY date_modified DESC")
    LiveData<List<Image>> getFavourites();

    @Query("UPDATE image_table SET is_favourite = :isFavourite WHERE path = :path")
    void update(String path, boolean isFavourite);
}
