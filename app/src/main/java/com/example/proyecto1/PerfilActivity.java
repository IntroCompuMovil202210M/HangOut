package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class PerfilActivity extends AppCompatActivity {
    TextView txtName, txtUsuario, txtPhone, txtCorreo;
    ImageView fotoPerfil, btnFav, btnLogout;
    ImageButton btnMod;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("users");
    FirebaseAuth mAuth;
    private MyUser user;
    private StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_perfil);
        txtName=findViewById(R.id.editTextTextMultiLine);
        txtUsuario=findViewById(R.id.textView2);
        txtPhone=findViewById(R.id.editTextTextMultiLine2);
        txtCorreo=findViewById(R.id.editTextTextMultiLine3);
        fotoPerfil=findViewById(R.id.imageView2);
        btnMod= findViewById(R.id.editor);
        btnFav= findViewById(R.id.favBtn);
        btnLogout=findViewById(R.id.logOut);
        btnMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(), ModifyActivity.class);
                startActivity(intent);
            }
        });
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(), FavoriteActivity.class);
                startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            userRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(MyUser.class);
                    user.setID(dataSnapshot.getKey());
                    txtName.setText(user.getName() + " " +user.getLastName());
                    txtCorreo.setText(user.getMail());
                    txtUsuario.setText(user.getUserName());
                    txtPhone.setText(user.getPhoneNumber());
                    fotoPerfil.setImageURI(Uri.parse(user.getUrlImage()));

                }
                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }

}