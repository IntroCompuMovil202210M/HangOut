package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddressActivity extends AppCompatActivity {

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
                //Ir a alguna pantalla que muestre varias opciones de restaurantes
            }
        });

    }
}