package com.example.maramb.utils;

import android.graphics.Bitmap;
import android.os.Bundle;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDate;
import java.util.List;

public class AmbianceMarker {

    private int markerID;

    private GeoPoint location;

    private String placeName;

    // Mots utilisé pour décrire l'endroit :
    private List<String> ambianceNames;

    // Scores pour chaque mot :
    private List<Integer> scores;

    private LocalDate date;

    private Bitmap photo;

    private int userID;

    public AmbianceMarker(int markerID, GeoPoint location, String placeName, List<String> ambianceName,  List<Integer> scores, LocalDate date, Bitmap photo, int userID){
        this.markerID = markerID;
        this.location = location;
        this.placeName = placeName;
        this.ambianceNames = ambianceNames;
        this.scores = scores;
        this.date = date;
        this.photo = photo;
        this.userID = userID;
    }

    public Integer getMarkerID(){return this.markerID;}

    public GeoPoint getLocation(){return this.location;}

    public String getPlaceName(){return this.placeName;}

    public List<String> getAmbianceName(){return this.ambianceNames;}

    public List<Integer> getScores(){return this.scores;}

    public LocalDate getDate(){return this.date;}

    public Bitmap getPhoto(){return this.photo;}

    public int getUserID(){return this.userID;}

    public void setLocation(GeoPoint location) {this.location = location;}

    public void setPlaceName(String placeName) {this.placeName = placeName;}

    public void setAmbianceName(List<String> ambianceNames) {this.ambianceNames = ambianceNames;}

    public void setScores(List<Integer> scores) {this.scores = scores;}

    public void setDate(LocalDate date) {this.date = date;}

    public void setPhoto(Bitmap photo) {this.photo = photo;}

    public void setUserID(int userID) {this.userID = userID;}
}
