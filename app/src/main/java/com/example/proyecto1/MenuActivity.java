package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {
    ImageButton perfil, contactos, fav, bares, parrilla, veget, marina, desayuno, postres, lupa;
    Button location;
    EditText buscar, ubicacion_personal, ubicacion_contacto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        perfil = findViewById(R.id.perfil_btn);
        contactos = findViewById(R.id.contactos_btn);
        fav = findViewById(R.id.favoritos_btn);
        bares = findViewById(R.id.bar_btn);
        parrilla = findViewById(R.id.parrilla_btn);
        veget = findViewById(R.id.vegetariano_btn);
        marina = findViewById(R.id.marina_btn);
        desayuno = findViewById(R.id.desayuno_btn);
        postres = findViewById(R.id.postres_btn);
        lupa = findViewById(R.id.btn_search_menu);
        buscar = findViewById(R.id.edit_search_menu);
        location = findViewById(R.id.addLocationShow);

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(),PerfilActivity.class);
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

        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), ContactsActivity.class);
                startActivity(intent);
            }
        });

        parrilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), GooglePlacesActivity.class);
                startActivity(intent);
            }
        });

        lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), MostrarRestaurantesActivity.class);
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

    }
}