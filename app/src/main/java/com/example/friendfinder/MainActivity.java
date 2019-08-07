package com.example.friendfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendfinder.data.model.Position;
import com.example.friendfinder.data.model.User;
import com.example.friendfinder.data.remote.ApiService;
import com.example.friendfinder.data.remote.ApiUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG";
    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int MY_LOCATION_REQUEST_CODE = 110;
    //private static final int REQUEST_CHECK_SETTINGS = 555;
    private boolean requestingLocationUpdates = true;
    private CompositeDisposable mCompositeDisposable;
    private ApiService mApiService;
    private String myDeviceId;
    private User mUser;
    private Button mAddFriendButton;
    private Button mOpenMapButton;
    private TextView mPairingNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mCompositeDisposable = new CompositeDisposable();

        myDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, myDeviceId);

        mApiService = ApiUtils.getApiService();

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        else {
            Log.d(TAG, "MainActivity onCreate access granted");
            startLocationUpdates();
        }

        TextView myDeviceIdTextView = findViewById(R.id.myDeviceIdTextView);
        myDeviceIdTextView.setText("Your Device ID: " + myDeviceId);

        Button checkUserButton = findViewById(R.id.myCheckUserButton);
        checkUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getUser(myDeviceId);
            }
        });

        mAddFriendButton = findViewById(R.id.myAddFriendButton);
        mAddFriendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // avaa activity missä voi lisätä
                Intent in = new Intent(MainActivity.this, AddFriendActivity.class);
                startActivity(in);
            }
        });

        Button locationButton = findViewById(R.id.myGetLocationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fetchLastLocation();
            }
        });

        mOpenMapButton = findViewById(R.id.myOpenMapButton);
        mOpenMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(in);
            }
        });

        mAddFriendButton.setEnabled(false);
        mOpenMapButton.setEnabled(false);

        Log.d(TAG, "MainActivity onCreate end");
    }

    private void getUser(final String myDeviceId) {

        Single<List<User>> user = mApiService.getUser(myDeviceId);

        user.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<User> users) {

                        //Log.d(TAG, "getUser onSuccess");
                        if (users.size() > 0) {
                            Log.d(TAG, users.get(0).getDeviceId());
                            Log.d(TAG, users.get(0).getPairingNumber());

                            mUser = users.get(0);
                            Toast.makeText(MainActivity.this, "User found", Toast.LENGTH_LONG).show();
                            mAddFriendButton.setEnabled(true);
                            mOpenMapButton.setEnabled(true);

                            mPairingNumberTextView = findViewById(R.id.myPairingNumberTextView);
                            mPairingNumberTextView.setText("Your pairing number: " + users.get(0).getPairingNumber());
                        }
                        else {
                            Toast.makeText(MainActivity.this, "User not found, creating user", Toast.LENGTH_LONG).show();
                            mAddFriendButton.setEnabled(false);
                            mOpenMapButton.setEnabled(false);
                            saveUser(myDeviceId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "getUser onError");
                        e.printStackTrace();
                    }
                });
    }

    private void saveUser(final String myDeviceId) {

        Single<User> user = mApiService.createUser(new User(myDeviceId));

        user.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(User user) {

                        if (user == null) {
                            Log.d(TAG, "onSuccess: user == null");
                        }
                        else {
                            Log.d(TAG, user.toString());
                        }
                        Toast.makeText(MainActivity.this, "User saved", Toast.LENGTH_LONG).show();
                        mAddFriendButton.setEnabled(true);
                        mOpenMapButton.setEnabled(true);
                        saveLocation(new Position(null, 0.0, 0.0, myDeviceId));
                        getUser(myDeviceId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "saveUser onError");
                        e.printStackTrace();
                    }
                });
    }

    private void saveLocation(Position pos) {

        Single<Position> position = mApiService.createPosition(pos);

        position.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Position>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Position position) {

                        Log.d(TAG, "saveLocation onSuccess: position == null");
                        if (position == null) {
                            Log.d(TAG, "onSuccess: position == null");
                        }
                        else {
                            Log.d(TAG, position.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                        e.printStackTrace();
                    }
                });
    }

    private void fetchLastLocation() {
        try {
            Task<Location> task = fusedLocationClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        Toast.makeText(MainActivity.this, currentLocation.getLatitude() + " " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        //SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        //supportMapFragment.getMapAsync(MapsActivity.this);
                        UpdateUI(currentLocation.getLatitude() + " " + currentLocation.getLongitude() +
                                " " + currentLocation.getSpeed());
                    } else {
                        Toast.makeText(MainActivity.this, "No Location recorded", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //requestingLocationUpdates = true;
                Log.d(TAG, "MainActivity onRequestPermissionResult permission hyvaksytty");
                startLocationUpdates();

            }
        } else {
            // Permission was denied. Display an error message.
            //requestingLocationUpdates = false;
            Toast.makeText(this,"Location permission missing",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            Log.d(TAG, "MainActivity onResume");
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {

        // Create the location request to start receiving updates
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.myLooper());
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        Location targetLocation = new Location("");//provider name is unnecessary
        targetLocation.setLatitude(60.17d);//your coords of course
        targetLocation.setLongitude(24.93d);

        float distance = location.distanceTo(targetLocation) / 1000;

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + ", " +
                Double.toString(location.getLongitude()) + " " +
                String.format("%.2f", distance) + " km";
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        UpdateUI(msg);
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void UpdateUI(String msg) {
        TextView myTextView = findViewById(R.id.myLocationTextView);
        myTextView.setText(msg);
    }

    @Override
    protected void onDestroy() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
        super.onDestroy();
    }

}
