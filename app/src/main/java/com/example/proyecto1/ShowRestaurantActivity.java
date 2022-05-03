package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowRestaurantActivity extends AppCompatActivity {
    ImageButton perfil, contactos, fav;
    Button location, mapa;
    TextView restaurantName, restaurantAddress, restaurantRating, restaurantPrices, restaurantCategories;
    ImageView restaurnatPhoto;

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
        restaurantPrices = findViewById(R.id.restaurantShownPrices);
        restaurantCategories = findViewById(R.id.restaurantShownCategories);
        restaurnatPhoto = findViewById(R.id.restaurantShownPhoto);


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
                Intent intent = new Intent(getBaseContext(), AddressActivity.class);
                startActivity(intent);
            }
        });

        //mapa = findViewById(R.id.button);

        /*mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), AddressActivity.class);
                startActivity(intent);
            }
        });*/
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

        String prices =getIntent().getStringExtra("prices");
        if(prices == null){
            restaurantPrices.setText("-");
        } else {
            restaurantPrices.setText(prices);
        }

        String categories =getIntent().getStringExtra("categories");
        restaurantCategories.setText(categories);
    }
}