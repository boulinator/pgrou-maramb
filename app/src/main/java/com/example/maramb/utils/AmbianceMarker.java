package com.example.maramb.utils;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDate;

public class AmbianceMarker {

    private String placeName;

    // Mot utilisé pour décrire l'endroit :
    private String ambianceName;

    private GeoPoint location;

    private LocalDate date;

    private int userID;

    public AmbianceMarker(String placeName, String ambianceName, GeoPoint location, LocalDate date, int userID){
        this.placeName = placeName;
        this.ambianceName = ambianceName;
        this.location = location;
        this.date = date;
        this.userID = userID;
    }

    public String getPlaceName(){return this.placeName;}

    public String getAmbianceName(){return this.ambianceName;}

    public GeoPoint getLocation(){return this.location;}

    public LocalDate getDate(){return this.date;}

    public int getUserID(){return this.userID;}

    public void setPlaceName(String placeName) {this.placeName = placeName;}

    public void setAmbianceName(String ambianceName) {this.ambianceName = ambianceName;}

    public void setLocation(GeoPoint location) {this.location = location;}

    public void setDate(LocalDate date) {this.date = date;}

    public void setUserID(int userID) {this.userID = userID;}
}
