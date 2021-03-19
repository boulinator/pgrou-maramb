package com.example.maramb.ui.saisie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.maramb.R;

import java.util.ArrayList;

public class SaisieFragment4 extends Fragment {

    ImageView photo;
    TextView marqueur1;
    TextView marqueur2;
    TextView marqueur3;
    ProgressBar progress1;
    ProgressBar progress2;
    ProgressBar progress3;
    Button sendButton;

    public SaisieFragment4(){}

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saisie4, container, false);
        Bundle bundle = this.getArguments();

        photo=root.findViewById(R.id.recap_image);
        marqueur1=root.findViewById(R.id.recap_marqueur1);
        marqueur2=root.findViewById(R.id.recap_marqueur2);
        marqueur3=root.findViewById(R.id.recap_marqueur3);
        progress1=root.findViewById(R.id.recap_progress1);
        progress2=root.findViewById(R.id.recap_progress2);
        progress3=root.findViewById(R.id.recap_progress3);
        sendButton=root.findViewById(R.id.recap_button_send);

        assert bundle != null;
        Uri photoUri = Uri.parse(bundle.getString("photo"));
        photo.setImageURI(photoUri);

        ArrayList<String> marqueurs = bundle.getStringArrayList("marqueurs");
        ArrayList<Integer> score = bundle.getIntegerArrayList("score");
        marqueur1.setText(marqueurs.get(0));
        progress1.setProgress(score.get(0));
        progress2.setVisibility(View.GONE);
        progress3.setVisibility(View.GONE);


        if (score.size()>1){
            progress2.setVisibility(View.VISIBLE);
            marqueur2.setText(marqueurs.get(1));
            progress2.setProgress(score.get(1));
            if(score.size()>2){
                progress3.setVisibility(View.VISIBLE);
                marqueur3.setText(marqueurs.get(2));
                progress3.setProgress(score.get(2));
            }
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
    }
}
