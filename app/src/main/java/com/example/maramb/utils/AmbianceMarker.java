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

    /**
     * L'ID du marqueur
     */
    private int markerID;

    /**
     * La localisation du marqeur
     */
    private GeoPoint location;

    /**
     * Le nom de la place du marqueur
     */
    private String placeName;

    /**
     * Les ambiances utilisés pour décrire le marqueur
     */
    private ArrayList<String> ambianceNames;

    /**
     * Le score des ambiances
     */
    private ArrayList<Integer> scores;

    /**
     * La date de création du marqueur
     */
    private Date date;

    /**
     * La photo associée au marqueur
     */
    private byte[] photo;

    /**
     * L'ID de l'utilisateur
     */
    private int userID;

    /**
     * L'id de la place du marqueur
     */
    private int placeID;


    /**
     * Constructeur du marqueur vide
     */
    public AmbianceMarker(){}

    /**
     * Constructeur du marqueur avec paramètres
     * @param markerID
     * @param location
     * @param placeName
     * @param ambianceNames
     * @param scores
     * @param date
     * @param photo
     * @param userID
     * @param placeID
     */
    public AmbianceMarker(int markerID, GeoPoint location, String placeName, ArrayList<String> ambianceNames, ArrayList<Integer> scores, Date date, byte[] photo, int userID, int placeID) {

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

    public void setMarkerID(int id) {this.markerID = id;}

    public void setLocation(GeoPoint location) {this.location = location;}

    public void setPlaceName(String placeName) {this.placeName = placeName;}

    public void setAmbianceName(ArrayList<String> ambianceNames) {this.ambianceNames = ambianceNames;}

    public void setScores(ArrayList<Integer> scores) {this.scores = scores;}

    public void setDate(Date date) {this.date = date;}

    public void setPhoto(byte[] photo) {this.photo = photo;}

    public void setUserID(int userID) {this.userID = userID;}

    public void setPlaceID(int placeID){this.placeID = placeID;}
}
