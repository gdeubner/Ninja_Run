package com.ninjadroid.app.activities.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.containers.LocationContainer;
import com.ninjadroid.app.webServices.PostRoute;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_UPDATE_INTERVAL = 10;  //in seconds
    private static final int FAST_LOCATION_UPDATE_INTERVAL = 2;  //in seconds
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker startMarker, endMarker;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    Button btn_start, btn_stop;
    ArrayList<LocationContainer> routeCoordinates;
    //true when route route is being recorded
    Boolean trackingRoute;
    Polyline drawnRoute;
    Boolean scrolling;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USERID = "userId";

    // TODO: Rename and change types of parameters
    private String mUserId;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId Parameter 1.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String userId) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(USERID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(USERID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setMappingFunctionality();
        setButtonAndTrackingFunctionality();
    }

    //sets the onclick listeners for the buttons
    private void setButtonAndTrackingFunctionality() {
        btn_start = getView().findViewById(R.id.btn_start);
        btn_stop = getView().findViewById(R.id.btn_stop);

        routeCoordinates = new ArrayList<>();
        trackingRoute = false;
        scrolling = false;

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!trackingRoute){
                    if(startMarker != null){
                        startMarker.remove();
                    }
                    if(endMarker != null){
                        endMarker.remove();
                    }
                    if(drawnRoute!=null){
                        drawnRoute.remove();
                    }
                    trackingRoute = true;
                    routeCoordinates = new ArrayList<>();

                    //Place current location marker
                    LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Start");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    startMarker = mGoogleMap.addMarker(markerOptions);

                    //sets the starting point for the route drawn on the screen
                    PolylineOptions options = new PolylineOptions()
                            .color(Color.BLUE)
                            .width(10)
                            .startCap(new RoundCap())
                            .endCap(new RoundCap())
                            .add(latLng);
                    drawnRoute = mGoogleMap.addPolyline(options);
                }
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
                    endMarker = mGoogleMap.addMarker(markerOptions);

                    if(routeCoordinates.size() > 3){
                        PostRoute.postRoute(routeCoordinates, getActivity().getBaseContext(), Integer.parseInt(mUserId) );
                    } else {
                        Toast.makeText(getView().getContext(), "Wow, that was quick.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getView().getContext(), "You haven't started a route yet.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
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

                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if(trackingRoute) {
                        LocationContainer lastLocation= new LocationContainer(
                                mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                mLastLocation.getAltitude(), mLastLocation.getSpeed(), mLastLocation.getTime());
                        routeCoordinates.add(lastLocation);
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        //MapFragment mapFragment = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

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
            if (ContextCompat.checkSelfPermission(getView().getContext(),
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
        if (ContextCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getView().getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use " +
                                "location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}