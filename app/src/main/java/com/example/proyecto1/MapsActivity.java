package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto1.Utilities.DirectionsJSONParser;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.proyecto1.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private static final double RADIUS_OF_EARTH = 6371;
    public static final String LOCATION_PERMISSION_NAME = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int LOCATION_PERMISSION_ID = 7;

    private double currentLocationLatitude;
    private double currentLocationLongitude;
    private boolean alreadyMoved = false;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private MarkerOptions currentUser;

    private ArrayList<Marker> markers;

    private SensorManager mgr;
    private TextView text;


    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    EditText address1, address2;
    FloatingActionButton fab1, ub1;
    Geocoder mGeocoder;
    String ubicacionAct ="";
    String ubicacionAct2 ="";
    static LatLng ubicacionRes;
    static LatLng ubicacionTwo;
    static String ubicacionOne;
    boolean myNotSend = false;
    boolean friendNotSend = false;

    boolean isOpen= false;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Verificar permisos de la ubicación
        requestPermission(this, LOCATION_PERMISSION_NAME, "¿Permite tener acceso a su ubicación?", LOCATION_PERMISSION_ID);

        //Inicializar botones
        address1 = findViewById(R.id.address1Anim);
        address2 = findViewById(R.id.address2Anim);
        fab1 = findViewById(R.id.plusAddress);
        ub1 = findViewById(R.id.centerUser);
        mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        //Inicializar marcadores
        markers = new ArrayList<Marker>();

        //Animaciones
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);

        //Obtener localización
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLocationRequest = createLocationRequest();

        //Generador de sensores
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //API de Google
        mGeocoder = new Geocoder(getBaseContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapUser);
        mapFragment.getMapAsync(this);

        //Seguimiento de posicion donde se analiza si hubo un movimiento de más de 2 metros
        mLocationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    if (!alreadyMoved) {
                        alreadyMoved = true;
                        currentLocationLatitude = location.getLatitude();
                        currentLocationLongitude = location.getLongitude();
                    } else {
                        if (location.getLongitude() != currentLocationLongitude || location.getLatitude() != currentLocationLatitude) {
                            if (distance(location.getLatitude(), location.getLongitude(), currentLocationLatitude, currentLocationLongitude) >= 2.0) {
                                currentLocationLatitude = location.getLatitude();
                                currentLocationLongitude = location.getLongitude();

                            }
                        }
                    }
                }
            }
        };

        //Busqueda con solo una dirección
        address1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String addressString = address1.getText().toString();
                    if (!addressString.isEmpty()) {
                        try {
                            List<Address> addresses = mGeocoder.getFromLocationName(addressString, 2);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address addressResult = addresses.get(0);
                                LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mMap != null) {
                                    createMarkerUser(position);
                                    drawRoute(position, ubicacionRes);
                                    mMap.addMarker(new MarkerOptions().position(ubicacionRes).title("Place").icon(bitmapDescriptorFromVector(MapsActivity.this,R.drawable.ic_baseline_location_on_24)));;
                                }
                            } else {Toast.makeText(MapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {Toast.makeText(MapsActivity.this, "La dirección 2 esta vacía", Toast.LENGTH_SHORT).show();}
                }
                return false;
            }

        });

        //Botón para ubicar a una segunda persona
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getBaseContext(), DisponiblesActivity.class);
                String addressString = address1.getText().toString();
                if (addressString.isEmpty()) {
                    Toast.makeText(MapsActivity.this, "Su ubicación esta vacía, oprima el botón de ubicación", Toast.LENGTH_LONG).show();
                }else {
                    ubicacionOne = address1.getText().toString();
                    startActivity(intent);
                }
            }
        });

        /**
         * Función para ubicar en la posición actual del usuario
         * TODO: mostrar el marcador
         */
        ub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    List<Address> addresses = mGeocoder.getFromLocation(currentLocationLatitude, currentLocationLongitude, 2);
                    System.out.println( addresses.toString());
                    if (!addresses.isEmpty()) {
                        Address addressResult = addresses.get(0);

                        address1.setText(addressResult.getAddressLine(0), TextView.BufferType.NORMAL);
                        LatLng mLocation = new LatLng(currentLocationLatitude, currentLocationLongitude);
                        if (mMap != null) {
                            createMarkerUser(mLocation);
                            drawRoute(mLocation, ubicacionRes);
                            mMap.addMarker(new MarkerOptions().position(ubicacionRes).title("Place").icon(bitmapDescriptorFromVector(MapsActivity.this,R.drawable.ic_baseline_location_on_24)));;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Función para ubicar la dos direcciones si son dos personas
     * TODO: Arreglar la función para generalizar
     */
    public LatLng searchAddress(String one){
        if (!one.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(one, 2);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                    return position;
                } else {Toast.makeText(MapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        return null;
    }

    //MAP ON READY
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        if(getIntent().getStringExtra("location") != null){
            Toast.makeText(MapsActivity.this, "Oprima el botón ubicado en la parte inferior", Toast.LENGTH_LONG).show();
            ubicacionAct = getIntent().getStringExtra("location");
            ubicacionRes =  new LatLng(Double.parseDouble(ubicacionAct.split(",")[0].replace("lat/lng: (","")),Double.parseDouble(ubicacionAct.split(",")[1].replace(")","")) );


        }else if(getIntent().getStringExtra("key") != null){
            System.out.println("AAAAAAAAAAAAAA"+ ubicacionRes+ "        BBBBBBBBBBBBBBBBBBB"+ ubicacionOne);
            System.out.println("KEY-VALUE " + getIntent().getStringExtra("key"));
            ubicacionAct2 = getIntent().getStringExtra("key");
            ubicacionTwo = new LatLng(Double.parseDouble(ubicacionAct2.split(",")[0]), Double.parseDouble(ubicacionAct2.split(",")[1]));
            animateFab();
            address1.setText(ubicacionOne);
            address2.setText(geoCoderSearchLatLang(ubicacionTwo));
            String addressString = address2.getText().toString();
            String add2 = address1.getText().toString();
            LatLng position1 = searchAddress(add2);
            LatLng position2 = searchAddress(addressString);
            if(position1!= null && position2!= null){
                createMarkerUser(position1);
                mMap.addMarker(new MarkerOptions().position(position2).title("Partner").icon(bitmapDescriptorFromVector(MapsActivity.this,R.drawable.ic_baseline_emoji_people_24)));
                System.out.println(ubicacionAct);
                drawRoute(position1, ubicacionRes);
                drawRoute(position2, ubicacionRes);
                mMap.addMarker(new MarkerOptions().position(ubicacionRes).title("Place").icon(bitmapDescriptorFromVector(MapsActivity.this,R.drawable.ic_baseline_location_on_24)));

                //Actualización en tiempo real
                getLocation();
            }else {Toast.makeText(MapsActivity.this, "Hubo un error", Toast.LENGTH_SHORT).show();}
        }



        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng mLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        }
                    }
                });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                //mMap.clear();

                LatLng start = new LatLng(currentLocationLatitude, currentLocationLongitude);

                drawRoute(start, latLng);

                double distance = distance(currentLocationLatitude,currentLocationLongitude, latLng.latitude, latLng.longitude);
                String messageToast = "Distancia: "+  distance/1000 +"km";
                Toast.makeText(getApplicationContext(), messageToast, Toast.LENGTH_LONG).show();
                mMap.addMarker(new MarkerOptions().position(latLng).title(geoCoderSearchLatLang(latLng)));

            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, LOCATION_PERMISSION_NAME) == PackageManager.PERMISSION_GRANTED) {
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
                    currentUser = new MarkerOptions().position(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude())).title("Marker in Current").icon(bitmapDescriptorFromVector(MapsActivity.this,R.drawable.ic_baseline_boy_24));
                    mMap.addMarker(currentUser);
                    getCurrentLocationOtherUser(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    drawRoute(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()), ubicacionRes);
                    updateLocation(new LatLng(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude()));


                }
            }, Looper.getMainLooper());
        }
    }


    private void getCurrentLocationOtherUser(Double lat, Double longitude){

        FirebaseDatabase.getInstance().getReference("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot snapshot : task.getResult().getChildren()){
                    Log.i("TOKEN-MAP", getIntent().getStringExtra("token"));
                    if(getIntent().getStringExtra("token").equals(snapshot.getValue(MyUser.class).getToken())){

                        if(snapshot.getValue(MyUser.class).getLatitude() != null) {

                            LatLng otherLocation = new LatLng(Double.parseDouble(snapshot.getValue(MyUser.class).getLatitude()), Double.parseDouble(snapshot.getValue(MyUser.class).getLongitude()));
                            mMap.addMarker(new MarkerOptions().position(otherLocation).title("Other").icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_baseline_emoji_people_24)));
                            mMap.addMarker(new MarkerOptions().position(ubicacionRes).title("Place").icon(bitmapDescriptorFromVector(MapsActivity.this,R.drawable.ic_baseline_location_on_24)));
                            drawRoute(otherLocation, ubicacionRes);


                            //NOTIFICACIONES
                            if(!friendNotSend) {
                                Double distancePoints;
                                distancePoints = distance(lat, longitude, ubicacionRes.latitude, ubicacionRes.longitude);
                                Log.i("DISTANCE", String.valueOf(distancePoints));
                                if (distancePoints <= 2000) {
                                    FCMSend.pushNotification(MapsActivity.this, getIntent().getStringExtra("token"), "¡Ya casi!", "Tu amigo está a punto de llegar al lugar.");
                                    friendNotSend = true;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void updateLocation(LatLng latLng){
        FirebaseAuth mAuth;
        //Firebase
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference("users/").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        MyUser user = task.getResult().getValue(MyUser.class);

                        user.setLatitude(String.valueOf(latLng.latitude));
                        user.setLongitude(String.valueOf(latLng.longitude));

                        FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid()).setValue(user);
                        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));*/

                        //NOTIFICACIONES
                        if(!myNotSend) {
                            Double distancePoints;
                            distancePoints = distance(latLng.latitude, latLng.longitude, ubicacionRes.latitude, ubicacionRes.longitude);

                            if (distancePoints <= 2000) {
                                FCMSend.pushNotification(MapsActivity.this, user.getToken(), "¡Ya casi!", "Estas a punto de llegar al lugar.");
                                myNotSend = true;
                            }

                        }
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        //Cambio de la interfaz del mapa según el sensor de luz
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mMap != null) {
                    if (event.values[0] < 230) {
                        Log.i("MAPS", "DARK MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.dark));
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.light));
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.i("LOCATION", "onSuccess location");
                        if (location != null) {
                            LatLng mLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        }
                    }
                });

        initView();

    }

    private LocationRequest createLocationRequest(){
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public void drawRoute(LatLng start, LatLng finish){

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(start, finish);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

    }

    public String geoCoderSearchLatLang(LatLng latLng){
        Geocoder mGeocoder = new Geocoder(getBaseContext()); //API de Google
        String address = " ";
        try {
            List<Address> addresses = mGeocoder.getFromLocation(latLng.latitude,latLng.longitude,2);
            Address addressResult = addresses.get(0);
            address = addressResult.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
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

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;

        }
    };

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

    //Funcion para crear el marcador en el mapa
    private void createMarkerUser(LatLng mLocation){
        if(mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(mLocation).title("Me").icon(bitmapDescriptorFromVector(this, R.drawable.ic_baseline_boy_24)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, 100, 100);
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    //Funcion para animación
    private void animateFab(){
        if (isOpen){
            fab1.startAnimation(fabOpen);
            address2.startAnimation(rotateForward);
            address2.setVisibility(View.INVISIBLE);
            isOpen=false;
        }else {
            fab1.startAnimation(fabClose);
            address2.startAnimation(rotateBackward);
            address2.setVisibility(View.VISIBLE);
            isOpen=true;
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        //Limpiar mapa
        mMap.clear();
        //Obtener la poscion del marcador
        LatLng position = marker.getPosition();
        //Verificar si solo hay una ubicacion del usuario
        String addressString = address2.getText().toString();
        String add2 = address1.getText().toString();

        try {
            List<Address> addresses = mGeocoder.getFromLocationName(add2, 2);
            List<Address> addresses2 = mGeocoder.getFromLocationName(addressString, 2);

            if (!addresses2.isEmpty() && !addresses.isEmpty() ) {
                Address addressResult = addresses.get(0);
                Address addressResult2 = addresses2.get(0);
                LatLng position1 = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                LatLng position2 = new LatLng(addressResult2.getLatitude(), addressResult2.getLongitude());
                if (mMap != null) {
                    drawRoute(position1, position);
                    drawRoute(position2, position);
                    createMarkerUser(position1);
                    mMap.addMarker(new MarkerOptions().position(position).title("Partner").icon(bitmapDescriptorFromVector(this,R.drawable.ic_baseline_emoji_people_24)));
                    mMap.addMarker(new MarkerOptions().position(position).title(geoCoderSearchLatLang(position)));
                }
            } else if(addresses2.isEmpty() && !addresses.isEmpty() ) {
                Address addressResult = addresses.get(0);
                LatLng position1 = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                if (mMap != null) {

                    drawRoute(position1, position);
                    createMarkerUser(position1);
                    mMap.addMarker(new MarkerOptions().position(position).title(geoCoderSearchLatLang(position)));
                }
            }else {Toast.makeText(MapsActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}

        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }


    ////FUNCIONAMIENTO DE GOOGLE MAPS
    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = new ArrayList<LatLng>();;
            PolylineOptions lineOptions = new PolylineOptions();;
            lineOptions.width(4);
            lineOptions.color(Color.RED);
            MarkerOptions markerOptions = new MarkerOptions();
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);

            }
            // Drawing polyline in the Google Map for the i-th route
            if(points.size()!=0)mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key="+ getResources().getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.i("URL", url);
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}