package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    ImageView perfil, contactos;
    ImageView logo;
    ListView mlista;
    Switch swDisp;

    ArrayList <Restaurant> restaurants = new ArrayList<>();

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        perfil = findViewById(R.id.perfil_btn);
        contactos = findViewById(R.id.contactos_btn);
        logo = findViewById(R.id.imageView3);
        swDisp = findViewById(R.id.available);

        mlista = findViewById(R.id.listFav);

        mAuth = FirebaseAuth.getInstance();

        checkAvailability();

        swDisp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swDisp.isChecked()){//SI SE PONE EN DISPONIBLE SE VA A LA BASE DE DATOS A PONER DISPONIBLE EN TRUE
                    FirebaseDatabase.getInstance().getReference("users/" + mAuth.getCurrentUser().getUid() + "/disponible").setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference("users/" + mAuth.getCurrentUser().getUid()  + "/disponible").setValue(false);
                }
            }
        });

        getRestaurants();

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), ModifyActivity.class);
                startActivity(intent);
            }
        });

        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), ContactsActivity.class);
                startActivity(intent);
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkAvailability() {
        FirebaseDatabase.getInstance().getReference("users/" + mAuth.getCurrentUser().getUid() + "/disponible").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().getValue().toString().equals("true")){
                    swDisp.setChecked(true);
                } else {
                    swDisp.setChecked(false);
                }
            }
        });
    }

    private void getRestaurants(){

        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid() + "/favorites").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()){
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    restaurant.setPhotoMetadata(StringToBitMap(restaurant.getBitmapString()));
                    restaurants.add(restaurant);
                }

                Log.i("REST-FAV ", restaurants.toString());
                RestaurantsAdapter adapter = new RestaurantsAdapter(FavoriteActivity.this, R.layout.item_show_restaurants, restaurants);
                mlista.setAdapter(adapter);

                mlista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent= new Intent(getBaseContext(), ShowRestaurantActivity.class);
                        putExtras(intent, i);
                        //Start activity
                        startActivity(intent);
                    }
                });
            }
        });
    }

    void putExtras(Intent intent, int i){
        //Restaurant name
        intent.putExtra("name", restaurants.get(i).getName());

        //Restaurant photo
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] byteArray = null;
        if(restaurants.get(i).getPhotoMetadata() != null) {
            restaurants.get(i).getPhotoMetadata().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }
        intent.putExtra("image", byteArray);

        //Restaurant address
        intent.putExtra("address", restaurants.get(i).getDir());

        //Restaurant types
        intent.putExtra("categories", restaurants.get(i).getCategories());

        //Resturant raiting
        intent.putExtra("rating", restaurants.get(i).getRating());

        //Resturant latlng
        intent.putExtra("location", restaurants.get(i).getLocation());

    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}