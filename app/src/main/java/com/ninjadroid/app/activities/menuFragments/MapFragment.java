package com.ninjadroid.app.activities.menuFragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.Utils;
import com.ninjadroid.app.utils.VolleyRouteCallback;
import com.ninjadroid.app.utils.containers.LocationContainer;
import com.ninjadroid.app.utils.containers.RouteContainer;
import com.ninjadroid.app.webServices.AddHistory;
import com.ninjadroid.app.webServices.GetRoute;
import com.ninjadroid.app.webServices.PostRoute;

import java.lang.reflect.Type;
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
    private static final int ZOOM_DEFAULT = 16;
    private static final int ZOOM_IN = 16;//19;  todo this is temporary
    int zoomLevel;
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
    boolean creatingRoute; // only true after user clicked "Start Run"
    boolean followingRouteMode;
    boolean running; //only true when followingRouteMode==true and actively running
    boolean scrolling;
    boolean initialMapLoad;
    Polyline drawnRoute;
    Polyline followedRoute;
    int routeId;
    Marker startRouteMarker;
    Marker endRouteMarker;
    Chronometer clock;
    long clockPauseTime;

    RadioGroup radioGroup;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USERID = "userId";
    private static final String ROUTE_ID = "routeId";

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
    public static MapFragment newInstance(String userId, int routeId) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(USERID, userId);
        args.putInt(ROUTE_ID, routeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(USERID);
            routeId = getArguments().getInt(ROUTE_ID);
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
        setRadioGroupFunctionality();
    }

    //sets the onclick listeners for the buttons
    private void setButtonAndTrackingFunctionality() {
        btn_start = getView().findViewById(R.id.btn_start);
        btn_stop = getView().findViewById(R.id.btn_stop);
        clock = getView().findViewById(R.id.cro_clock);
        clock.setFormat("%s");
        clock.stop();

        routeCoordinates = new ArrayList<>();
        creatingRoute = false;
        scrolling = false;
        followingRouteMode = false;
        running = false;

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(followingRouteMode && !running){
                    running = true;
                    Toast.makeText(getContext(), "Run started", Toast.LENGTH_SHORT).show();
                    routeCoordinates = new ArrayList<>();
                    clock.setBase(SystemClock.elapsedRealtime());
                    clock.start();

                } else if(!creatingRoute ){
                    clearCreatedRoute();
                    creatingRoute = true;
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
                if(creatingRoute && !followingRouteMode){
                    if(routeCoordinates.size() > 3){
                        creatingRoute = false;

                        //Place current location marker
                        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("Finish");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        endMarker = mGoogleMap.addMarker(markerOptions);
                        //int calories = GetProfile.getProfile()
                        startRouteFinishDialog();
                    } else {//route was too short. remove all map objects and dont post route
                        drawnRoute.remove();
                        creatingRoute = false;
                        startMarker.remove();
                        Toast.makeText(getView().getContext(), "Wow, that was quick.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if(followingRouteMode && running){
                    running = false;
                    clock.stop();
                    clockPauseTime = SystemClock.elapsedRealtime();
                    startRunFinishedDialog();
                } else {
                    Toast.makeText(getView().getContext(), "You haven't started a run yet.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startRunFinishedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to log this workout?")
                .setTitle("Run Finished");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //send run to history table
                AddHistory.sendHistoryUsingVolley(getContext(), Integer.parseInt(mUserId),
                        Utils.formatDateTime(routeCoordinates.get(0).getTime()),
                        -1, Utils.getRunDuration(routeCoordinates),
                        Utils.calcDistanceTraveled(routeCoordinates), routeId);
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                clearCreatedRoute();
                Toast.makeText(getContext(), "Discarded", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton("Continue Running", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getContext(), "Keep on going!", Toast.LENGTH_SHORT).show();
                running = true;
                clock.setBase(clockPauseTime - SystemClock.elapsedRealtime() + clock.getBase());
                clock.start();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearCreatedRoute() {
        if(startMarker != null){
            startMarker.remove();
            endMarker.remove();
            drawnRoute.remove();
        }
    }

    private void startRouteFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to save your route?")
                .setTitle("Route Complete");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PostRoute.postRoute(routeCoordinates, getActivity().getBaseContext(), Integer.parseInt(mUserId) );
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                clearCreatedRoute();
                Toast.makeText(getContext(), "Discarded", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton("Continue Route", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                creatingRoute = true;
                endMarker.remove();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //creates the location callback which tells the app what to do when it receives new location data
    private void setMappingFunctionality() {
        zoomLevel = ZOOM_DEFAULT;
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {

                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);

                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if(creatingRoute || running) {
                        LocationContainer lastLocation= new LocationContainer(
                                mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                mLastLocation.getAltitude(), mLastLocation.getSpeed(),
                                mLastLocation.getTime(), mLastLocation.getElapsedRealtimeNanos());
                        routeCoordinates.add(lastLocation);
                        if(creatingRoute){
                            List<LatLng> newRoute = drawnRoute.getPoints();
                            newRoute.add(latLng);
                            drawnRoute.setPoints(newRoute);
                        }
                    }

                    if(initialMapLoad){
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                        initialMapLoad = false;
                    }
                    if(!scrolling){
                        //focus map on user location
                        setCameraPosition(latLng);
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

    private void setCameraPosition(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoomLevel)
                .build();
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mGoogleMap.animateCamera(cu);
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
    public void onResume() {
        super.onResume();
        initialMapLoad = true;
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

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason ==REASON_GESTURE) {
                    scrolling=true;

                }
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

    private void setRadioGroupFunctionality(){
        radioGroup = ((Activity)getContext()).findViewById(R.id.rg_mapMode);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i("followRoute", "Choosing route mode");
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);
                switch (index) {
                    case 0: // create route selected
                        if(running){
                            Toast.makeText(getContext(), "End current run first.", Toast.LENGTH_SHORT).show();
                            ((RadioButton)radioGroup.getChildAt(1)).setChecked(true);
                        } else {
                            createRouteMode();
                        }
                        break;
                    case 1: // follow route selected
                        if(routeId == -1){
                            Toast.makeText(getContext(), "No route selected", Toast.LENGTH_SHORT).show();
                            ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
                        } else {
                            if(creatingRoute){
                                Toast.makeText(getContext(), "End current run first", Toast.LENGTH_SHORT).show();
                                ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
                            } else {
                                followRouteMode(routeId);
                            }
                        }
                        break;
                }
            }
        });
        if(routeId != -1){
            Log.i("followRoute", "Setting followRouteMode");
            zoomLevel = ZOOM_IN;
            ((RadioButton)radioGroup.getChildAt(1)).setChecked(true);
        }
        Log.i("followRoute", routeId + "");

    }

    private void createRouteMode() {
        if (followedRoute != null){
            followedRoute.remove();
            endRouteMarker.remove();
            startRouteMarker.remove();
            Log.i("followRoute",followedRoute.toString());
        }
        zoomLevel = ZOOM_DEFAULT;
        if(mLastLocation != null){
            setCameraPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
        followingRouteMode = false;
        clock.setVisibility(View.INVISIBLE);
    }

    private void followRouteMode(int routeId) {
        if (followedRoute != null){
            followedRoute.remove();
        }
        followingRouteMode = true;
        zoomLevel = ZOOM_IN;
        clock.setVisibility(View.VISIBLE);
        if(mLastLocation != null){
            setCameraPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
        GetRoute.getRoute(getContext(), routeId, new VolleyRouteCallback() {
            @Override
            public void onSuccess(RouteContainer route) {
                List<LatLng> latLngRoute = getLatLngList(route.getRoute_f().substring(6, route.getRoute_f().length()-1));

//                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                latLngRoute.add(0, latLng);

                PolylineOptions options = new PolylineOptions()
                        .color(Color.BLUE)
                        .width(12)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap())
                        .addAll(latLngRoute);
                followedRoute = mGoogleMap.addPolyline(options);

                endRouteMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .anchor(0.26f, 0.9f)
                        .position(latLngRoute.get(latLngRoute.size()-1))
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_baseline_flag_24)));

                startRouteMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(latLngRoute.get(0))
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_circle_green)));
            }
        });

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private List<LatLng> getLatLngList(String route) {

        Type listType = new TypeToken<ArrayList<LocationContainer>>() {}.getType();
        route = route.replace('\'', '\"');
        ArrayList<LocationContainer> locationList = new Gson().fromJson(route, listType);
        List<LatLng> finalRoute = new ArrayList<>();

        for(LocationContainer loc : locationList) {
            LatLng ll = new LatLng(loc.getLat(), loc.getLon());
            finalRoute.add(ll);
        }
        return finalRoute;
    }


}