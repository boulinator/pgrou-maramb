package com.example.maramb.ui.saisie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import com.example.maramb.R;
import com.example.maramb.utilsMap;
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

    private MapView map;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private ImageButton nearMe;
    private Button nextButton;
    private LocationManager locationManager;
    private FusedLocationProviderClient client;
    private Marker startMarker;


    public SaisieFragment3(){}


    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saisie3, container, false);
        nearMe = root.findViewById(R.id.near_me);
        nextButton = root.findViewById(R.id.next_button);
        Context ctx = this.getActivity().getApplicationContext();
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        Bundle bundle = this.getArguments();

        map = root.findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(18.0);

        utilsMap.requestPermissionsIfNecessary((Fragment)this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET},
                REQUEST_PERMISSIONS_REQUEST_CODE);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        CompassOverlay compassOverlay = new CompassOverlay(getContext(), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        GeoPoint point = new GeoPoint(47.214429, -1.558497);

        startMarker = new Marker(map);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setDraggable(true);
        map.getOverlays().add(startMarker);
        map.getController().setCenter(point);

        nearMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getCurrentLocation();
                startMarker.setPosition(point);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                map.getOverlays().add(startMarker);
                map.getController().setCenter(point);
            }
        });

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


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putDouble("latitude",startMarker.getPosition().getLatitude());
                bundle.putDouble("longitude",startMarker.getPosition().getLongitude());

                SaisieFragment4 nextFrag = new SaisieFragment4();
                nextFrag.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, nextFrag, "findThisFragment")
                        .addToBackStack("troisieme")
                        .commit();
            }
        });





        return root;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        utilsMap.onRequestPermissionsResult((Fragment) this, requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task<Location> task) {
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
                }

            });
        } else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

}
