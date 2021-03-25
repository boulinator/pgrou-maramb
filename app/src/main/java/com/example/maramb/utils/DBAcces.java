package com.example.maramb.utils;

import com.example.maramb.MainActivity;
import com.example.maramb.R;

import android.graphics.Bitmap;

import org.osmdroid.util.GeoPoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.HashMap;
import java.util.List;

public class DBAcces {

    String url = "jdbc:postgresql://ser-info-03.ec-nantes.fr:5432/maramb";
    String user = "maramb";
    String pass = "lepetitcheval";
    private Connection connection;
    private boolean status;
    String place;
    ArrayList returned;

    public DBAcces() {
        connect();
        //this.disconnect();
        System.out.println("connection status:" + status);
    }


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

        return c;
    }

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
     * Récupérer un GeoPoint pour chaque marqueur de la bdd
     */
    public ArrayList<GeoPoint> getPlaces() {
        ArrayList<GeoPoint> ListGeopoints = new ArrayList<>();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    Connection con = connect();
                    String query = "SELECT ST_AsText(GeomFromEWKT(geometry)) FROM marqueursimple";

                    PreparedStatement stmt = con.prepareStatement(query);

                    ResultSet rs = stmt.executeQuery();

                    while( rs.next() ) {

                        String geom = (String) rs.getObject(1);

                        String delims = "[( )]";
                        String[] tokens = geom.split(delims);

                        double longi = Double.parseDouble(tokens[1]);
                        double lat = Double.parseDouble(tokens[2]);

                        GeoPoint geoPoint = new GeoPoint(lat, longi);
                        ListGeopoints.add(geoPoint);
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
        return ListGeopoints;
    }



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

    public HashMap<Integer, AmbianceMarker> getExistingMarkers(){
        HashMap<Integer, AmbianceMarker> existingMarkers = new HashMap<Integer, AmbianceMarker>;
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    Connection con = connect();
                    String query = "SELECT marqueur.marqueurid, marqueur.localisation, place.placelibelle, place.placeid, mot.motid, "
                                    +"decrit.valeurmarqueur, marqueur.datecreation, image.image "
                                    +"FROM marqueur, place, mot, decrit, image "
                                    +"WHERE (mot.motid=decrit.motid AND marqueur.marqueurid=decrit.marqueurid AND marqueur.imageid=image.imageid "
                                    +"AND marqueur.placeid=place.placeid) "
                                    +"ORDER BY decrit.marqueurid;";

                    PreparedStatement stmt = con.prepareStatement(query);

                    ResultSet rs = stmt.executeQuery();

                    while( rs.next() ) {

                        int marqueurid = (int) rs.getObject(1);

                        if (!existingMarkers.containsKey(marqueurid)){
                            // Le marqueur n'existe pas dans la map : on le cree
                            String geom = (String) rs.getObject(2);
                            String delims = "[( )]";
                            String[] tokens = geom.split(delims);
                            double longi = Double.parseDouble(tokens[1]);
                            double lat = Double.parseDouble(tokens[2]);
                            GeoPoint location = new GeoPoint(lat, longi);

                            String placelibelle = (String) rs.getObject(3);
                            int placeid = (int) rs.getObject(4);

                            ArrayList<String> mots = new ArrayList<String>();
                            String motlibelle = (String) rs.getObject(5);
                            mots.add(motlibelle);

                            ArrayList<Integer> scores = new ArrayList<Integer>();
                            int valeurmarqueur = (int) rs.getObject(6);
                            scores.add(valeurmarqueur);

                            LocalDate date = (LocalDate) rs.getObject(7);

                            // get image

                            AmbianceMarker currentAmbianceMarker = new AmbianceMarker();
                            existingMarkers.put(marqueurid, currentAmbianceMarker);
                        } else {
                            // Le marqueur existe dans la map : on ajoute le mot et le score correspondants
                            String motlibelle = (String) rs.getObject(5);
                            ArrayList<String> oldString = existingMarkers.get(marqueurid).getAmbianceName();
                            oldString.add(motlibelle);
                            existingMarkers.get(marqueurid).setAmbianceName(oldString);

                            int valeurmarqueur = (int) rs.getObject(6);
                            ArrayList<Integer> oldInt = existingMarkers.get(marqueurid).getScores();
                            oldInt.add(valeurmarqueur);
                            existingMarkers.get(marqueurid).setScores(oldInt);
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




