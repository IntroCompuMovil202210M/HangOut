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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GooglePlacesActivity extends AppCompatActivity {

    //DOCUMENTATION GOOGLE PLACES API:
    //https://developers.google.com/maps/documentation/places/android-sdk/client-migration

    //Elements
    ListView mlista;
    ArrayList<Restaurant> model = new ArrayList<>();
    ImageButton contactos;
    ImageButton fav;
    ImageButton profile;
    ImageView logo;
    Button location;
    ImageView lupa;


    //Location
    //locationRequest with google
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private boolean settingsOK = false;
    //Places API
    private PlacesClient placesClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Initialize attributes
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = createLocationRequest();
        locationCallback = createLocationCallBack();

        //Ask Permission
        getSinglePermission.launch(ACCESS_FINE_LOCATION);
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
                        //Get the firsy placeID (CHANGE THIS)
                        final String placeId = response.getPlaceLikelihoods().get(0).getPlace().getId().toString();

                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            if(placeLikelihood.getPlace().getTypes().contains(Place.Type.RESTAURANT)) {
                                Restaurant restaurant = new Restaurant();

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

                                if(placeLikelihood.getPlace().getPhotoMetadatas() != null){
                                    final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(placeLikelihood.getPlace().getPhotoMetadatas().get(0))
                                            .setMaxWidth(500) // Optional.
                                            .setMaxHeight(300) // Optional.
                                            .build();
                                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                        restaurant.setPhotoMetadata(bitmap);
                                        Log.i("RESTAURANT-PHOTO", restaurant.getPhotoMetadata().toString());
                                    }).addOnFailureListener((exception) -> {
                                        if (exception instanceof ApiException) {
                                            final ApiException apiException = (ApiException) exception;
                                            //Log.e(TAG, "Place not found: " + exception.getMessage());
                                            final int statusCode = apiException.getStatusCode();
                                            // TODO: Handle error with given status code.
                                        }
                                    });
                                }
                                model.add(restaurant);
                            }
                            RestaurantsAdapter adapter = new RestaurantsAdapter(GooglePlacesActivity.this, R.layout.item_show_restaurants, model);
                            mlista.setAdapter(adapter);

                            location.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                                    int h = 0;
                                    for(Restaurant restaurant : model){
                                        intent.putExtra("location" + h, restaurant.getLocation());
                                        h++;
                                    }
                                    intent.putExtra("locatioSize", h);
                                    startActivity(intent);
                                }
                            });

                            mlista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent= new Intent(getBaseContext(), ShowRestaurantActivity.class);
                                    putExtras(intent, i);
                                    //Start activity
                                    startActivity(intent);
                                }
                            });
                        }
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
            getSinglePermission.launch(ACCESS_FINE_LOCATION);
        }
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
        intent.putExtra("location", model.get(i).getLocation());

    }

    void startButtons(){
        profile = findViewById(R.id.perfil_btn);
        contactos = findViewById(R.id.contactos_btn);
        fav = findViewById(R.id.favoritos_btn);
        lupa = findViewById(R.id.btn_search_menu);
        location = findViewById(R.id.addLocationShow);
        logo = findViewById(R.id.logoMostrarR);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasa a la pantalla principal.
                Intent intent= new Intent(getBaseContext(), ModifyActivity.class);
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