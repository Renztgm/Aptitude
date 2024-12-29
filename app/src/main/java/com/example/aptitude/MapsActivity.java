package com.example.aptitude;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RequestQueue requestQueue;
    private List<Place> placesList = new ArrayList<>();

    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Remove the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars()); // Hides the status bar
            }
        } else {
            // For devices below Android 11, use this method
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Remove the action bar (top navigation bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        requestQueue = Volley.newRequestQueue(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up the Toolbar and back button
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed()); // Handles the back button click
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        googleMap.setMyLocationEnabled(true); // Enable location layer

        // Get the last known location of the user
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                        // Fetch nearby cafes and libraries
                        fetchNearbyPlaces(userLocation);
                    } else {
                        LatLng defaultLocation = new LatLng(37.7749, -122.4194); // San Francisco
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
                        Toast.makeText(this, "Location unavailable", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchNearbyPlaces(LatLng location) {
        String apiKey = getString(R.string.api_key);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location.latitude + "," + location.longitude +
                "&radius=1000&type=cafe|library&key=" + apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");

                        placesList.clear();

                        // Loop through the results and add to placesList
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            JSONObject geometry = place.getJSONObject("geometry");
                            JSONObject locationJson = geometry.getJSONObject("location");

                            double lat = locationJson.getDouble("lat");
                            double lng = locationJson.getDouble("lng");
                            String name = place.getString("name");
                            String address = place.getString("vicinity");

                            Place placeObj = new Place(name, address, lat, lng);
                            placesList.add(placeObj);  // Add to placesList

                            // Add marker for the place on the map
                            LatLng placeLocation = new LatLng(lat, lng);
                            googleMap.addMarker(new MarkerOptions()
                                    .position(placeLocation)
                                    .title(name)
                                    .snippet(address)); // Display address as snippet
                        }

                        // Now pass the updated placesList to the fragment
                        PlacesListFragment placesListFragment = new PlacesListFragment(placesList);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, placesListFragment)
                                .commit();

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> Log.e(TAG, "Volley error: " + error.getMessage())
        );

        requestQueue.add(jsonObjectRequest);
    }

    public void moveToPlace(Place place) {
        LatLng selectedLocation = new LatLng(place.getLatitude(), place.getLongitude());

        // Move the camera to the selected place
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));

        // Clear any previous markers and add a marker for the selected place
        googleMap.clear(); // Clears all markers
        googleMap.addMarker(new MarkerOptions()
                .position(selectedLocation)
                .title(place.getName())
                .snippet(place.getAddress())); // Show place info as snippet
    }
}
