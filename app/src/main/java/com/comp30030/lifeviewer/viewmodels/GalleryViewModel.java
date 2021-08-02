package com.comp30030.lifeviewer.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    // private DocumentFile imageDirectory;
    // private DocumentFile[]

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

//    public List<Uri> getImageUrisSlow(DocumentFile rootDocument) {
//        List<Uri> uris = new ArrayList<>();
//
//        if (rootDocument == null) {
//            Log.d("directory", "is null");
//        }
//        else if (rootDocument.isDirectory()) {
//            Log.i("directory", "type == directory");
//
//            DocumentFile[] listOfSubDocuments = rootDocument.listFiles();
//
//            for(DocumentFile subDocument : listOfSubDocuments) {
//                uris.addAll(getImageUrisSlow(subDocument));
//            }
//        }
//        else if (rootDocument.getType() == null) {
//            Log.d("directory", "type == null");
//        }
//        else if (rootDocument.getType().contains("image")) {
//            Log.d("directory", "type == image");
//            uris.add(rootDocument.getUri());
//        }
//        else {
//            Log.d("directory", "type == " + rootDocument.getType());
//        }
//
//        return uris;
//    }
}
