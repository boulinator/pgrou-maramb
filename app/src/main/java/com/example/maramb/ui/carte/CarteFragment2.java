package com.example.maramb.ui.carte;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.maramb.R;
import com.example.maramb.utils.AmbianceMarker;
import com.example.maramb.utils.DBAcces;

public class CarteFragment2 extends Fragment {
    private CarteViewModel carteViewModel;
    ImageView photo;
    TextView namePlace;
    TextView marqueur1;
    TextView marqueur2;
    TextView marqueur3;
    TextView marqueur4;
    TextView marqueur5;
    ProgressBar progress1;
    ProgressBar progress2;
    ProgressBar progress3;
    ProgressBar progress4;
    ProgressBar progress5;

    DBAcces db;
    ConstraintLayout layout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        carteViewModel =
                new ViewModelProvider(this).get(CarteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_carte2, container, false);
        Bundle bundle = this.getArguments();

        photo=root.findViewById(R.id.marqueur_image);
        namePlace = root.findViewById(R.id.marqueur_lieu);
        marqueur1=root.findViewById(R.id.marqueur_mot1);
        marqueur2=root.findViewById(R.id.marqueur_mot2);
        marqueur3=root.findViewById(R.id.marqueur_mot3);
        marqueur4=root.findViewById(R.id.marqueur_mot4);
        marqueur5=root.findViewById(R.id.marqueur_mot5);
        progress1=root.findViewById(R.id.mot_progress1);
        progress2=root.findViewById(R.id.mot_progress2);
        progress3=root.findViewById(R.id.mot_progress3);
        progress4=root.findViewById(R.id.mot_progress4);
        progress5=root.findViewById(R.id.mot_progress5);
        layout=root.findViewById(R.id.layout);
        assert bundle != null;
        int marker_id = bundle.getInt("key");

        db = new DBAcces();
        AmbianceMarker currentMarker = db.getMarkerById(marker_id);

        namePlace.setText(currentMarker.getPlaceName());

        byte[] byteArray = currentMarker.getPhoto();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        photo.setImageBitmap(bmp);

        marqueur1.setText(currentMarker.getAmbianceName().get(0));
        progress1.setProgress(currentMarker.getScores().get(0));
        progress2.setVisibility(View.GONE);
        progress3.setVisibility(View.GONE);
        progress4.setVisibility(View.GONE);
        progress5.setVisibility(View.GONE);

        if (currentMarker.getScores().size()>1){
            progress2.setVisibility(View.VISIBLE);
            marqueur2.setText(currentMarker.getAmbianceName().get(1));
            progress2.setProgress(currentMarker.getScores().get(1));
            if(currentMarker.getScores().size()>2){
                progress3.setVisibility(View.VISIBLE);
                marqueur3.setText(currentMarker.getAmbianceName().get(2));
                progress3.setProgress(currentMarker.getScores().get(2));
                if(currentMarker.getScores().size()>3){
                    progress4.setVisibility(View.VISIBLE);
                    marqueur4.setText(currentMarker.getAmbianceName().get(3));
                    progress4.setProgress(currentMarker.getScores().get(3));
                    if(currentMarker.getScores().size()>4){
                        progress5.setVisibility(View.VISIBLE);
                        marqueur5.setText(currentMarker.getAmbianceName().get(4));
                        progress5.setProgress(currentMarker.getScores().get(4));
                    }
                }
            }
        }

        return root;
    }
}