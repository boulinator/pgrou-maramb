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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maramb.R;
import com.example.maramb.utils.AmbianceMarker;
import com.example.maramb.utils.DBAcces;

import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;

public class SaisieFragment4 extends Fragment{

    ImageView photo;
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

    Button sendButton;
    DBAcces db;
    ConstraintLayout layout;

    public SaisieFragment4(){}

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saisie4, container, false);
        Bundle bundle = this.getArguments();

        photo=root.findViewById(R.id.recap_image);
        marqueur1=root.findViewById(R.id.recap_marqueur1);
        marqueur2=root.findViewById(R.id.recap_marqueur2);
        marqueur3=root.findViewById(R.id.recap_marqueur3);
        marqueur4=root.findViewById(R.id.recap_marqueur4);
        marqueur5=root.findViewById(R.id.recap_marqueur5);
        progress1=root.findViewById(R.id.recap_progress1);
        progress2=root.findViewById(R.id.recap_progress2);
        progress3=root.findViewById(R.id.recap_progress3);
        progress4=root.findViewById(R.id.recap_progress4);
        progress5=root.findViewById(R.id.recap_progress5);
        sendButton=root.findViewById(R.id.recap_button_send);
        layout=root.findViewById(R.id.layout);
        assert bundle != null;
        Uri photoUri = Uri.parse(bundle.getString("photo"));
        photo.setImageURI(photoUri);

        Double latitude = bundle.getDouble("latitude");
        Double longitude = bundle.getDouble("longitude");
        GeoPoint location = new GeoPoint(latitude, longitude);
        ArrayList<String> marqueurs = bundle.getStringArrayList("marqueurs");
        ArrayList<Integer> score = bundle.getIntegerArrayList("score");
        marqueur1.setText(marqueurs.get(0));
        progress1.setProgress(score.get(0));
        progress2.setVisibility(View.GONE);
        progress3.setVisibility(View.GONE);
        progress4.setVisibility(View.GONE);
        progress5.setVisibility(View.GONE);


        if (score.size()>1){
            progress2.setVisibility(View.VISIBLE);
            marqueur2.setText(marqueurs.get(1));
            progress2.setProgress(score.get(1));
            if(score.size()>2){
                progress3.setVisibility(View.VISIBLE);
                marqueur3.setText(marqueurs.get(2));
                progress3.setProgress(score.get(2));
                if(score.size()>3){
                    progress4.setVisibility(View.VISIBLE);
                    marqueur4.setText(marqueurs.get(3));
                    progress4.setProgress(score.get(3));
                    if(score.size()>4){
                        progress5.setVisibility(View.VISIBLE);
                        marqueur5.setText(marqueurs.get(4));
                        progress5.setProgress(score.get(4));
                    }
                }
            }
        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream iStream = null;
                try {
                    iStream = getActivity().getContentResolver().openInputStream(photoUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    db = new DBAcces();
                    Connection con = db.connect();
                    ArrayList place = db.locationToPlace(con, latitude,longitude);
                    String placeName = place.get(0).toString();
                    Integer placeID = (Integer)place.get(1);
                    Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
                    byte[] inputData = new byte[0];
                    inputData = getBytes(iStream);
                    AmbianceMarker marker = new AmbianceMarker(0,location,placeName,marqueurs,score, date,inputData,0,placeID);
                    db.writeMarker(con, marker);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        });

        return root;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
