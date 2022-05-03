package com.example.proyecto1;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class ShowRestaurantActivity extends AppCompatActivity {
    ImageButton perfil, contactos, fav;
    Button location, mapa;
    TextView restaurantName, restaurantAddress, restaurantRating, restaurantCategories;
    ImageView restaurnatPhoto, clickFav;
    MyUser user;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_restaurant);

        perfil = findViewById(R.id.perfil_btn);
        contactos = findViewById(R.id.contactos_btn);
        fav = findViewById(R.id.favoritos_btn);
        location = findViewById(R.id.addLocationShow);

        restaurantName = findViewById(R.id.restaurantShownName);
        restaurantAddress = findViewById(R.id.restaurantShownAddress);
        restaurantRating = findViewById(R.id.restaurantShownRating);
        restaurantCategories = findViewById(R.id.restaurantShownCategories);
        restaurnatPhoto = findViewById(R.id.restaurantShownPhoto);

        mapa = findViewById(R.id.showMap_btn);
        clickFav = findViewById(R.id.clickFav);

        mAuth = FirebaseAuth.getInstance();

        checkFav( getIntent().getStringExtra("address"), getIntent().getStringExtra("name"));

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

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), FavoriteActivity.class);
                startActivity(intent);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), MapsActivity.class);
                String locationRest = getIntent().getStringExtra("location");
                intent.putExtra("location", locationRest);
                startActivity(intent);
            }
        });

        clickFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickFav.getColorFilter() == null) {
                    clickFav.setColorFilter(Color.RED);
                    byte[] byteArray = getIntent().getByteArrayExtra("image");
                    Bitmap bmp = null;
                    if(byteArray != null) {
                        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    }

                    addToFavList(getIntent().getStringExtra("name"), getIntent().getStringExtra("address"), getIntent().getStringExtra("rating"), bmp);
                } else {
                    clickFav.setColorFilter(null);
                    removeOfFavList(getIntent().getStringExtra("address"), getIntent().getStringExtra("name"));
                }
            }
        });
    }

    private void addToFavList(String name, String address, String rating, Bitmap bmp) {

        //Update info
        FirebaseUser currentUser = mAuth.getCurrentUser();

        DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference("users/").child(currentUser.getUid());

        firebaseDB.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        user = task.getResult().getValue(MyUser.class);

                        Restaurant restaurant = new Restaurant();

                        //restaurant.setPhotoMetadata(bmp);
                        restaurant.setRating(rating);
                        restaurant.setDir(address);
                        restaurant.setName(name);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        restaurant.setBitmapString(new String(stream.toByteArray()));

                        FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid() + "/favorites").push().setValue(restaurant);
                    }
                }
            }
        });
    }

    private void removeOfFavList(String address, String name) {

        //Update info
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid() + "/favorites").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()){
                    if(snapshot.getValue(Restaurant.class).getDir().equals(address) && snapshot.getValue(Restaurant.class).getName().equals(name)){
                        snapshot.getRef().removeValue();
                    }
                }
            }
        });

    }

    private void checkFav(String address, String name){

        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid() + "/favorites").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()){
                    if(snapshot.getValue(Restaurant.class).getDir().equals(address) && snapshot.getValue(Restaurant.class).getName().equals(name)){
                        clickFav.setColorFilter(Color.RED);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String name = getIntent().getStringExtra("name");
        restaurantName.setText(name);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        if(byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            restaurnatPhoto.setImageBitmap(bmp);
            restaurnatPhoto.getLayoutParams().height = (int) ((324 * getBaseContext().getResources().getDisplayMetrics().density) + 0.5f);
            restaurnatPhoto.getLayoutParams().width = (int) ((322 * getBaseContext().getResources().getDisplayMetrics().density) + 0.5f);
        }

        String address = getIntent().getStringExtra("address");
        restaurantAddress.setText(address);

        String rating =getIntent().getStringExtra("rating");
        restaurantRating.setText(rating);

        String categories =getIntent().getStringExtra("categories");
        restaurantCategories.setText(categories);
    }

    private void retriveUser(String userId){

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
        myRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot = task.getResult();
                        user = dataSnapshot.getValue(MyUser.class);
                        Log.i("USUARIO-ON", user.toString());
                    } else {
                        //Log de error
                    }
                } else {
                    //Log de error
                }
            }
        });
    }
}