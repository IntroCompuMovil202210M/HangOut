package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ModifyActivity extends AppCompatActivity {

    ImageView fav, location, logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        //Inflate
        fav = findViewById(R.id.favBtn);
        location = findViewById(R.id.locationBtn);
        logOut = findViewById(R.id.logOut);

        //Action when fav button is clicked
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModifyActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        //Action when location button is clicked
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModifyActivity.this, AddressActivity.class);
                startActivity(intent);
            }
        });

        //Action when logOut button is clicked
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}