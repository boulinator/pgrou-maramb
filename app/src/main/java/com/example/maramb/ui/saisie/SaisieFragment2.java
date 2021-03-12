package com.example.maramb.ui.saisie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.maramb.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        marqueurs = new ArrayList<>(Arrays. asList(getString(R.string.marqueur1),
                getString(R.string.marqueur2), getString(R.string.marqueur3)));
        Collections.shuffle(marqueurs);
        imageView = (ImageView) root.findViewById(R.id.imageView);
        nextButton = (Button) root.findViewById(R.id.nextButton);
        vote = (SeekBar) root.findViewById(R.id.seekBar);
        otherButton = (Button) root.findViewById(R.id.otherButton);
        text = (TextView) root.findViewById(R.id.ambiance);

        text.setText(marqueurs.get(0));

        Bundle bundle = this.getArguments();
        assert bundle != null;
        byte[] data = bundle.getByteArray("key");
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                data.length);
        imageView.setImageBitmap(bitmap);

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                score = vote.getProgress();
                result.add(score);
                Bundle bundle2 = new Bundle();
                bundle2.putByteArray("photo",data);
                bundle2.putIntegerArrayList("score",result);
                bundle2.putStringArrayList("marqueurs",marqueurs);

                Fragment nextFrag = new SaisieFragment3();
                nextFrag.setArguments(bundle2);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, nextFrag, "findThisFragment")
                        .addToBackStack("second")
                        .commit();
            }
        });

        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = vote.getProgress();
                result.add(score);
                text.setText(marqueurs.get(result.size()));
                vote.setProgress(0);
                if (result.size() == marqueurs.size()-1){
                    otherButton.setVisibility(View.GONE);
                }

            }
        });
        return root;
    }


}
