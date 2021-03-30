package com.example.maramb.ui.saisie;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.maramb.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SaisieFragment2 extends Fragment {

    /**
     * Constructeur du second fragment
     */
    public SaisieFragment2(){}

    /**
     * L'adresse locale de la photo
     */
    Uri photoUri;

    /**
     * L'ImageView pour montrer la photo prise
     */
    ImageView imageView;

    /**
     * Le bouton permettant de passer au fragment suivant
     */
    Button nextButton;

    /**
     * Le bouton permettant de saisir une nouvelle ambiance
     */
    Button otherButton;

    /**
     * Le texte décrivant l'ambiance
     */
    TextView text;

    /**
     * La bare pour noter l'ambiance
     */
    SeekBar vote;

    /**
     * La note de l'ambiance
     */
    int score;

    /**
     * La liste des notes des ambiances
     */
    ArrayList<Integer> result = new ArrayList<>();

    /**
     * La liste des ambiances
     */
    ArrayList<String> ambiances;


    /**
     * Création de la vue du deuxième fragment
     * Création des listes et mélange de la liste d'ambiance
     * Affichage de la photo
     * Mise en place des onClickListener des différents boutons
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return la vue
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                                ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_saisie2, container, false);
        ambiances = new ArrayList<>(Arrays. asList(getResources().getStringArray(R.array.marqueurs)));
        Collections.shuffle(ambiances);
        imageView = root.findViewById(R.id.imageView);
        nextButton = root.findViewById(R.id.nextButton);
        vote = root.findViewById(R.id.seekBar);
        otherButton = root.findViewById(R.id.otherButton);
        text = root.findViewById(R.id.ambiance);
        text.setText(ambiances.get(0));
        Bundle bundle = this.getArguments();
        assert bundle != null;
        photoUri = Uri.parse(bundle.getString("key"));
        imageView.setImageURI(photoUri);

        nextButton.setOnClickListener(this::nextFrag);

        otherButton.setOnClickListener(v -> newAmbiance());
        return root;
    }

    /**
     * On vide la liste de résultats et on remet la bare de note à 0 au redémarrage du fragment
     */
    @Override
    public void onResume() {
        super.onResume();
        result = new ArrayList<>();
        vote.setProgress(0);
    }

    /**
     * Récupération et envoi au nouveau fragment des listes et de l'adresse de la photo
     * Création du nouveau fragment
     * @param v
     */
    public void nextFrag(View v){
        score = vote.getProgress();
        result.add(score);
        Bundle bundle2 = new Bundle();
        bundle2.putString("photo",photoUri.toString());
        bundle2.putIntegerArrayList("score",result);
        bundle2.putStringArrayList("marqueurs", ambiances);
        Navigation.findNavController(v).navigate(R.id.action_navigation_saisie_to_saisieFragment3, bundle2);
    }

    /**
     * Stockage de la note affectée à l'ambiance. Mise à jour du texte avec l'ambiance suivante
     * Disparition du bouton à la cinquième ambiance
     */
    public void newAmbiance(){
        score = vote.getProgress();
        result.add(score);
        text.setText(ambiances.get(result.size()));
        vote.setProgress(0);
        if (result.size() == 4){
            otherButton.setVisibility(View.GONE);
        }
    }

}
