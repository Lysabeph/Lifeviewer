package com.comp30030.lifeviewer.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HighlightsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HighlightsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is highlights fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
