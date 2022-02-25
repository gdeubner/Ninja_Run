package com.ninjadroid.app.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninjadroid.app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.Utils;
import com.ninjadroid.app.utils.containers.RouteContainer;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {


    private static final int LOCATION_UPDATE_INTERVAL = 10;  //in seconds
    private static final int FAST_LOCATION_UPDATE_INTERVAL = 2;  //in seconds
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    Button btn_start, btn_stop;
    ImageButton ibtn_profile;
    ArrayList<Location> routeCoordinates;
    //true when route route is being recorded
    Boolean trackingRoute;
    Polyline drawnRoute;
    Boolean scrolling;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setTitle("Route Tracker");

        //sets up location tracking and map
        setMappingFunctionality();
        setButtonAndTrackingFunctionality();
    }

    //sets the onclick listeners for the buttons
    private void setButtonAndTrackingFunctionality() {
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);
        ibtn_profile = findViewById(R.id.ibtn_profile);

        routeCoordinates = new ArrayList<>();
        trackingRoute = false;
        scrolling = false;

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackingRoute = true;

                //Place current location marker
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Start");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //sets the starting point for the route drawn on the screen
                PolylineOptions options = new PolylineOptions()
                        .color(Color.BLUE)
                        .width(10)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap())
                        .add(latLng);
                drawnRoute = mGoogleMap.addPolyline(options);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trackingRoute){
                    trackingRoute = false;

                    //Place current location marker
                    LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Finish");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                    RouteContainer route = routeDetails();
                    postRoute(route);

                } else {
                    Toast.makeText(MapActivity.this, "You haven't started a route yet!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        ibtn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, ProfilePage.class);
                startActivity(intent);
            }
        });
    }

    //collects all of the route details and adds them to a RouteContainer
    private RouteContainer routeDetails() {
        double startLat, startLon, endLat, endLon;
        if(routeCoordinates.size()==0) {
            return null;
        }
         startLat = routeCoordinates.get(0).getLatitude();
         startLon = routeCoordinates.get(0).getLongitude();
         endLat = routeCoordinates.get(routeCoordinates.size()-1).getLatitude();
         endLon = routeCoordinates.get(routeCoordinates.size()-1).getLongitude();
         Geocoder geocoder = new Geocoder(this, Locale.getDefault());
         String cityName = "";
         String stateName = "";
         String countryName = "";
         List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(startLat, startLon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            cityName = addresses.get(0).getAddressLine(0);
            stateName = addresses.get(0).getAddressLine(1);
            countryName = addresses.get(0).getAddressLine(2);
        }

        double dist = Utils.calcDistanceTraveled(routeCoordinates);

        Log.i("route", String.format("startLat:%s StartLon:%s EndLat:%s EndLon:%s City:%s Distance:%s",
                startLat, startLon, endLat, endLon, cityName, dist));

        RouteContainer route = new RouteContainer();
        route.setVar_lat_start(startLat);
        route.setVar_long_start(startLon);
        route.setVar_lat_start(endLat);
        route.setVar_long_start(endLon);
        route.setVar_uid(17); //todo: change this once user functionality is done
        route.setVar_dist(dist);
        route.setVar_town(cityName);
        //converts the list of Location objects to json
        Log.i("Route", routeCoordinates.get(0).toString() );
        Type listType = new TypeToken<ArrayList<Location>>() {}.getType();
        String mRoute = new Gson().toJson(routeCoordinates, listType).replace('\"', '\'');
        route.setVar_routf(mRoute);
        Log.i("Route", route.getVar_routf() );


        ArrayList<Location> fixedList = new Gson().fromJson(mRoute.replace('\'', '\"'),
                listType);
        Log.i("Route", fixedList.get(0).toString() );


        return route;

    }

    //sends the route to the database
    private void postRoute(RouteContainer route) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getSendRoutePath())
                .appendQueryParameter("var_lat_start", String.valueOf(route.getVar_lat_start()))
                .appendQueryParameter("var_long_start", String.valueOf(route.getVar_long_start()))
                .appendQueryParameter("var_lat_end", String.valueOf(route.getVar_lat_end()))
                .appendQueryParameter("var_long_end", String.valueOf(route.getVar_long_end()))
                .appendQueryParameter("var_town", route.getVar_town())
                .appendQueryParameter("var_dist", String.valueOf(route.getVar_dist()))
                .appendQueryParameter("var_uid", String.valueOf(route.getVar_uid()))
                .appendQueryParameter("var_routf", route.getVar_routf());

        String myUrl = builder.build().toString();
        Log.i("Route", myUrl);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("Route", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Route", error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //creates the location callback which tells the app what to do when it receives new location data
    private void setMappingFunctionality() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);
                    Log.i("MapsActivity", "Location: " + location.getLatitude() + " " +
                            location.getLongitude());
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if(trackingRoute) {
                        routeCoordinates.add(mLastLocation);
                        Log.i("Route", "Route point: " + location.getLatitude() + " " +
                                location.getLongitude());
                        List<LatLng> newRoute = drawnRoute.getPoints();
                        newRoute.add(latLng);
                        drawnRoute.setPoints(newRoute);
                    }
                    if(!scrolling){
                        //move map camera
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                    }

                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //resets map to continue moving camera location to center around user
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                scrolling = false;
                return false;
            }
        });

        //todo: add a listener to prevent camera from resetting after user scrolls map

        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(1000 * FAST_LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted

                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use " +
                                "location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}