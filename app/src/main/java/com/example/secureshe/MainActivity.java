package com.example.secureshe;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.secureshe.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private Button setup;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    private boolean isCallAttended = false;
    private call callStateReceiver;
    private ArrayList<String> mobileNumbers = new ArrayList<>();
    private static final String MOBILE_NUMBERS_KEY = "mobile_numbers";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private int volumeUpClickCount = 0;
    private long lastVolumeUpClickTime = 0;
    private boolean isToastShown = false;
    private boolean isCallInProgress = false;
    private boolean isSmsSent = false;
    private int currentPhoneNumberIndex = 0;
    private Location currentLocation;
    private LocationManager locationManager;

    private Switch switch1;
    private boolean isSwitchOn = false;
    private int volumeDownClickCount = 0;
    private long lastVolumeDownClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMobileNumbers();
         switch1 = findViewById(R.id.switch1);


            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        switch1.setText("Secured You");
                        isSwitchOn=true;


                    } else {
                        switch1.setText("Secure You");
                    }
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Check if the app has the required permissions
                if (!checkPermissions()) {
                    // Request the necessary permissions
                    requestPermissions();
                }
            }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        callStateReceiver = new call();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(callStateReceiver, intentFilter);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Permission already granted, start listening for location updates
                startLocationUpdates();
            }
            // Check and request camera permission if needed
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                // Permission already granted, initialize the camera

            }






    }
    private boolean checkPermissions() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        return locationPermission == PackageManager.PERMISSION_GRANTED &&
                callPermission == PackageManager.PERMISSION_GRANTED &&
                smsPermission == PackageManager.PERMISSION_GRANTED &&
                cameraPermission == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.CAMERA
        }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Handle the result of the permission request
            // You can check grantResults to see if the user granted the permissions
        }
    }

    public void showNumberInputDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Three Numbers");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText input1 = new EditText(this);
        final EditText input2 = new EditText(this);
        final EditText input3 = new EditText(this);

        input1.setInputType(InputType.TYPE_CLASS_NUMBER);
        input2.setInputType(InputType.TYPE_CLASS_NUMBER);
        input3.setInputType(InputType.TYPE_CLASS_NUMBER);

        input1.setHint("Number 1");
        input2.setHint("Number 2");
        input3.setHint("Number 3");

        layout.addView(input1);
        layout.addView(input2);
        layout.addView(input3);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number1 = input1.getText().toString();
                String number2 = input2.getText().toString();
                String number3 = input3.getText().toString();


                if (!TextUtils.isEmpty(number1) && !TextUtils.isEmpty(number2) && !TextUtils.isEmpty(number3)) {
                    mobileNumbers.clear();
                    mobileNumbers.add(number1);
                    mobileNumbers.add(number2);
                    mobileNumbers.add(number3);
                    Switch switch1 = findViewById(R.id.switch1);
                    Button btnGetNumbers = findViewById(R.id.btnGetNumbers);


                    saveMobileNumbers();
                    btnGetNumbers.setVisibility(View.GONE);
                    switch1.setVisibility(View.VISIBLE);

                } else {
                    showToast("Please enter three valid numbers");
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void saveMobileNumbers() {

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> mobileNumbersSet = new HashSet<>(mobileNumbers);
        editor.putStringSet(MOBILE_NUMBERS_KEY, mobileNumbersSet);
        editor.apply();
    }

    private void loadMobileNumbers() {

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        Set<String> mobileNumbersSet = preferences.getStringSet(MOBILE_NUMBERS_KEY, new HashSet<>());
        mobileNumbers.clear();
        mobileNumbers.addAll(mobileNumbersSet);
        Switch switch1 = findViewById(R.id.switch1);

        Button btnGetNumbers = findViewById(R.id.btnGetNumbers);
        if (!mobileNumbers.isEmpty()) {
            btnGetNumbers.setVisibility(View.GONE);
            switch1.setVisibility(View.VISIBLE);
        } else {
            btnGetNumbers.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveMobileNumbers();
    }



    private void startLocationUpdates() {
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0,
                        this
                );
            } else {
                showToast("GPS is not enabled");
            }
        } else {
            showToast("LocationManager is null");
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String mapsLink = generateGoogleMapsLink(latitude, longitude);

    }

    private String generateGoogleMapsLink(double latitude, double longitude) {
        String mapsLink = "https://www.google.com/maps?q=" + latitude + "," + longitude;
        return mapsLink;
    }
    protected void onDestroy() {
        super.onDestroy();
        // Stop location updates when the activity is destroyed
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        if (callStateReceiver != null) {
            unregisterReceiver(callStateReceiver);
        }
        if (mCamera != null) {
            mCamera.release();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            handleVolumeButtonClick();
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            handleVolumeDownButtonClick();
        }
        return super.dispatchKeyEvent(event);
    }

    private void handleCallEnded() {
        isCallAttended = false;

    }

    private void handleCallAnswered() {
        isCallAttended = true;

    }
    private void handleVolumeButtonClick() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastVolumeUpClickTime < 2000) {
            volumeUpClickCount++;

            if (volumeUpClickCount >= 3 && !isToastShown && !isSmsSent) {
                showToast("Volume up button clicked three times!");

                if (currentLocation != null) {

                    String mapsLink = generateGoogleMapsLink(currentLocation.getLatitude(), currentLocation.getLongitude());
                    callNextPerson();
                    sendSmsToAll("Check my location: " + mapsLink);
                    isSmsSent = true;
                } else {
                    showToast("Location not available");
                }

                volumeUpClickCount = 0;
                isToastShown = true;
            }
        } else {
            volumeUpClickCount = 1;
            isToastShown = false;
            isSmsSent = false;
        }

        lastVolumeUpClickTime = currentTime;
    }
    private boolean callNextPerson = true;
    private void callNextPerson() {
        if (!isCallInProgress) {
            if (callNextPerson) {
                isCallInProgress = true;
                String mobileNumber = getCurrentMobileNumber();
                makePhoneCall(mobileNumber);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        boolean isCallAttended = checkIfCallAttended();


                        isCallInProgress = false;

                        if (!isCallAttended) {
                            currentMobileNumberIndex = (currentMobileNumberIndex + 1) % mobileNumbers.size();
                            callNextPerson();
                            callNextPerson = true;
                        }
                        else {
                            callNextPerson = false;
                        }
                    }
                }, 25000); // 25 seconds delay
            }
        }
    }
    private int currentMobileNumberIndex = 0;

    private String getCurrentMobileNumber() {
        if (currentMobileNumberIndex < mobileNumbers.size()) {
            return mobileNumbers.get(currentMobileNumberIndex);
        } else {

            currentMobileNumberIndex = 0;
            return mobileNumbers.get(currentMobileNumberIndex);
        }
    }
    private boolean checkIfCallAttended() {
        return isCallAttended;
    }
    private void sendSmsToAll(String message) {
        for (String mobileNumber : mobileNumbers) {
            sendSms(mobileNumber, message);
        }
    }
    private void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            showToast("SMS sent successfully");
        } catch (Exception e) {
            showToast("Failed to send SMS");
            e.printStackTrace();
        }
    }

// ...



    private void makePhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber)); // Fix here

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            showToast("Call phone permission not granted");
        }
    }



    private void handleVolumeDownButtonClick() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastVolumeDownClickTime < 2000) {
            volumeDownClickCount++;

            if (volumeDownClickCount >= 3) {
                playAudio();
                volumeDownClickCount = 0;
            }
        } else {
            volumeDownClickCount = 1;
        }

        lastVolumeDownClickTime = currentTime;
    }
    private void playAudio() {
        if (isSwitchOn) {

            int audioResourceId = R.raw.audio;


            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioResourceId);


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    mediaPlayer.release();
                }
            });


            mediaPlayer.start();
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



    }







