package com.example.maramb.ui.carte;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.maramb.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.maramb.utils.DBAcces;
import com.example.maramb.utils.utilsMap;

public class CarteFragment extends Fragment {

    private MapView map;
    private MapController mapController;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LocationManager locationManager;

    public CarteFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_carte, container, false);

        Context ctx = this.getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // Initialize map and see my position
        initMap(ctx, root);

        // Get pins for each marker in the db
        DBAcces instance = new DBAcces();
        HashMap<Integer, GeoPoint> existingMarkers = instance.getLocationsAndMarkersID();
        ArrayList<Integer> ambianceMarkerID = new ArrayList<Integer>();
        ArrayList<Marker> listMarkers = getPlacesInMarkers(existingMarkers, ambianceMarkerID);
        for (int i = 0 ; i < listMarkers.size(); i++){
            Marker positionMarker = listMarkers.get(i);
            positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            map.getOverlays().add(positionMarker);
        }

        actionOnMarkerHit(listMarkers, ambianceMarkerID);

        return root;
    }

    private void initMap(Context ctx, View root) {
        map = root.findViewById(R.id.mapview2);
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = (MapController) map.getController();
        mapController.setZoom(15.0);

        utilsMap.requestPermissionsIfNecessary((Fragment)this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        CompassOverlay compassOverlay = new CompassOverlay(getContext(), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Get location
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation == null){
            lastLocation = new Location("dummyprovider");
            lastLocation.setLongitude(-1.558497);
            lastLocation.setLatitude(47.214429);
        }

        GeoPoint currentLocation = utilsMap.updateLoc(lastLocation, mapController, map);

        // Display current position on the map
        Marker positionMarker = new Marker(map);
        positionMarker.setPosition(currentLocation);
        positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        positionMarker.setIcon(ctx.getResources().getDrawable(R.drawable.iconhuman));
        map.getOverlays().add(positionMarker);

        map.getController().setCenter(currentLocation);
    }

    private ArrayList<Marker> getPlacesInMarkers(HashMap<Integer, GeoPoint> existingMarkers, ArrayList<Integer> ambianceMarkerID){
        ArrayList<Marker> markersList = new ArrayList<>();
        for (Map.Entry mapentry : existingMarkers.entrySet()) {
            int markerID = (int) mapentry.getKey();
            GeoPoint currentLocation = (GeoPoint) mapentry.getValue();
            Marker positionMarker = new Marker(map);
            positionMarker.setPosition(currentLocation);
            markersList.add(positionMarker);
            ambianceMarkerID.add(markerID);
        }
        return markersList;
    }

    public void actionOnMarkerHit(ArrayList<Marker> listMarkers, ArrayList<Integer> ambianceMarkerID){
        for (int i = 0 ; i < listMarkers.size(); i++){
            Marker currentMarker = listMarkers.get(i);
            int marker_id = ambianceMarkerID.get(i);
            Bundle bundle = new Bundle();
            currentMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView map) {
                    bundle.putInt("key", marker_id);
                    Navigation.findNavController(map).navigate(R.id.action_navigation_carte_to_navigation_carte2, bundle);
                    return true;
                }
        });
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Context ctx = this.getActivity().getApplicationContext();
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        locationManager.removeUpdates(myLocationListener);
    }

    private LocationListener myLocationListener
            = new LocationListener(){

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            utilsMap.updateLoc(location, mapController, map);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        utilsMap.onRequestPermissionsResult((Fragment) this, requestCode, permissions, grantResults);
    }

}
