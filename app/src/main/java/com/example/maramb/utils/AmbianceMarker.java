package com.example.maramb.utils;

import android.graphics.Bitmap;
import android.os.Bundle;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Array;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AmbianceMarker {

    private int markerID;

    private GeoPoint location;

    private String placeName;

    // Mots utilisé pour décrire l'endroit :
    private ArrayList<String> ambianceNames;

    // Scores pour chaque mot :
    private ArrayList<Integer> scores;

    private Date date;

    private byte[] photo;

    private int userID;

    private int placeID;

    public AmbianceMarker(int markerID, GeoPoint location, String placeName, ArrayList<String> ambianceName, ArrayList<Integer> scores, Date date, byte[] photo, int userID, int placeID) {

        this.markerID = markerID;
        this.location = location;
        this.placeName = placeName;
        this.ambianceNames = ambianceNames;
        this.scores = scores;
        this.date = date;
        this.photo = photo;
        this.userID = userID;
        this.placeID = placeID;
    }


    public Integer getMarkerID(){return this.markerID;}

    public GeoPoint getLocation(){return this.location;}

    public String getPlaceName(){return this.placeName;}

    public ArrayList<String> getAmbianceName(){return this.ambianceNames;}

    public ArrayList<Integer> getScores(){return this.scores;}

    public Date getDate(){return this.date;}

    public byte[] getPhoto(){return this.photo;}

    public int getPlaceID(){return this.placeID;}

    public int getUserID(){return this.userID;}

    public void setLocation(GeoPoint location) {this.location = location;}

    public void setPlaceName(String placeName) {this.placeName = placeName;}

    public void setAmbianceName(ArrayList<String> ambianceNames) {this.ambianceNames = ambianceNames;}

    public void setScores(ArrayList<Integer> scores) {this.scores = scores;}

    public void setDate(Date date) {this.date = date;}

    public void setPhoto(byte[] photo) {this.photo = photo;}

    public void setUserID(int userID) {this.userID = userID;}

    public void setPlaceID(int placeID){this.placeID = placeID;}
}
