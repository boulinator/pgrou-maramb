package com.example.maramb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

/**
 * Classe représentant l'activité de la page de connexion. Elle comprend les mécanismes d'authentification et implémente les redirections vers d'autres pages.
 */
public class ConnexionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
    }

    /**
     * Méthode appelé par l'action du bouton de création de compte. Elle redirige simplement vers la page(activité) de création de compte.
     * @param view  : Widget actionnant la méthode, ici ce sera le bouton de création de compte normalement.
     */
    public void CreerCompte(View view) {
        Intent intent = new Intent(this, CreerCompteActivity.class);
        startActivity(intent);
    }

    /**
     * Méthode appelé par l'action du bouton de connexion. Elle sert à authentifier l'utilisateur auprès de la base de donnée PostGIS directement.
     * @param view  : Widget actionnant la méthode, ici ce sera le bouton de connexion normalement.
     */
    public void Connexion(View view) {
        String nomUtilisateur = ((EditText)findViewById(R.id.connexion_nomUtilisateur)).getText().toString();
        String mdp = ((EditText)findViewById(R.id.connexion_mdp)).getText().toString();
        String query = "SELECT COUNT(*) FROM users WHERE id = ? AND pwd = ?;";
        new ExistanceQueryDatabaseAsyncTask().execute(new String[]{query, nomUtilisateur, BCrypt.hashpw(mdp, BCrypt.gensalt())});
    }

    /**
     * Cette sous-classe a été construite pour réaliser des appels asynchrones en base de donnée pour vérfier l'existence ou non d'un champ.
     * Elle hérite de la classe AsyncTask de manière à pouvoir s'exécuter en parallèle du Thread principal.
     * Par défaut de temps, je n'ai pas pu me renseigner suffisamment, et donc la classe n'est pas encore vraiment générique, j'ai dû inclure des instructions propres au problème de connexion de compte.
     */
    public class ExistanceQueryDatabaseAsyncTask extends AsyncTask<String,Void,Boolean> {
        /**
         * Cette méthode va réaliser en fond le travail de checker en base de donnée l'existence du compte.
         * @param queryParams   : Paramètres de la requête.
         * @return              : Renvoie un booléen témoignant de si le test est un succès ou non.
         */
        @Override
        protected Boolean doInBackground(String... queryParams) {
            try{
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://ser-info-03.ec-nantes.fr:5432/maramb";
                String user = "maramb";
                String password = "lepetitcheval";
                Connection con = DriverManager.getConnection(url, user, password);
                String query = queryParams[0];

                PreparedStatement stmt = con.prepareStatement(query);
                for(int i=0;i<queryParams.length-1;i++){
                    try{
                        stmt.setInt(i+1,Integer.parseInt(queryParams[i+1]));
                    }
                    catch(Exception e){
                        stmt.setString(i+1, queryParams[i+1]);
                    }
                }
                ResultSet result = stmt.executeQuery();
                result.next();
                if (result.getInt(1)!=0) {
                    Intent intent = new Intent(ConnexionActivity.this, FirstActivity.class);
                    Snackbar.make(findViewById(R.id.connexion_layout),R.string.connexion_msg_succes, Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar snackbar, int event){
                            startActivity(intent);
                        }
                    }).show();
                }
                else {
                    Snackbar.make(findViewById(R.id.connexion_layout),R.string.connexion_msg_echec, Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar snackbar, int event){
                            ((EditText)findViewById(R.id.connexion_nomUtilisateur)).setText("");
                            ((EditText)findViewById(R.id.connexion_mdp)).setText("");
                        }
                    }).show();
                }
                con.close();
                return true;
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            return false;
        }

        /**
         * Cette méthode s'occupe d'appliquer les résultats en fonction de l'existence ou non du compte
         * @param bool  : Réponse de la méthode doInBackground. Indique si le compte existe ou pas.
         */
        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool){
                Intent intent = new Intent(ConnexionActivity.this, FirstActivity.class);
                Snackbar.make(findViewById(R.id.connexion_layout),R.string.connexion_msg_succes, Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar snackbar, int event){
                        startActivity(intent);
                    }
                }).show();
            }
            else{
                Snackbar.make(findViewById(R.id.connexion_layout),R.string.connexion_msg_echec, Snackbar.LENGTH_SHORT).show();
                ((EditText)findViewById(R.id.connexion_nomUtilisateur)).setText("");
                ((EditText)findViewById(R.id.connexion_mdp)).setText("");
            }
        }
    }
}