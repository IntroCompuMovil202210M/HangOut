package com.example.proyecto1;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private boolean settingsOK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Initialize attributes
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = createLocationRequest();
        locationCallback = createLocationCallBack();
        perfil = findViewById(R.id.perfil_btn);
        contactos = findViewById(R.id.contactos_btn);
        fav = findViewById(R.id.favoritos_btn);
        swDisp = findViewById(R.id.available);

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        storeToken();
        //pedir permiso
        getSinglePermission.launch(ACCESS_FINE_LOCATION);
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
    /*---------------------------------LOCATION PERMISSIONS AND GPS---------------------------------*/

    //Ask for permission
    ActivityResultLauncher<String> getSinglePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result == true) { //granted
                        startLocationUpdates();

                    } else {//denied
                    }
                }
            });
    //Turn Location settings (GPS) ON
    ActivityResultLauncher<IntentSenderRequest> getLocationSettings = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i("LOCATION", "Result from settings:" + result.getResultCode());
                    if (result.getResultCode() == RESULT_OK) {
                        settingsOK = true;
                        startLocationUpdates();
                    } else {
                        //locationText.setText("GPS is unavailable");
                    }
                }
            }
    );

    //LOCATION
    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.i("LOCATION", "GPS is ON");
                settingsOK = true;
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (((ApiException) e).getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest isr = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                    getLocationSettings.launch(isr);
                } else {
                    //locationText.setText("No GPS available");
                }
            }
        });
    }

    private LocationRequest createLocationRequest() {
        LocationRequest request = LocationRequest.create().setFastestInterval(5000).setInterval(10000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }

    private LocationCallback createLocationCallBack() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Get Location from Google Services
                lastLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    String txt = "Latitude: " + lastLocation.getLatitude() + " ,Longitude: " + lastLocation.getLongitude();
                    Log.i("LOCATION", txt);
                }
            }
        };
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (settingsOK) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);//looper: cada cuanto quiere que lo haga
            }
        }
    }
}