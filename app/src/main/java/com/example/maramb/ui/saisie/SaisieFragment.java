package com.example.maramb.ui.saisie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.maramb.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaisieFragment extends Fragment {

    /**
     * Constructeur du premier fragment
     */
    public SaisieFragment(){}

    /**
     * Le chemin d'accès à la photo
     */
    private String currentPhotoPath;

    /**
     * L'adresse locale de la photo
     */
    private Uri photoURI;

    /**
     * Le code pour prendre une photo
     */
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * La vue du fragment
     */
    View root;

    /**
     * Le bouton permettant de lancer l'appareil photo (en cas de retour dans l'application)
     */
    ImageButton photoButton;

    /**
     * Création de la vue du premier fragment de saisie
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return la vue
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_saisie, container, false);
        photoButton = root.findViewById(R.id.photo_button);
        photoButton.setOnClickListener(v -> startImageCapture());
        startImageCapture();
        return root;
    }


    /**
     * Méthode pour prendre la photo et la sauvegarder
     */
    public void startImageCapture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.d("photo save error", "erreur dans l'enregistrement de la photo");
        }
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(getContext(),
                    "com.example.android.fileprovider",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            galleryAddPic();
        }

    }


    /**
     * Méthode pour démarrer le second fragment en envoyant l'adresse de la photo lorsque la photo est prise
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = new Bundle();
                bundle.putString("key", photoURI.toString());
                Navigation.findNavController(root).navigate(R.id.action_navigation_saisie_to_saisieFragment2, bundle);
            }
        }
    }

    /**
     * Méthode pour créer le fichier de l'image
     * @return le fichier image
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Méthode pour ajouter une photo à la galerie du téléphone
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }
}