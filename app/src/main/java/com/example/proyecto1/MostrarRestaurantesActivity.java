package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MostrarRestaurantesActivity extends AppCompatActivity {
    ImageView imagen_1;
    ImageButton perfil, contactos, fav,lupa;
    EditText buscar, ubicacion_personal, ubicacion_contacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_restaurantes);
        perfil = findViewById(R.id.perfil_btn);
        contactos = findViewById(R.id.contactos_btn);
        fav = findViewById(R.id.favoritos_btn);
        lupa = findViewById(R.id.btn_search_menu);
        buscar = findViewById(R.id.edit_search_menu);
        ubicacion_personal = findViewById(R.id.location_personal);
        ubicacion_contacto = findViewById(R.id.location_partner);
        imagen_1 = findViewById(R.id.imageView34);

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

        imagen_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), ShowRestaurantActivity.class);
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
        /*
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), ContactsActivity.class);
                startActivity(intent);
            }
        });*/

    }
}