package com.example.maramb.ui.saisie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.maramb.R;

public class SaisieFragment extends Fragment {
    private SaisieViewModel saisieViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        saisieViewModel =
                new ViewModelProvider(this).get(SaisieViewModel.class);
        View root = inflater.inflate(R.layout.fragment_saisie, container, false);
        final TextView textView = root.findViewById(R.id.text_saisie);
        saisieViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
