package com.example.proyecto1;

import android.graphics.Bitmap;

import com.google.android.libraries.places.api.model.PhotoMetadata;

public class Restaurant {
    String name;
    String dir;
    String rating;
    Bitmap photoMetadata;

    public Restaurant(){}

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

    public Bitmap getPhotoMetadata() {
        return photoMetadata;
    }

    public void setPhotoMetadata(Bitmap photoMetadata) {
        this.photoMetadata = photoMetadata;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                '}';
    }
}
