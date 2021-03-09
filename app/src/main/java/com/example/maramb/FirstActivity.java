package com.example.maramb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FirstActivity extends AppCompatActivity {

    LinearLayoutCompat saisieLayout;
    LinearLayoutCompat carteLayout;
    LinearLayoutCompat raLayout;
    LinearLayoutCompat compteLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        saisieLayout = findViewById(R.id.launch_saisie);
        carteLayout = findViewById(R.id.launch_carte);
        raLayout = findViewById(R.id.launch_ra);
        compteLayout = findViewById(R.id.launch_compte);

        saisieLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(FirstActivity.this, MainActivity.class);
                myIntent.putExtra("key", R.id.navigation_saisie);
                startActivity(myIntent);
            }
        });

        carteLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(FirstActivity.this, MainActivity.class);
                myIntent.putExtra("key", R.id.navigation_carte);
                startActivity(myIntent);
            }
        });

        raLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(FirstActivity.this, MainActivity.class);
                myIntent.putExtra("key", R.id.navigation_ra);
                startActivity(myIntent);
            }
        });

        compteLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(FirstActivity.this, MainActivity.class);
                myIntent.putExtra("key", R.id.navigation_compte);
                startActivity(myIntent);
            }
        });
    }
}