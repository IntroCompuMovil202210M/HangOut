package com.example.proyecto1;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
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

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 101010;
    Button btnIngresar, btnHuella;
    ImageButton btnGoogle;
    ImageButton btnFacebook;
    TextView registrarse;
    EditText txtEmail;
    EditText txtPass;

    //firebase authentication
    private FirebaseAuth mAuth;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    ProgressDialog progressDialog;

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
        btnHuella=findViewById(R.id.huella);

        mAuth = FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);
        SharedPreferences sharedPreferences;

        //Start all buttons
        startButtons();
        sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        boolean isLogin= sharedPreferences.getBoolean("isLogin", false);
        if(isLogin){
            btnHuella.setVisibility(View.VISIBLE);
        }
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG |DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Fingerprint sensor not exist", Toast.LENGTH_SHORT).show();
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Sensor not avail or busy", Toast.LENGTH_SHORT).show();
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String email= sharedPreferences.getString("email","");
                String password= sharedPreferences.getString("password", "");
                signInFirebase(email, password);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
       btnHuella.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });

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
        progressDialog.setMessage("Login");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI
                            Log.d("EMAIL", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            SharedPreferences.Editor editor= getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("email",email);
                            editor.putString("password",password);
                            editor.putBoolean("isLogin",true);
                            editor.apply();
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