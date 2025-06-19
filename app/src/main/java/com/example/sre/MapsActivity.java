package com.example.sre;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapsActivity extends AppCompatActivity {

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;

    private GeoPoint confirmedLocation = null;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    double lat;
    double longt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(),
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_maps);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check permission & get location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getAndShowCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Handle user tap to add marker and share location (your existing listener)
        mapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                GeoPoint tappedPoint = (GeoPoint) mapView.getProjection().fromPixels(
                        (int) event.getX(), (int) event.getY());

                addMarker(tappedPoint, "Your Location");

                shareLocation(tappedPoint.getLatitude(), tappedPoint.getLongitude());

                v.performClick();
            }
            return true;
        });

        mapView.setOnClickListener(v -> {
            // Accessibility override
        });
    }

    private void getAndShowCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        GeoPoint currentPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        mapView.getController().setZoom(18.0);
                        mapView.getController().setCenter(currentPoint);

                        addMarker(currentPoint, "Current Location");

                        // Show confirmation dialog after 3 seconds
                        new Handler().postDelayed(() -> showConfirmDialog(currentPoint), 3000);
                    } else {
                        Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addMarker(GeoPoint point, String title) {
        mapView.getOverlays().clear();
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void showConfirmDialog(GeoPoint point) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Location")
                .setMessage("Is this your current location?\nLatitude: " + point.getLatitude() +
                        "\nLongitude: " + point.getLongitude())
                .setPositiveButton("Confirm", (dialog, which) -> {
                    confirmedLocation = point;  // Save coordinates here
                    lat = point.getLatitude();
                    longt = point.getLongitude();
                    saveLocationToPrefs(lat, longt);
                    onBackPressed();

                    Toast.makeText(this, "Location saved!", Toast.LENGTH_SHORT).show();
                    // You can now use confirmedLocation elsewhere in your app
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void shareLocation(double lat, double lon) {
        String uri = "http://maps.google.com/maps?q=" + lat + "," + lon;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Here's my location: " + uri);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAndShowCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveLocationToPrefs(double lat, double lon) {
        getSharedPreferences("location_prefs", MODE_PRIVATE)
                .edit()
                .putLong("latitude", Double.doubleToRawLongBits(lat))
                .putLong("longitude", Double.doubleToRawLongBits(lon))
                .apply();

        Log.d("latitude", String.valueOf(Double.doubleToRawLongBits(lat)));
    }

}
