package com.example.maramb.ui.carte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.maramb.R;
import com.example.maramb.utils.AmbianceMarker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;

public class CarteActivity extends AppCompatActivity {
    final Fragment fragment = new CarteFragment();
    final FragmentManager fm = getSupportFragmentManager();

    Fragment active;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        Intent intent = getIntent();
        int selected_item = intent.getIntExtra("key",0);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_carte)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        navView.setSelectedItemId(selected_item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_carte:
                    fm.beginTransaction().hide(active).show(fragment).commit();
                    active = fragment;
                    return true;
            }
            return false;
        }
    };

    public HashMap<Integer, AmbianceMarker> getAmbianceMarkers(){
        HashMap<Integer, AmbianceMarker> allAmbianceMarkers = new HashMap<Integer, AmbianceMarker>();
        // TODO : remplir hashmap avec données issues de la bdd
        return allAmbianceMarkers;
    };

}