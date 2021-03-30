package com.example.maramb.ui.saisie;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.maramb.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SaisieFragment2 extends Fragment {

    public SaisieFragment2(){}
    ImageView imageView;
    Button nextButton;
    Button otherButton;
    TextView text;
    SeekBar vote;
    int score;
    ArrayList<Integer> result = new ArrayList<>();
    ArrayList<String> marqueurs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                                ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_saisie2, container, false);
        marqueurs = new ArrayList<>(Arrays. asList(getResources().getStringArray(R.array.marqueurs)));
        Collections.shuffle(marqueurs);
        imageView = root.findViewById(R.id.imageView);
        nextButton = root.findViewById(R.id.nextButton);
        vote = root.findViewById(R.id.seekBar);
        otherButton = root.findViewById(R.id.otherButton);
        text = root.findViewById(R.id.ambiance);

        text.setText(marqueurs.get(0));

        Bundle bundle = this.getArguments();
        assert bundle != null;
        Uri photoUri = Uri.parse(bundle.getString("key"));
        imageView.setImageURI(photoUri);

        nextButton.setOnClickListener(v -> {
            score = vote.getProgress();
            result.add(score);
            Bundle bundle2 = new Bundle();
            bundle2.putString("photo",photoUri.toString());
            bundle2.putIntegerArrayList("score",result);
            bundle2.putStringArrayList("marqueurs",marqueurs);
            Navigation.findNavController(v).navigate(R.id.action_navigation_saisie_to_saisieFragment3, bundle2);



        });

        otherButton.setOnClickListener(v -> {
            score = vote.getProgress();
            result.add(score);
            text.setText(marqueurs.get(result.size()));
            vote.setProgress(0);
            if (result.size() == 4){
                otherButton.setVisibility(View.GONE);
            }

        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        result = new ArrayList<>();
        vote.setProgress(0);
    }
}
