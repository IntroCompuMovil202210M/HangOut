package com.example.proyecto1;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
public class LoginGoogle extends AppCompatActivity{
    Button ingresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logingoogle);
        ingresar= findViewById(R.id.buttonIngresar);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pasa a la pantalla principal
                Intent intent= new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

    }
}
