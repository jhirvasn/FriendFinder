package com.example.friendfinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.friendfinder.data.model.Position;
import com.example.friendfinder.data.remote.ApiService;
import com.example.friendfinder.data.remote.ApiUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "DEBUG";
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location mLastKnownLocation;
    private Position mFriendPosition;
    private boolean mPermissionDenied = false;
    private CompositeDisposable mCompositeDisposable;
    private ApiService mApiService;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.mCompositeDisposable = new CompositeDisposable();

        mApiService = ApiUtils.getApiService();

        Log.d(TAG, "onCreate loppu");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady nyt");

        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        getDeviceLocation();
        //getFriendLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();

        }

    }

    private void getFriendLocation() {

    }

    private void getAllLocations() {

        Single<List<Position>> position = mApiService.getPositions();

        position.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Position>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Position> positions) {

                        if (positions.size() > 0) {
                            Log.d(TAG, positions.get(2).getLat().toString());
                            Log.d(TAG, positions.get(2).getLon().toString());
                            if (mMap != null) {
                                LatLng friend = new LatLng(positions.get(2).getLat().doubleValue(), positions.get(2).getLon().doubleValue());
                                mMap.addMarker(new MarkerOptions().position(friend).title("Friend's location")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(friend));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                        e.printStackTrace();
                    }
                });
    }

    private void getLocation() {

        Single<List<Position>> position = mApiService.getPosition(6);

        position.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Position>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Position> positions) {

                        if (positions.size() > 0) {
                            Log.d(TAG, positions.get(0).getLat().toString());
                            Log.d(TAG, positions.get(0).getLon().toString());

                            mFriendPosition = positions.get(0);

                            /*if (mMap != null) {
                                LatLng friend = new LatLng(positions.get(0).getLat().doubleValue(), positions.get(0).getLon().doubleValue());
                                mMap.addMarker(new MarkerOptions().position(friend).title("Friend's location")
                                        .snippet()
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(friend));
                                mMap.
                            }*/
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d(TAG, "onError");
                        e.printStackTrace();
                    }
                });
    }

    private void saveLocation() {

        Single<Position> position = mApiService.createPosition(new Position(null, 55.0, 26.1, 0));

        position.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Position>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Position position) {

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

    private void updateLocation(Position pos) {
        Single<Position> position = mApiService.updatePosition(1, pos);

        position.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Position>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Position position) {
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

    private void deleteLocation() {

        Single<Position> position = mApiService.deletePosition(10);

        position.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Position>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Position position) {
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
    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (!mPermissionDenied) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 5f));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        } else {
            // Permission was denied. Display an error message.
            Toast.makeText(this,"Location permission missing",Toast.LENGTH_SHORT).show();
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onDestroy() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
        super.onDestroy();
    }

    private void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        try {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper());
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void onLocationChanged(Location location) {

        getLocation();

        LatLng friend = null;

        if (mFriendPosition != null) {
            friend = new LatLng(mFriendPosition.getLat(), mFriendPosition.getLon());
        }

        Position myPos = new Position(null, location.getLatitude(), location.getLongitude(), 0);
        updateLocation(myPos);

        Location targetLocation = new Location("");
        if (mFriendPosition != null) {
            targetLocation.setLatitude(mFriendPosition.getLat());
            targetLocation.setLongitude(mFriendPosition.getLon());
        }

        float distance = location.distanceTo(targetLocation) / 1000;

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + ", " +
                Double.toString(location.getLongitude()) + " " +
                String.format("%.2f", distance) + " km";
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        if (friend != null && mMarker == null && mMap != null) {
            mMarker = mMap.addMarker(new MarkerOptions().position(friend).title("Friend's location")
                    .snippet("Distance: " + String.format("%.2f", distance) + " km")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(friend));
        }
        else if (friend != null && mMarker != null) {
            mMarker.setPosition(friend);
            mMarker.setSnippet("Distance: " + String.format("%.2f", distance) + " km");
        }

        Log.d(TAG, msg);

        //UpdateUI(msg);
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocationProviderClient != null)
            Log.d(TAG, "onStop");
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    /*@Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }*/

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    /*private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }*/
}
