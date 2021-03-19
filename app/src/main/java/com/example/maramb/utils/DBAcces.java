package com.example.maramb.utils;

import org.osmdroid.util.GeoPoint;
import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBAcces {

    String url = "jdbc:postgresql://ser-info-03.ec-nantes.fr:5432/maramb";
    String user = "maramb";
    String pass = "lepetitcheval";
    private Connection connection;
    private boolean status;
    String place;

    public DBAcces() {
        connect();
        //this.disconnect();
        System.out.println("connection status:" + status);
    }


    private Connection connect() {
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

    public String locationToPlace(double lati, double longi) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    Connection con = connect();
                    String query1 = "SELECT placelibelle FROM place where placelibelle = ? LIMIT 1;";
                    String query = "SELECT placelibelle FROM place "
                            + "ORDER BY ST_Distance(ST_SetSRID(ST_MakePoint(?,?),4326),place.geometry)"
                            + " LIMIT 1;";


                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setDouble(1, longi);
                    stmt.setDouble(2, lati);

                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    place = rs.getString("placelibelle");
                    System.out.println(rs.getString("placelibelle"));

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
        return place;
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
}

