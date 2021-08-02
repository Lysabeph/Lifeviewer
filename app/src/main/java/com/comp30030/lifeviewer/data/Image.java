package com.comp30030.lifeviewer.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_table")
public class Image extends BaseObservable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "path")
    private String path;

    @ColumnInfo(name = "date_modified")
    private long dateModified;

    @Bindable
    @ColumnInfo(name = "is_favourite")
    private boolean isFavourite;

    public Image(String path, long dateModified, boolean isFavourite) {
        this.path = path;
        this.dateModified = dateModified;
        this.isFavourite = isFavourite;
    }

    public String  getPath()         { return path; }
    public long    getDateModified() { return dateModified; }
    public boolean getIsFavourite()  { return isFavourite; }

    public void toggleIsFavourite() {
        isFavourite = !isFavourite;
        Log.d("isFavourite", "" + isFavourite);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Image) {
            Image image = (Image) object;

            if (image.path.equals(this.path)
                    && image.dateModified == this.dateModified
                    && image.isFavourite == isFavourite) {
                return true;
            }
        }

        return false;
    }
}
