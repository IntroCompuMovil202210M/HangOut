package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MenuActivity extends AppCompatActivity {
    ImageView perfil, contactos, fav;
    Button parrilla, vegetariano, bar, marina, desayuno, postres;
    Switch swDisp;

    private static final String PATH_USERS = "users/";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private MyUser data;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        perfil = findViewById(R.id.perfil_btn);
        contactos = findViewById(R.id.contactos_btn);
        fav = findViewById(R.id.favoritos_btn);
        swDisp = findViewById(R.id.available);

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        storeToken();

        //Botones de categoría
        parrilla = findViewById(R.id.parrillaBtn);
        vegetariano = findViewById(R.id.vegetarianoBtn);
        bar = findViewById(R.id.barBtn);
        marina = findViewById(R.id.marinaBtn);
        desayuno = findViewById(R.id.desayunoBtn);
        postres = findViewById(R.id.postresBtn);

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
                intent.putExtra("tipo", "PARRILLA");
                startActivity(intent);
            }
        });

        vegetariano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.

                createNotification();
                Intent intent= new Intent(getBaseContext(), GooglePlacesActivity.class);
                intent.putExtra("tipo", "VEGETARIANO");
                startActivity(intent);
            }
        });

        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), GooglePlacesActivity.class);
                intent.putExtra("tipo", "BAR");
                startActivity(intent);
            }
        });

        postres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), GooglePlacesActivity.class);
                intent.putExtra("tipo", "POSTRES");
                startActivity(intent);
            }
        });

        marina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), GooglePlacesActivity.class);
                intent.putExtra("tipo", "PARQUES");
                startActivity(intent);
            }
        });

        desayuno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), GooglePlacesActivity.class);
                intent.putExtra("tipo", "DESAYUNO");
                startActivity(intent);
            }
        });
    }

    private void storeToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult();

                        //Update info
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference("users/").child(currentUser.getUid());

                        firebaseDB.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task2) {
                                if(task2.isSuccessful()) {
                                    if (task2.getResult().exists()) {
                                        FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid() + "/token").setValue(token);
                                    }
                                }
                            }
                        });
                    }
                });
    }

    private void createNotification(){

        String title = "Vegetariano Hang Out";
        String message = "Comida vegetariana en Hang Out";

        FCMSend.pushNotification(MenuActivity.this, "cP7iSHckRt6CU1MXyyy9Z-:APA91bH7QbPSeGRUGwCM2UcbfbZKF-2rO-XdV8ylSxQ1e_9tPC0fcyorfP1gAynloT9EY7g2IDe8F6xzXnBkBBFCquuISxAtYTVd5FOKXJj9sIJxCbfV5YGxdmgk90ZJifT1sVkElNJm", title, message);
    }

    private void checkAvailability() {
        FirebaseDatabase.getInstance().getReference("users/" + mAuth.getCurrentUser().getUid() + "/disponible").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().getValue() != null) {
                    if (task.getResult().getValue().toString().equals("true")) {
                        swDisp.setChecked(true);
                    } else {
                        swDisp.setChecked(false);
                    }
                }
            }
        });
    }
}