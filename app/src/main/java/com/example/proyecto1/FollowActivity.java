package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto1.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FollowActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final double RADIUS_OF_EARTH = 6371;
    public static final String LOCATION_PERMISSION_NAME = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int LOCATION_PERMISSION_ID = 7;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Double distancePoints;
    private MarkerOptions currentUser;

    private double currentLocationLatitude;
    private double currentLocationLongitude;
    private boolean alreadyMoved = false;

    TextView distance;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        distance = findViewById(R.id.distancia);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

       // Toolbar toolbar = findViewById(R.id.toolBar);
        //setSupportActionBar(toolbar);

        //LOCALIZATION
        binding = ActivityMapsBinding.inflate(getLayoutInflater());

        //PERMISSION
        requestPermission(this, LOCATION_PERMISSION_NAME, "¿Permite tener acceso a su ubicación?", LOCATION_PERMISSION_ID);

        //Obtener localización
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();

        Geocoder mGeocoder = new Geocoder(getBaseContext()); //API de Google

        mLocationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    if (!alreadyMoved) {
                        Log.i("LOCATION", "Location update in the callback: " + location);
                        alreadyMoved = true;
                        currentLocationLatitude = location.getLatitude();
                        currentLocationLongitude = location.getLongitude();
                    } else {
                        if (location.getLongitude() != currentLocationLongitude || location.getLatitude() != currentLocationLatitude) {

                            if (distance(location.getLatitude(), location.getLongitude(), currentLocationLatitude, currentLocationLongitude) >= 2.0) {
                                Log.i("LOCATION", "Location update in the callback: " + location);
                                currentLocationLatitude = location.getLatitude();
                                currentLocationLongitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        };

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapUser2);
        mapFragment.getMapAsync(this);

        getLocation();
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase.getInstance().getReference("available").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()){
                    if(snapshot.getValue(MyUser.class).getMail().equals(currentUser.getEmail())){
                        //PONER EL SWITCH
                        // menu.findItem(R.id.availability).getIcon().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    }
                }
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    //UTILITIES
    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH* c;
        return Math.round(result*1000);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, 100, 100);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //FIREBASE
    private void addAvailable(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey().equals(mAuth.getUid())) {
                    FirebaseDatabase.getInstance().getReference("available").child(mAuth.getUid()).setValue(snapshot.getValue(MyUser.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void deleteAvailable(){
        //Update info
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase.getInstance().getReference("available").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()){
                    if(snapshot.getValue(MyUser.class).getMail().equals(currentUser.getEmail())){
                        snapshot.getRef().removeValue();
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(FollowActivity.this, LOCATION_PERMISSION_NAME) == PackageManager.PERMISSION_GRANTED) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(3000);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Log.i("TOASTACT", "Entra");
                    mMap.clear();
                    currentUser = new MarkerOptions().position(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude())).title("Marker in Current").icon(bitmapDescriptorFromVector(FollowActivity.this,R.drawable.ic_baseline_emoji_people_24));
                    mMap.addMarker(currentUser);
                    getCurrentLocationOtherUser(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                    updateLocation(new LatLng(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude()));

                }
            }, Looper.getMainLooper());
        }
    }

    private void getCurrentLocationOtherUser(Double lat, Double longitude){

        FirebaseDatabase.getInstance().getReference("available").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()){
                    Log.i("EMAILGET", getIntent().getStringExtra("otherEmail"));
                    if(snapshot.getValue(MyUser.class).getMail().equals(getIntent().getStringExtra("otherEmail"))){
                        Log.i("USER-P", snapshot.getValue(MyUser.class).toString());
                        if(snapshot.getValue(MyUser.class).getLatitude() != null) {
                            LatLng otherLocation = new LatLng(Double.parseDouble(snapshot.getValue(MyUser.class).getLatitude()), Double.parseDouble(snapshot.getValue(MyUser.class).getLongitude()));
                            mMap.addMarker(new MarkerOptions().position(otherLocation).title("Other").icon(bitmapDescriptorFromVector(FollowActivity.this, R.drawable.ic_baseline_emoji_people_24)));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                            distancePoints = distance(lat, longitude, Double.parseDouble(snapshot.getValue(MyUser.class).getLatitude()), Double.parseDouble(snapshot.getValue(MyUser.class).getLongitude()));
                            distance.setText("Distancia actual: " + distancePoints);
                        }
                    }
                }
            }
        });
    }

    private void updateLocation(LatLng latLng){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference("available/").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        MyUser user = task.getResult().getValue(MyUser.class);

                        user.setLatitude(String.valueOf(latLng.latitude));
                        user.setLongitude(String.valueOf(latLng.longitude));

                        FirebaseDatabase.getInstance().getReference("available/" + currentUser.getUid()).setValue(user);
                        FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid()).setValue(user);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("LOCATION", "onSuccess location");
                System.out.println(location.toString());
                LatLng mLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            }
        });
    }

    //Permissions
    @SuppressLint("MissingPermission")
    private void initView(){
        if(ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION_NAME) == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void requestPermission(Activity context, String permission, String justification, int id){
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)){
                Toast.makeText(context, justification, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_ID){
            initView();
        }
    }
}