package com.example.proyecto1;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;

import java.net.URL;

public class Restaurant {
    String name;
    String dir;
    String rating;
    String categories;
    String bitmapString;
    Bitmap photoMetadata;
    LatLng location;
    byte[] byteArray;
    URL imageURL;
    String id;

    public  Restaurant(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public Bitmap getPhotoMetadata() {
        return photoMetadata;
    }

    public void setPhotoMetadata(Bitmap photoMetadata) {
        this.photoMetadata = photoMetadata;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getBitmapString() {
        return bitmapString;
    }

    public void setBitmapString(String bitmapString) {
        this.bitmapString = bitmapString;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public void setImageURL(URL imageURL) {
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                '}';
    }
}
