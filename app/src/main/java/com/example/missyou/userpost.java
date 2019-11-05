package com.example.missyou;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class userpost {

    public String user_id;
    public String image_url;
    public String desc; //desc
    public String image_thumb;
    public String user_phone;
    public double longitude;
    public double latitude;

    public Date timestamp;
    public LatLng loc;


    // constructor
    public userpost(String user_id, String image_url, String desc, String image_thumb, String Phone, double latitude, double longitude, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
        this.user_phone = user_phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.loc = loc;
    }


    public userpost(){};

    // Geter and Setter++++++++

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getLongitude(){ return longitude;}

    public double getLatitude(){ return latitude; }

    public LatLng getLocation(){

        LatLng loc = new LatLng(longitude, latitude);
        return loc;
    }




    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }




}
