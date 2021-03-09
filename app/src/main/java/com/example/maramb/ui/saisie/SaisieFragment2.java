package com.example.maramb.ui.saisie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import java.io.ByteArrayOutputStream;

public class SaisieFragment2 extends Fragment {

    public SaisieFragment2(){}
    ImageView imageView;
    Button nextButton;
    SeekBar vote;
    int score;

    public View onCreateView(@NonNull LayoutInflater inflater,
                                ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saisie2, container, false);
        imageView = (ImageView)root.findViewById(R.id.imageView);
        nextButton = (Button)root.findViewById(R.id.nextButton);
        vote = (SeekBar)root.findViewById(R.id.seekBar);
        Bundle bundle = this.getArguments();
        byte[] data = bundle.getByteArray("key");
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                data.length);
        imageView.setImageBitmap(bitmap);

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                score = vote.getProgress();
                Fragment nextFrag = new SaisieFragment3();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, nextFrag, "findThisFragment")
                        .addToBackStack("second")
                        .commit();
            }
        });
        return root;
    }


}
