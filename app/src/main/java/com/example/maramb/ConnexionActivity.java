package com.example.maramb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;

import java.sql.*;

public class ConnexionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
    }

    public void CreerCompte(View view) {
        Intent intent = new Intent(this, CreerCompteActivity.class);
        startActivity(intent);
    }

    public void Connexion(View view) {
        EditText conn_id = (EditText) findViewById(R.id.connexion_nomUtilisateur);
        EditText conn_mdp = (EditText) findViewById(R.id.connexion_mdp);
        String nomUtilisateur = conn_id.getText().toString();
        String mdp = conn_mdp.getText().toString();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver OK");
            String url = "jdbc:postgresql://ser-info-03.ec-nantes.fr:5432/maramb";
            String user = "maramb";
            String password = "lepetitcheval";

            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("Connection effective");
            String query = "SELECT COUNT(*) FROM users WHERE id = ? AND pwd = ?;";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, nomUtilisateur);
            stmt.setString(2, mdp);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int resultat = rs.getInt(1);

            con.close();
            System.out.println("Connection fermée");

            if (resultat !=0) {
                Intent intent = new Intent(this, FirstActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, CreerCompteActivity.class);
                startActivity(intent);
            }
        }

        catch (Exception e){
            e.printStackTrace();
            conn_id.setText("Toz");
        }
    }
}