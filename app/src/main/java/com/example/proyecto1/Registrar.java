package com.example.proyecto1;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Registrar extends AppCompatActivity{
    Button btnIngresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        btnIngresar=findViewById(R.id.btnIngresa);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pasa a la pantalla principal
                //Intent intent= new Intent(getBaseContext(), );
                //startActivity(intent);
            }
        });

    }
}
