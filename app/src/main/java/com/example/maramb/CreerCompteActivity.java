package com.example.maramb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import org.mindrot.jbcrypt.BCrypt;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.regex.*;

/**
 * Classe représentant l'activité de la page de connexion. Elle comprend les mécanismes d'authentification et implémente les redirections vers d'autres pages.
 */
public class CreerCompteActivity extends AppCompatActivity {
    String nom, prenom, tel, mail, id, mdp, mdpConf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_compte);
    }

    /**
     * Méthode servant à vérifier l'existance ou non de doublons dans la base de donnée PostGIS.
     * @param con       : Connexion à utiliser pour accéder à la base de donnée.
     * @param champ     : Champ de la valeur à tester.
     * @param valeur    : Valeur du champ, on vérifiera s'il y a déjà un enregistrement avec cette valeur pour ce champ.
     * @param errorMsg  : Message d'erreur à afficher en cas de doublon.
     * @return          : Renvoie true s'il n'y a pas de doublons.
     */
    public boolean checkBDD(Connection con, String champ, String valeur, String errorMsg){
        try{
            String checkQuery = "SELECT COUNT(*) FROM users WHERE "+ champ +"=?";
            PreparedStatement stmt = con.prepareStatement(checkQuery);
            try{
                stmt.setInt(1,Integer.parseInt(valeur));
            }
            catch(Exception e){
                stmt.setString(1, valeur);
            }
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt(1)==0){
                return false;
            }
            else{
                Snackbar.make(findViewById(R.id.creer_compte_layout), errorMsg, Snackbar.LENGTH_SHORT).show();
                return true;
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * Méthode servant à vérifier le format de certaines entrées à partir d'une expression régulière.
     * @param entry     : Entrée à valider.
     * @param reg       : Expression régulière représentant le bon format pour l'entrée.
     * @param errorMsg  : Message d'erreur au cas où l'entrée ne valide pas l'expression régulière
     * @return          : Renvoie true si l'entrée est invalide.
     */
    public boolean checkEntry(String entry, Pattern reg, String errorMsg){
        Matcher m = reg.matcher(entry);
        if (m.matches()){
            return false;
        }
        else{
            Snackbar.make(findViewById(R.id.creer_compte_layout), errorMsg, Snackbar.LENGTH_SHORT).show();
            return true;
        }
    }

    /**
     * Méthode appelé par l'action du bouton de création de compte. Elle amorce les vérifications du formulaire et l'insertion du nouvel utilisateur dans la base de donnée PostGIS.
     * @param view  : Widget actionnant la méthode, ici ce sera le bouton de création de compte normalement.
     */
    public void creationCompte(View view){
        nom = ((EditText)findViewById(R.id.creer_compte_nom))                    .getText().toString();
        prenom = ((EditText)findViewById(R.id.creer_compte_prenom))              .getText().toString();
        tel = ((EditText)findViewById(R.id.creer_compte_tel))                    .getText().toString();
        mail = ((EditText)findViewById(R.id.creer_compte_mail))                  .getText().toString();
        id = ((EditText)findViewById(R.id.creer_compte_id))                      .getText().toString();
        mdp = ((EditText)findViewById(R.id.creer_compte_mdp))                    .getText().toString();
        mdpConf = ((EditText)findViewById(R.id.creer_compte_mdp_confirmation))   .getText().toString();
        // On verifie d'abord que les entrées ont le bon format avec les regex, avant de realiser les appels en base de donnee
        // On remarque qu'on ne prend pas en compte les numéros de téléphone étrangers /!\
        Pattern mailRegex = Pattern.compile(".+@.+\\..+");
        Pattern telRegex = Pattern.compile("[0-9]{10}");
        if( checkEntry(mail, mailRegex, CreerCompteActivity.this.getString(R.string.creer_compte_error_mail)) ||
                checkEntry(tel, telRegex, CreerCompteActivity.this.getString(R.string.creer_compte_error_tel))){
            return;
        }
        // On verifie aussi que le mot de passe et sa confirmation
        else if (!mdp.equals(mdpConf)){
            Snackbar.make(findViewById(R.id.creer_compte_layout), R.string.creer_compte_error_mdp, Snackbar.LENGTH_SHORT).show();
            return;
        }
        // Puis on accede a la base de donnee en asynchrone
        String query = "INSERT INTO users (name,surname,tel,mail,id,pwd) VALUES (?,?,?,?,?,?);";
        new UpdateDatabaseAsyncTask().execute(new String[]{query, nom, prenom, tel, mail, id, BCrypt.hashpw(mdp, BCrypt.gensalt()), BCrypt.hashpw(mdp, BCrypt.gensalt())});
    }

    /**
     * Cette sous-classe a été construite pour réaliser des appels asynchrones en base de donnée pour actualiser une table. En utilisant donc une méthode executeUpdate() !
     * Elle hérite de la classe AsyncTask de manière à pouvoir s'exécuter en parallèle du Thread principal.
     * Par défaut de temps, je n'ai pas pu me renseigner suffisamment, et donc la classe n'est pas encore vraiment générique, j'ai dû inclure des instructions propres au problème de connexion de compte.
     */
    public class UpdateDatabaseAsyncTask extends AsyncTask<String, Void, Boolean> {
        /**
         * Cette méthode va réaliser en fond le travail d'accéder à base de donnée et de la mettre à jour avec les nouvelles données.
         * Cette méthode va aussi vérifier les nouvelles données avant de les insérer.
         * @param queryParams   : Paramètres de la requête.
         * @return              : Renvoie un booléen témoignant de si la modification de la base de donnée a pu se faire.
         */
        @Override
        protected Boolean doInBackground(String... queryParams) {
            try{
                // Information de connexion
                Class.forName("org.postgresql.Driver");
                String url = "jdbc:postgresql://ser-info-03.ec-nantes.fr:5432/maramb";
                String user = "maramb";
                String password = "lepetitcheval";
                String query = queryParams[0];
                mail = queryParams[4];
                id = queryParams[5];
                tel = queryParams[3];
                // Connexion à la base de donnee et verification de l'unicite des identifiants en base de donnee
                Connection con = DriverManager.getConnection(url, user, password);
                // Puis on verifie qu'il n y a pas de doublons en base de donnee
                if( checkBDD(con, "tel",tel, CreerCompteActivity.this.getString(R.string.creer_compte_error_used_tel))      ||
                    checkBDD(con, "mail",mail, CreerCompteActivity.this.getString(R.string.creer_compte_error_used_mail))    ||
                    checkBDD(con, "id",id, CreerCompteActivity.this.getString(R.string.creer_compte_error_used_id)))
                {
                    return false;
                }
                // Preparation de la requete
                PreparedStatement stmt = con.prepareStatement(query);
                // Il ne faut pas regarder ni le premier élément (requet) ni le dernier (confirmation du mdp)
                for(int i=0;i<queryParams.length-2;i++){
                    try{
                        stmt.setInt(i+1,Integer.parseInt(queryParams[i+1]));
                    }
                    catch(Exception e){
                        stmt.setString(i+1, queryParams[i+1]);
                    }
                }
                // Execution de la requete et fermeture de la connexion
                stmt.executeUpdate();
                con.close();
                return true;
            }
            catch (PSQLException ex){
                ex.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            Snackbar.make(findViewById(R.id.creer_compte_layout), CreerCompteActivity.this.getString(R.string.creer_compte_errorbdd), Snackbar.LENGTH_SHORT).show();
            return false;
        }

        /**
         * Cette méthode s'occupe d'appliquer les résultats en fonction de l'application ou non des changements de la base de donnée.
         * @param bool  : Réponse de la méthode doInBackground. Indique si les modifications de la base de donnée ont été réalisés ou non.
         */
        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool){
                Snackbar.make(findViewById(R.id.connexion_layout), CreerCompteActivity.this.getString(R.string.creer_compte_validation), Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback(){
                    public void onDismissed(Snackbar snack, int event){
                        startActivity(new Intent(CreerCompteActivity.this, ConnexionActivity.class));
                    }
                }).show();
            }
        }
    }
}