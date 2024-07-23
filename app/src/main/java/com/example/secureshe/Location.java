package com.example.secureshe;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Location extends AppCompatActivity implements LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request location permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, start listening for location updates
            getLocationAndSendSMS();
        }
    }

    private void getLocationAndSendSMS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Register for location updates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            // GPS is not enabled, show a message or prompt the user to enable it
            Log.e("Location", "GPS is not enabled");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start listening for location updates
                getLocationAndSendSMS();
            } else {
                // Permission denied, handle accordingly (show a message, etc.)
                Log.e("Location", "Permission denied");
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        // Handle the new location here
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);

        // Get the mobile number from your setup class or wherever you have it stored
        String phoneNumber = "9952672209";
        if (phoneNumber != null) {
            String message = "Current Location - Latitude: " + latitude + ", Longitude: " + longitude;
            sendSMS(phoneNumber, message);

            // Stop listening for location updates after sending the SMS
            stopLocationUpdates();
        } else {
            Log.e("SMS", "Mobile number not available");
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("SMS", "SMS sent to " + phoneNumber);
        } catch (Exception e) {
            Log.e("SMS", "Error sending SMS: " + e.getMessage());
        }
    }

    private void stopLocationUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not needed for this example
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Not needed for this example
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Not needed for this example
    }
}
