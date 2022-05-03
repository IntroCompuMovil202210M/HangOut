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
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
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
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GooglePlacesActivity extends AppCompatActivity {

    //DOCUMENTATION GOOGLE PLACES API:
    //https://developers.google.com/maps/documentation/places/android-sdk/client-migration

    //Elements
    ListView mlista;
    ArrayList<Restaurant> model = new ArrayList<>();

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
                Place.Field.ICON_URL);

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

                                model.add(restaurant);
                                Log.i("RESTAURANT-MODEL-IN", model.toString());

                                Log.i("RESTAURANT", restaurant.toString());

                                RestaurantsAdapter adapter = new RestaurantsAdapter(GooglePlacesActivity.this, R.layout.item_show_restaurants, model);
                                mlista.setAdapter(adapter);

                                Log.i("ADAPTER", adapter.toString());
                            }
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