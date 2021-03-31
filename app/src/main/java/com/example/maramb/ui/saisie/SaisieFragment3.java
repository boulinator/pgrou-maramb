package com.example.maramb.ui.saisie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.maramb.R;
import com.example.maramb.utils.utilsMap;
import com.example.maramb.utils.utilsMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SaisieFragment3 extends Fragment {

    /**
     * L'espace de la carte dans le Layout
     */
    private MapView map;

    /**
     * Le code pour la demande de permissions
     */
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    /**
     * Le bouton permettant de se localiser
     */
    private ImageButton nearMe;

    /**
     *  Le bouton pour passer a la suite
     */
    private Button nextButton;

    /**
     * Client permettant l'acces a la localisation du téléphone
     */
    private FusedLocationProviderClient client;

    /**
     * Le marqueur de position
     */
    private Marker startMarker;

    /**
     * Le point de départ
     */
    GeoPoint point;

    /**
     * Le Bundle pour passer les information d'un fragment a un autre
     */
    Bundle bundle;

    /**
     * Constructeur du fragment
     */
    public SaisieFragment3(){}


    /**
     * Création de la vue du troisieme fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return la vue
     */
    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saisie3, container, false);
        nearMe = root.findViewById(R.id.near_me);
        nextButton = root.findViewById(R.id.next_button);
        bundle = this.getArguments();
        Context ctx = this.getActivity().getApplicationContext();
        map = root.findViewById(R.id.mapview);


        initMap(ctx);

        nearMe.setOnClickListener(v -> newLocation());

        startMarker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                GeoPoint point = marker.getPosition();
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                map.getOverlays().add(marker);
                map.getController().setCenter(point);
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });


        nextButton.setOnClickListener(this::nextFrag);

        return root;
    }


    /**
     * Création de la carte
     * @param ctx contexte
     */
    public void initMap(Context ctx){
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(18.0);

        utilsMap.requestPermissionsIfNecessary(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET},
                REQUEST_PERMISSIONS_REQUEST_CODE);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        CompassOverlay compassOverlay = new CompassOverlay(getContext(), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        point = new GeoPoint(47.214429, -1.558497);
        getCurrentLocation();
        startMarker = new Marker(map);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setDraggable(true);
        map.getOverlays().add(startMarker);
        map.getController().setCenter(point);
    }

    /**
     * Modifie le placement du marqueur de localisation
     */
    public void newLocation(){
            getCurrentLocation();
            startMarker.setPosition(point);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            map.getOverlays().add(startMarker);
            map.getController().setCenter(point);
    }

    /**
     * Démarre le nouveau fragment en ajoutant au bundle la localisation
     * @param v vue
     */
    public void nextFrag(View v){
        bundle.putDouble("latitude",startMarker.getPosition().getLatitude());
        bundle.putDouble("longitude",startMarker.getPosition().getLongitude());
        Navigation.findNavController(v).navigate(R.id.action_navigation_saisie_to_saisieFragment4, bundle);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        utilsMap.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * Récupération de la localisation du téléphone
     */
    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {
            client.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();

                if (location != null){
                    GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                    startMarker.setPosition(point);
                    map.getOverlays().add(startMarker);
                    map.getController().setCenter(point);
                }
                else{
                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1);

                    LocationCallback locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                            startMarker.setPosition(point);
                            map.getOverlays().add(startMarker);
                            map.getController().setCenter(point);
                        }
                    };

                    client.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                }
            });
        } else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

}
