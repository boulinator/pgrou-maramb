package com.example.maramb.ui.saisie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.maramb.R;

public class SaisieFragment5 extends Fragment {

    /**
     * Le bouton pour créer un nouveau marqueur
     */
    Button newButton;

    /**
     * Création de la vue du fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return la vue du fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saisie5, container, false);
        newButton=root.findViewById(R.id.nouveau_marqueur);
        newButton.setOnClickListener(v ->
                Navigation.findNavController(root).navigate(R.id.action_navigation_saisie_to_saisieFragment));
        return root;
    }
}
