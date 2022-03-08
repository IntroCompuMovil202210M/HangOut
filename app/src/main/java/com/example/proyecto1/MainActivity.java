package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button btnIngresar;
    ImageButton btnGoogle;
    ImageButton btnFacebook;
    TextView registrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnIngresar=findViewById(R.id.ingresar);
        btnGoogle=findViewById(R.id.registarGoogle);
        btnFacebook=findViewById(R.id.registrarFacebook);
        registrarse=findViewById(R.id.registrarse);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla activity_logingoogle
                Intent intent= new Intent(getBaseContext(),LoginGoogle.class);
                startActivity(intent);

            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla activity_loginfacebook
                Intent intent=new Intent(getBaseContext(),LoginFacebook.class);
                startActivity(intent);
            }
        });
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla activity_registrarse
                Intent intent=new Intent(getBaseContext(),Registrar.class);
                startActivity(intent);
            }
        });
    }
}