package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddressActivity extends AppCompatActivity {

    private static final double RADIUS_OF_EARTH = 6371;
    public static final String LOCATION_PERMISSION_NAME = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int LOCATION_PERMISSION_ID = 7;

    Button add_address;
    Button search;
    EditText address1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        //Inflate
        add_address = findViewById(R.id.remove_location);
        search = findViewById(R.id.search_place);
        address1 = findViewById(R.id.address1_2);

        //Action when add_address is clicked
        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, SecondAddressActivity.class);
                startActivity(intent);
            }
        });

        //Action when search is clicked
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this, MostrarRestaurantesActivity.class);
                startActivity(intent);
            }
        });

    }


}