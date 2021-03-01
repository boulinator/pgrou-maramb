package com.example.maramb.ui.carte;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CarteViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CarteViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Partie carte");
    }

    public LiveData<String> getText() {
        return mText;
    }
}