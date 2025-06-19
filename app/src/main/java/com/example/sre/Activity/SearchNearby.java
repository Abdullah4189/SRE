package com.example.sre.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sre.Adapter.AdAdapter;
import com.example.sre.Domain.AdModel;
import com.example.sre.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchNearby extends AppCompatActivity {

    ArrayList<AdModel> nearbyAds = new ArrayList<>();

    TextView categoryTitle;
    RecyclerView recyclerView1;
    AdAdapter adapter;


    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_nearby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView1 = findViewById(R.id.recyclerView1);

        Intent get = getIntent();
//        Toast.makeText(this, selected_cat, Toast.LENGTH_SHORT).show();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        adapter = new AdAdapter(nearbyAds, SearchNearby.this);

        // attach to global ads list
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(adapter);

        getCurrentLocation();

    }

    private void fetchNearbyAds(double userLat, double userLong, AdAdapter adapter, NearbyAdsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ads").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult()) {
                    try {
                        Double adLatObj = doc.getDouble("latitude");
                        Double adLongObj = doc.getDouble("longitude");

                        if (adLatObj != null && adLongObj != null) {
                            double adLat = adLatObj;
                            double adLong = adLongObj;

                            float[] results = new float[1];
                            Location.distanceBetween(userLat, userLong, adLat, adLong, results);
                            float distanceInMeters = results[0];

                            if (distanceInMeters <= 5000) { // 5 km
                                AdModel ad = doc.toObject(AdModel.class);
                                if (ad != null) {
                                    nearbyAds.add(ad);
                                }
                            }
                        } else {
                            Log.w("AdFetchWarning", "Missing latitude or longitude in document: " + doc.getId());
                        }

                    } catch (Exception e) {
                        Log.e("AdFetchError", "Error parsing ad document: " + e.getMessage());
                    }
                }
                callback.onAdsFetched(nearbyAds);
            } else {
                Log.e("FirestoreError", "Failed to fetch ads: " + task.getException().getMessage());
                callback.onAdsFetched(nearbyAds); // Return whatever we have, even if empty
            }
        });
    }

    public interface NearbyAdsCallback {
        void onAdsFetched(ArrayList<AdModel> ads);
    }


    private void getCurrentLocation() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            // Ask for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            Log.d("GPS", "Lat: " + latitude + ", Long: " + longitude);

                            // Now call your fetchNearbyAds method
                            fetchNearbyAds(latitude, longitude, adapter, new NearbyAdsCallback() {
                                @Override
                                public void onAdsFetched(ArrayList<AdModel> ads) {
                                    // Handle ads
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Log.e("GPS", "Permission denied.");
            }
        }
    }
}