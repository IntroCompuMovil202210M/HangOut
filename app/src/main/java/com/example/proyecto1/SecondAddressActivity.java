package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SecondAddressActivity extends AppCompatActivity {

    Button remove;
    Button search;
    EditText address1;
    EditText address2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_address);

        //Inflate
        remove = findViewById(R.id.remove_location);
        search = findViewById(R.id.search_place);
        address1 = findViewById(R.id.address1_2);
        address2 = findViewById(R.id.address2);

        //Action when remove button clicked
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondAddressActivity.this, AddressActivity.class);
                startActivity(intent);
            }
        });

        //Action when search button clicked
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ir a alguna pantalla que muestre varias opciones de restaurantes
            }
        });
    }
}