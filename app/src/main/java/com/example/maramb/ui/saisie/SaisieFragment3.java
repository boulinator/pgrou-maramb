package com.example.maramb.ui.saisie;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.maramb.R;
import com.example.maramb.utilsMap;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SaisieFragment3 extends Fragment {

    private MapView map;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    public SaisieFragment3(){}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_saisie4, container, false);

        Context ctx = this.getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

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

        Marker startMarker = new Marker(map);
        startMarker.setPosition(point);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(startMarker);

        map.getController().setCenter(point);

        return root;
    }


    @Override
    // Chloé : J'ai laissé la def de la fonction ici parce que c'est un Override et pas sûre
    // de pouvoir juste le caler dans utilsMap directement
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        utilsMap.onRequestPermissionsResult((Fragment) this, requestCode, permissions, grantResults);
    }

}
