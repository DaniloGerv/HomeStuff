package com.univpm.homestuff.entities;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Location {
    private double latitude;
    private double longitude;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String name;



    public Location(double latitude,double longitude,String address,String city,String state,String country,String postalCode,String name)
    {
        this.latitude=latitude;
        this.longitude=longitude;
        this.address=address;
        this.city=city;
        this.state=state;
        this.country=country;
        this.postalCode=postalCode;
        this.name=name;
    }

    public Location() {}

    public String getAddress(){
        return this.address;
    }
    public void setAddress(String val)
    {
        this.address=val;
    }

    public String getCity(){
        return this.city;
    }
    public void setCity(String val)
    {
        this.city=val;
    }

    public String getState(){
        return this.state;
    }
    public void setState(String val)
    {
        this.state=val;
    }

    public String getCountry(){
        return this.country;
    }
    public void setCountry(String val)
    {
        this.country=val;
    }

    public String getPostalCode(){
        return this.postalCode;
    }
    public void setPostalCode(String val)
    {
        this.postalCode=val;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String val)
    {
        this.name=val;
    }

    public double getLatitude(){
        return this.latitude;
    }
    public void setLatitude(double val) {this.latitude=val;}

    public double getLongitude(){
        return this.longitude;
    }
    public void setLongitude(double val) {this.longitude=val;}



}
