package com.example.maramb.utils;

import org.osmdroid.util.GeoPoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DBAcces {

    /**
     * L'URL de la base de données
     */
    String url = "jdbc:postgresql://ser-info-03.ec-nantes.fr:5432/maramb";

    /**
     * L'utilisateur de la base de données
     */
    String user = "maramb";

    /**
     * Le mot de passe de la base de données
     */
    String pass = "lepetitcheval";

    /**
     * La connection à la base de données
     */
    private Connection connection;

    /**
     * Les statuts de la base de données
     */
    private boolean status;

    /**
     * La place d'un marqueur
     */
    String place;

    /**
     * Liste renvoyée
     */
    ArrayList returned;

    /**
     * Constructeur de DBAcces
     */
    public DBAcces() {
        connect();
        System.out.println("connection status:" + status);
    }

    /**
     * Connexion à la base
     * @return la connexion
     */
    public Connection connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(url, user, pass);
                    status = true;
                    System.out.println("connected:" + status);
                } catch (Exception e) {
                    status = false;
                    System.out.print(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            this.status = false;
        }
        return connection;
    }

    public Connection getExtraConnection() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupération de la place à partir d'une localisation
     * @param con connexion
     * @param lati latitude du point
     * @param longi longitude du point
     * @return le nom et l'ID de la place
     */
    public ArrayList locationToPlace(Connection con, double lati, double longi) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    returned = new ArrayList();
                    String query1 = "SELECT placelibelle, placeid FROM place where placelibelle = ? LIMIT 1;";
                    String query = "SELECT placelibelle, placeid FROM place "
                            + "ORDER BY ST_Distance(ST_SetSRID(ST_MakePoint(?,?),4326),place.geometry)"
                            + " LIMIT 1;";


                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setDouble(1, longi);
                    stmt.setDouble(2, lati);

                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    Integer placeid = rs.getInt("placeid");
                    place = rs.getString("placelibelle");

                    returned.add(place);
                    returned.add(placeid);
                    System.out.println(rs.getString("placelibelle"));

                    System.out.println("Connection fermée");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            this.status = false;
        }
        return returned;
    }

    /**
     * Ecritue d'un marqueur dans la base
     * @param con connexion
     * @param marker le marqueur a ecrire dans la base
     */
    public void writeMarker(Connection con, AmbianceMarker marker){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    GeoPoint location = marker.getLocation();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    int placeid = marker.getPlaceID();
                    java.sql.Date sqlDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
                    byte[] image = marker.getPhoto();
                    ArrayList<String> ambiances = marker.getAmbianceName();
                    ArrayList<Integer> scores = marker.getScores();

                    String queryAddLocation = "INSERT INTO image (imageid, image) VALUES (DEFAULT,?)" +
                            " RETURNING imageid";
                    PreparedStatement stmtAddLocation = con.prepareStatement(queryAddLocation);
                    stmtAddLocation.setBytes(1, image);
                    ResultSet rsAddLocation = stmtAddLocation.executeQuery();
                    rsAddLocation.next();
                    int imageid = rsAddLocation.getInt(1);


                    String queryAddMarker = "INSERT INTO marqueur(marqueurid, datecreation, imageid," +
                            "placeid, localisation) VALUES (DEFAULT,?,?,?,ST_SetSRID(ST_MakePoint(?,?),4326))" +
                            "RETURNING marqueurid";

                    PreparedStatement stmtAddMarker = con.prepareStatement(queryAddMarker);
                    stmtAddMarker.setDate(1,sqlDate);
                    stmtAddMarker.setInt(2,imageid);
                    stmtAddMarker.setInt(3,placeid);
                    stmtAddMarker.setDouble(4,longitude);
                    stmtAddMarker.setDouble(5,latitude);
                    ResultSet rsAddMarker = stmtAddMarker.executeQuery();
                    rsAddMarker.next();
                    int markerid = rsAddMarker.getInt(1);
                    int i = 0;
                    for (int score : scores){
                        System.out.println(score);
                        String queryAddAmbiance = "INSERT INTO decrit(marqueurid, motid, valeurmarqueur)" +
                                "VALUES (?,(SELECT motid from mot where motlibelle = LOWER(?)),?)";
                        PreparedStatement stmtAddAmbiance = con.prepareStatement(queryAddAmbiance);
                        stmtAddAmbiance.setInt(1,markerid);
                        stmtAddAmbiance.setString(2,ambiances.get(i));
                        stmtAddAmbiance.setInt(3,score);
                        stmtAddAmbiance.executeUpdate();
                        i += 1;
                    }
                    con.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            this.status = false;
        }
    }

    /**
     * Réalise un appel à la BDD pour remplir les attributs d'un AmbianceMarker
     * @param marqueur_id l'id du marqueur à chercher dans la BDD
     * @return l'AmbianceMarker de la bdd dont le marqueur_id correspond au paramètre d'entrée
     */
    public AmbianceMarker getMarkerById(int marqueur_id){
        AmbianceMarker currentAmbianceMarker = new AmbianceMarker();
        currentAmbianceMarker.setMarkerID(marqueur_id);
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    Connection con = connect();
                    System.out.println("Connection OK !");
                    String query = "SELECT ST_X(localisation), ST_Y(localisation), placelibelle, placeid, motlibelle, valeurmarqueur, datecreation, image "
                            +"FROM marqueur NATURAL JOIN place NATURAL JOIN mot NATURAL JOIN decrit NATURAL JOIN image "
                            +"WHERE marqueurid = ? "
                            + "ORDER BY marqueurid LIMIT 2;";

                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setDouble(1, marqueur_id);

                    ResultSet rs = stmt.executeQuery();

                    boolean created = false;
                    while( rs.next() ) {

                        if (!created){
                            double longi = (double) rs.getObject(1);
                            double lat = (double) rs.getObject(2);
                            GeoPoint location = new GeoPoint(lat, longi);
                            currentAmbianceMarker.setLocation(location);

                            String placelibelle = (String) rs.getObject(3);
                            currentAmbianceMarker.setPlaceName(placelibelle);

                            int placeid = (int) rs.getObject(4);
                            currentAmbianceMarker.setPlaceID(placeid);

                            ArrayList<String> mots = new ArrayList<String>();
                            String motlibelle = (String) rs.getObject(5);
                            mots.add(motlibelle);
                            currentAmbianceMarker.setAmbianceName(mots);

                            ArrayList<Integer> scores = new ArrayList<Integer>();
                            int valeurmarqueur = (int) rs.getObject(6);
                            scores.add(valeurmarqueur);
                            currentAmbianceMarker.setScores(scores);

                            Date date = (Date) rs.getObject(7);
                            currentAmbianceMarker.setDate(date);

                            // get image
                            byte[] image = (byte[]) rs.getObject(8);
                            currentAmbianceMarker.setPhoto(image);

                            int userID = 0;
                            currentAmbianceMarker.setUserID(userID);

                            created = true;
                        } else {
                            // Le marqueur existe dans la map : on ajoute le mot et le score correspondants
                            String motlibelle = (String) rs.getObject(5);
                            ArrayList<String> oldString = currentAmbianceMarker.getAmbianceName();
                            oldString.add(motlibelle);
                            currentAmbianceMarker.setAmbianceName(oldString);

                            int valeurmarqueur = (int) rs.getObject(6);
                            ArrayList<Integer> oldInt = currentAmbianceMarker.getScores();
                            oldInt.add(valeurmarqueur);
                            currentAmbianceMarker.setScores(oldInt);
                        }
                    }

                    con.close();
                    System.out.println("Connection fermée");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            this.status = false;
        }


        return currentAmbianceMarker;
    }

    /**
     * Réalise un appel à la BDD pour récupérer, pour chaque marqueur, son id et sa localisation
     * Ces informations sont stockées dans une hashMap existingMarkers, dont la clé correspond à l'id du marqueur
     * et la valeur à un GeoPoint correspondant à la position du marqueur
     * @return existingMarkers
     */
    public HashMap<Integer, GeoPoint> getLocationsAndMarkersID(){
        HashMap<Integer, GeoPoint> existingMarkers = new HashMap<Integer, GeoPoint>();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    Connection con = connect();
                    System.out.println("Connection OK !");
                    String query = "SELECT marqueurid, ST_X(localisation), ST_Y(localisation) "
                            +"FROM marqueur ORDER BY marqueurid;";

                    PreparedStatement stmt = con.prepareStatement(query);

                    ResultSet rs = stmt.executeQuery();

                    while( rs.next() ) {

                        int marqueurid = (int) rs.getObject(1);

                        if (!existingMarkers.containsKey(marqueurid)){
                            double longi = (double) rs.getObject(2);
                            double lat = (double) rs.getObject(3);
                            GeoPoint location = new GeoPoint(lat, longi);
                            existingMarkers.put(marqueurid, location);
                        }
                    }
                    con.close();
                    System.out.println("Connection fermée");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            this.status = false;
        }


        return existingMarkers;
    }

}




