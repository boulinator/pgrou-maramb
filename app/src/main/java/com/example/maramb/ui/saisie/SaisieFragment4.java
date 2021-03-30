package com.example.maramb.ui.saisie;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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

    /**
     * L'espace de la photo dans le Layout
     */
    ImageView photo;

    /**
     * La première ambiance
     */
    TextView ambiance1;

    /**
     * La deuxième ambiance
     */
    TextView ambiance2;

    /**
     * La troisième ambiance
     */
    TextView ambiance3;

    /**
     * La quatrième ambiance
     */
    TextView ambiance4;

    /**
     * La cinquième ambiance
     */
    TextView ambiance5;

    /**
     * La première barre de note
     */
    ProgressBar progress1;

    /**
     * La deuxième barre de note
     */
    ProgressBar progress2;

    /**
     * La troisième barre de note
     */
    ProgressBar progress3;

    /**
     * La quatrième barre de note
     */
    ProgressBar progress4;

    /**
     * La cinquième barre de note
     */
    ProgressBar progress5;

    /**
     * Le bouton d'envoi
     */
    Button sendButton;

    /**
     * La base de données
     */
    DBAcces db;

    /**
     * La liste des ambiances
     */
    ArrayList<String> ambiances;

    /**
     * La liste des scores
     */
    ArrayList<Integer> score;

    /**
     * L'adresse locale de la photo
     */
    Uri photoUri;

    /**
     * La latitude du marqueur
     */
    double latitude;

    /**
     * La longitude du marqueur
     */
    double longitude;

    /**
     * La localisation du marqueur
     */
    GeoPoint location;

    /**
     * Constructeur du quatrième fragment
     */
    public SaisieFragment4(){}



    /**
     * Création de la vue du quatrième fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return la vue
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saisie4, container, false);
        Bundle bundle = this.getArguments();

        photo=root.findViewById(R.id.recap_image);
        ambiance1=root.findViewById(R.id.recap_marqueur1);
        ambiance2=root.findViewById(R.id.recap_marqueur2);
        ambiance3=root.findViewById(R.id.recap_marqueur3);
        ambiance4=root.findViewById(R.id.recap_marqueur4);
        ambiance5=root.findViewById(R.id.recap_marqueur5);
        progress1=root.findViewById(R.id.recap_progress1);
        progress2=root.findViewById(R.id.recap_progress2);
        progress3=root.findViewById(R.id.recap_progress3);
        progress4=root.findViewById(R.id.recap_progress4);
        progress5=root.findViewById(R.id.recap_progress5);
        sendButton=root.findViewById(R.id.recap_button_send);
        assert bundle != null;
        photoUri = Uri.parse(bundle.getString("photo"));
        photo.setImageURI(photoUri);

        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        location = new GeoPoint(latitude, longitude);
        ambiances = bundle.getStringArrayList("marqueurs");
        score = bundle.getIntegerArrayList("score");
        ambiance1.setText(ambiances.get(0));
        progress1.setProgress(score.get(0));
        progress2.setVisibility(View.GONE);
        progress3.setVisibility(View.GONE);
        progress4.setVisibility(View.GONE);
        progress5.setVisibility(View.GONE);


        if (score.size()>1){
            progress2.setVisibility(View.VISIBLE);
            ambiance2.setText(ambiances.get(1));
            progress2.setProgress(score.get(1));
            if(score.size()>2){
                progress3.setVisibility(View.VISIBLE);
                ambiance3.setText(ambiances.get(2));
                progress3.setProgress(score.get(2));
                if(score.size()>3){
                    progress4.setVisibility(View.VISIBLE);
                    ambiance4.setText(ambiances.get(3));
                    progress4.setProgress(score.get(3));
                    if(score.size()>4){
                        progress5.setVisibility(View.VISIBLE);
                        ambiance5.setText(ambiances.get(4));
                        progress5.setProgress(score.get(4));
                    }
                }
            }
        }


        sendButton.setOnClickListener(this::sendToDb);

        return root;
    }

    /**
     * Méthode permettant l'envoi des données à la base de données
     * @param v vue
     */
    public void sendToDb(View v){
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
            int placeID = (Integer)place.get(1);
            Date date = new Date(Calendar.getInstance().getTime().getTime());
            byte[] inputData;
            inputData = getBytes(iStream);
            AmbianceMarker marker = new AmbianceMarker(0,location,placeName,ambiances,score, date,inputData,0,placeID);
            db.writeMarker(con, marker);
            Navigation.findNavController(v).navigate(R.id.action_navigation_saisie_to_saisieFragment5);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"Echec de l'envoi du marqueur", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Conversion d'un InputStream en ByteArray
     * @param inputStream
     * @return le ByteArray de photo
     * @throws IOException
     */
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
