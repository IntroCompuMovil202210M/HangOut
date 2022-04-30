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
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto1.Utilities.EmailPasswordVerifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.Authenticator;

public class MainActivity extends AppCompatActivity {
    Button btnIngresar;
    ImageButton btnGoogle;
    ImageButton btnFacebook;
    TextView registrarse;
    EditText txtEmail;
    EditText txtPass;

    //firebase authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Inflate
        btnIngresar = findViewById(R.id.ingresar);
        //btnGoogle = findViewById(R.id.registarGoogle);
        //btnFacebook = findViewById(R.id.registrarFacebook);
        registrarse = findViewById(R.id.registrarse);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);

        mAuth = FirebaseAuth.getInstance();

        //Start all buttons
        startButtons();


    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get current active User
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            //User is signed in
            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            txtEmail.setText("");
            txtPass.setText("");
        }
    }


    private void startButtons() {

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Try to log in
                String email = txtEmail.getText().toString().trim();
                String password = txtPass.getText().toString().trim();
                //If email and password are correctly written
                if (EmailPasswordVerifier.verifyEmail(email) && EmailPasswordVerifier.verifyPassword(password)) {
                    //Try to sign in on Firebase
                    Log.i("EMAIL", "Correo y pass bien");
                    signInFirebase(email,password);
                } else {
                    //Show toast and reset textfields
                    Toast.makeText(view.getContext(), "El email o la contraseña están mal escritos", Toast.LENGTH_LONG).show();
                    Log.i("EMAIL", "Correo y password mal");
                    txtEmail.setText("");
                    txtPass.setText("");
                }

            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla activity_registrarse
                Intent intent = new Intent(getBaseContext(), Registrar.class);
                startActivity(intent);
            }
        });

    }

    void signInFirebase(String email, String password) {
        //Try to sign in with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI
                            Log.d("EMAIL", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("EMAIL", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }


}