package com.example.maramb.ui.saisie;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SaisieViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public SaisieViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Partie saisie de marqueur");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
