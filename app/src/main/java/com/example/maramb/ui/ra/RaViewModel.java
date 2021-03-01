package com.example.maramb.ui.ra;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RaViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public RaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Partie réalité augmentée");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
