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
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.proyecto1.Utilities.MapsEmptyActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GooglePlacesActivity extends AppCompatActivity {

    //DOCUMENTATION GOOGLE PLACES API:
    //https://developers.google.com/maps/documentation/places/android-sdk/client-migration

    //Elements
    ListView mlista;
    ArrayList<Restaurant> model = new ArrayList<>();
    ImageView contactos;
    ImageView fav;
    ImageView profile;
    ImageView logo;
    ImageView lupa;
    Switch swDisp;

    //Location
    //locationRequest with google
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private boolean settingsOK = false;
    //Places API
    private PlacesClient placesClient;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize attributes
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = createLocationRequest();
        locationCallback = createLocationCallBack();

        //Ask Permission
       // getSinglePermission.launch(ACCESS_FINE_LOCATION);
        //Check if GPS is ON
        checkLocationSettings();


        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyD4s1vv88uPw4DaALEPeRvQu5zLHU7RtTM");

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this);


        getCurrentPlace();

    }

    /*---------------------------------GOOGLE PLACES API---------------------------------*/

    private void getCurrentPlace()
    {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.RATING,
                Place.Field.TYPES,
                Place.Field.PHOTO_METADATAS,
                Place.Field.PRICE_LEVEL,
                Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setCurrentLocation();
            @SuppressLint("MissingPermission") Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful()){
                        setContentView(R.layout.activity_google_places);

                        //Inflate
                        mlista = findViewById(R.id.restaurantList);
                        startButtons();

                        FindCurrentPlaceResponse response = task.getResult();

                        ArrayList<Place.Type> placesTypes = getType();
                        ArrayList<PlaceLikelihood> likelihoods = getLikelihoodPlaces(response.getPlaceLikelihoods(), placesTypes);

                        for (PlaceLikelihood placeLikelihood : likelihoods) {
                            Restaurant restaurant = new Restaurant();
                            setRestaurantInfo(placeLikelihood, restaurant);
                            model.add(restaurant);
                        }

                        RestaurantsAdapter adapter = new RestaurantsAdapter(GooglePlacesActivity.this, R.layout.item_show_restaurants, model);
                        mlista.setAdapter(adapter);

                        mlista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent= new Intent(getBaseContext(), ShowRestaurantActivity.class);
                                putExtras(intent, i);
                                //Start activity
                                startActivity(intent);
                            }
                        });
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.i("PLACES", "Place not found: " + apiException.getStatusCode());
                        }
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            //getSinglePermission.launch(ACCESS_FINE_LOCATION);
            Toast.makeText(this, "Permiso no concedido", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MapsEmptyActivity.class);
            startActivity(intent);
        }
    }

    void setRestaurantInfo(PlaceLikelihood placeLikelihood, Restaurant restaurant){

        if (placeLikelihood.getPlace().getId() != null) {
            restaurant.setId(placeLikelihood.getPlace().getId());
        }

        if (placeLikelihood.getPlace().getName() != null) {
            restaurant.setName(placeLikelihood.getPlace().getName());
        }

        if (placeLikelihood.getPlace().getAddress() != null) {
            restaurant.setDir(placeLikelihood.getPlace().getAddress());
        }

        if (placeLikelihood.getPlace().getRating() != null) {
            restaurant.setRating(placeLikelihood.getPlace().getRating().toString());
        }

        if(placeLikelihood.getPlace().getLatLng() != null){
            restaurant.setLocation(placeLikelihood.getPlace().getLatLng());
        }

        if(placeLikelihood.getPlace().getTypes() != null){
            setCategories(restaurant, placeLikelihood);
        }

        if(placeLikelihood.getPlace().getPhotoMetadatas() != null){
            getPhotoFromMetaData(placeLikelihood.getPlace().getId(), placesClient, restaurant);
        }
    }

    @SuppressLint("MissingPermission")
    void setCurrentLocation(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i("TOASTACT", "Entra");

                updateLocation(new LatLng(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude()));

            }
        }, Looper.getMainLooper());
    }

    private void updateLocation(LatLng latLng){
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
                    }
                }
            }
        });
    }

    void setCategories(Restaurant restaurant, PlaceLikelihood placeLikelihood){
        String categories = "";
        int lenght = placeLikelihood.getPlace().getTypes().size();
        int j = 0;
        for (Place.Type type : placeLikelihood.getPlace().getTypes()){
            if(type != Place.Type.RESTAURANT) {
                if(j==lenght-1) {
                    categories += type.name().toLowerCase(Locale.ROOT).replace("_", " ");
                } else {
                    categories += type.name().toLowerCase(Locale.ROOT).replace("_", " ") + " - ";
                }
            }
            j++;
        }
        restaurant.setCategories(categories);
    }

    void getPhotoFromMetaData(String placeId, PlacesClient placesClient, Restaurant restaurant){
        final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();

            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("METADATA-PLACES", "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                storeRestaurantImage(restaurant, placeId, bitmap);
                restaurant.setPhotoMetadata(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e("PLACE-FOUND: ", "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
        });

    }

    void storeRestaurantImage(Restaurant restaurant, String id, Bitmap bitmap){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference uploader=storage.getReference("restaurant/"+id);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(GooglePlacesActivity.this.getContentResolver(), bitmap, "IMG_" + Calendar.getInstance().getTime(), null);
        Log.i("PATH-IMG", bitmap.toString());
        Uri imageUri = Uri.parse(path);

        uploader.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    ArrayList<PlaceLikelihood> getLikelihoodPlaces(List<PlaceLikelihood> places, ArrayList<Place.Type> types){
        ArrayList<PlaceLikelihood> placesReturned = new ArrayList<>();
        for (Place.Type type : types){
            for (PlaceLikelihood place : places){
                if(place.getPlace().getTypes().contains(type)){
                    placesReturned.add(place);
                }
            }
        }
        return placesReturned;
    }

    ArrayList<Place.Type> getType(){
        ArrayList<Place.Type> types = new ArrayList<>();
        if(getIntent().getStringExtra("tipo").equals("VEGETARIANO")){
            types.add(Place.Type.HEALTH);
            types.add(Place.Type.NATURAL_FEATURE);
        }
        if(getIntent().getStringExtra("tipo").equals("BAR")){
            types.add(Place.Type.BAR);
            types.add(Place.Type.NIGHT_CLUB);
            types.add(Place.Type.LIQUOR_STORE);
        }
        if(getIntent().getStringExtra("tipo").equals("PARRILLA")){
            types.add(Place.Type.RESTAURANT);
        }
        if(getIntent().getStringExtra("tipo").equals("POSTRES")){
            types.add(Place.Type.BAKERY);
        }
        if(getIntent().getStringExtra("tipo").equals("PARQUES")){
            types.add(Place.Type.PARK);
        }
        return types;
    }

    void putExtras(Intent intent, int i){
        //Restaurant name
        intent.putExtra("name", model.get(i).getName());

        //Restaurant photo
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] byteArray = null;
        if(model.get(i).getPhotoMetadata() != null) {
            model.get(i).getPhotoMetadata().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }
        intent.putExtra("image", byteArray);

        //Restaurant address
        intent.putExtra("address", model.get(i).getDir());

        //Restaurant types
        intent.putExtra("categories", model.get(i).getCategories());

        //Resturant raiting
        intent.putExtra("rating", model.get(i).getRating());

        //Resturant latlng
        intent.putExtra("location", model.get(i).getLocation().toString());

        intent.putExtra("id", model.get(i).getId());

    }

    void startButtons(){
        profile = findViewById(R.id.perfil_btn);
        contactos = findViewById(R.id.contactos_btn);
        fav = findViewById(R.id.favoritos_btn);
        lupa = findViewById(R.id.btn_search_menu);
        logo = findViewById(R.id.imageView3);
        swDisp = findViewById(R.id.available);

        mAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

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

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), PerfilActivity.class);
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

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), FavoriteActivity.class);
                startActivity(intent);
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkAvailability() {
        FirebaseDatabase.getInstance().getReference("users/" + mAuth.getCurrentUser().getUid() + "/disponible").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().getValue()!=null) {
                    if (task.getResult().getValue().toString().equals("true")) {
                        swDisp.setChecked(true);
                    } else {
                        swDisp.setChecked(false);
                    }
                }
            }
        });
    }

    void printPlaceDetailsById(String placeId)
    {
        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.RATING);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        // Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            Log.i("PLACES", "Place found: " + place.getName()
                    +" address: " + place.getAddress()
                    +" rating: "+place.getRating());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("PLACES", "Place not found: " + exception.getMessage());
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


    @Override
    protected void onPause() {
        super.onPause();
        //LOCATION
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //LOCATION
        startLocationUpdates();

    }


}