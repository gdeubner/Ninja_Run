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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.maps.android.PolyUtil;
import com.ninjadroid.app.R;
import com.ninjadroid.app.webServices.callbacks.DirectionsCallback;
import com.ninjadroid.app.utils.Utils;
import com.ninjadroid.app.webServices.callbacks.RouteCallback;
import com.ninjadroid.app.utils.containers.DirectionsContainers.DirectionsContainer;
import com.ninjadroid.app.utils.containers.DirectionsContainers.Step;
import com.ninjadroid.app.utils.containers.LocationContainer;
import com.ninjadroid.app.utils.containers.ProfileContainer;
import com.ninjadroid.app.utils.containers.UserRouteContainer;
import com.ninjadroid.app.webServices.AddHistory;
import com.ninjadroid.app.webServices.GetDirections;
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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int LOCATION_UPDATE_INTERVAL = 10;  //in seconds
    private static final int FAST_LOCATION_UPDATE_INTERVAL = 2;  //in seconds
    private static final int ZOOM_DEFAULT = 16;
    private static final int ZOOM_IN = 19;
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
    boolean directionsAddedToMap;
    boolean followingRoute;
    boolean routeCompleted;
    Polyline drawnRoute;
    Polyline followedRoute;
    int routeId;
    ProfileContainer profile;
    Marker startRouteMarker;
    Marker endRouteMarker;
    Chronometer clock;
    long clockPauseTime;
    RadioGroup radioGroup;
    //int nextRouteIndex;
    Context context;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USERID = "userId";
    private static final String ROUTE_ID = "routeId";
    private static final String USER_PROFILE = "profile";


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
    public static MapFragment newInstance(String userId, int routeId, ProfileContainer profile) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(USERID, userId);
        args.putInt(ROUTE_ID, routeId);
        args.putSerializable(USER_PROFILE, profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(USERID);
            routeId = getArguments().getInt(ROUTE_ID);
            profile = (ProfileContainer) getArguments().getSerializable(USER_PROFILE);
            Log.i("MapFrag", "routeID" + routeId);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.map_title);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    //sets up the functionality for the whole fragment
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRadioGroupFunctionality();
        setMappingFunctionality();
        setButtonAndTrackingFunctionality();
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
        followingRoute = false;
        routeCompleted = false;

        //sets the start button functionality
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(followingRouteMode){
                    if(!running){
                        running = true;
                        Toast.makeText(getContext(), "Head to the green starting point!", Toast.LENGTH_SHORT).show();
                        routeCoordinates = new ArrayList<>();
                        clock.setBase(SystemClock.elapsedRealtime());
                        clock.start();
                        followingRoute = true;
                    } else {
                        Toast.makeText(getContext(), "Already on a run ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(!creatingRoute ){
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
                    } else {
                        Toast.makeText(getContext(), "Already creating a run", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

        //sets the stop button functionality
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create mode
                if(!followingRouteMode){
                    if(creatingRoute){
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
                    } else {
                        Toast.makeText(getView().getContext(), "You haven't started a run yet.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else { // following route mode
                    if (running) {
                        running = false;
                        startRunFinishedDialog();
                    } else {
                        Toast.makeText(getView().getContext(), "You haven't started a run yet.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //creates a dialog to be displayed when a user finished a run
    private void startRunFinishedDialog() {
        clockPauseTime = SystemClock.elapsedRealtime() - clock.getBase();
        clock.stop();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Congrats! Do you want to log this workout?")
                .setTitle("Run Finished");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //send run to history table
                followingRoute = false;
                //converts times to seconds
                long startTime = clock.getBase();
                long endTime = SystemClock.elapsedRealtime();
                int totalTime = (int)((endTime - startTime)/1000);
                clock.setBase(SystemClock.elapsedRealtime());
                clock.stop();
                AddHistory.sendHistoryUsingVolley(getContext(), Integer.parseInt(mUserId),
                        Utils.formatDateTime(routeCoordinates.get(0).getTime()),
                        Utils.simpleCalorieCalc(totalTime,profile.getWeight()), totalTime,
                        Utils.calcDistanceTraveled(routeCoordinates), routeId);
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                followingRoute = false;
                clock.setBase(SystemClock.elapsedRealtime());
                clock.stop();
                clearCreatedRoute();
                Toast.makeText(getContext(), "Discarded", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton("Continue Running", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(!routeCompleted){
                    followingRoute = true;
                }
                Toast.makeText(getContext(), "Keep on going!", Toast.LENGTH_SHORT).show();
                running = true;
                clock.setBase(SystemClock.elapsedRealtime() - clockPauseTime);
                clock.start();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //removes any markers and routes from th map
    private void clearCreatedRoute() {
        if(startMarker != null){
            startMarker.remove();
        }
        if(endMarker != null){
            endMarker.remove();
        }
        if(drawnRoute != null){
            drawnRoute.remove();
        }
    }

    //creates a dialog to be displayed when a user finished creating a route
    private void startRouteFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to save your route?")
                .setTitle("Route Complete");
        View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.send_route_dialogue_layout,
                (ViewGroup) getView(), false);

        EditText routeTitle = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                double startTime = routeCoordinates.get(0).getTime();
                double endTime = routeCoordinates.get(routeCoordinates.size()-1).getTime();
                int totalTime = (int) ((endTime - startTime)/1000);
                String title = routeTitle.getText().toString();

                PostRoute.postRoute(routeCoordinates, getActivity().getBaseContext(), Integer.parseInt(mUserId),
                        Utils.simpleCalorieCalc(totalTime, profile.getWeight()), title,
                        Utils.formatDateTime(routeCoordinates.get(0).getTime()));
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
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {

                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);

                    mLastLocation = location;

                    LatLng curLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if(creatingRoute || running) {
                        LocationContainer lastLocation= new LocationContainer(
                                mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                mLastLocation.getAltitude(), mLastLocation.getSpeed(),
                                mLastLocation.getTime(), mLastLocation.getElapsedRealtimeNanos());
                        routeCoordinates.add(lastLocation);
                        if(creatingRoute){
                            List<LatLng> newRoute = drawnRoute.getPoints();
                            newRoute.add(curLatLng);
                            drawnRoute.setPoints(newRoute);
                        }
                    }

                    if(initialMapLoad){
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, ZOOM_DEFAULT));

                        initialMapLoad = false;

                        if(routeId != -1){
                            Log.i("followRoute", "Setting followRouteMode");
                            ((RadioButton)radioGroup.getChildAt(1)).setChecked(true);
                        }
                        Log.i("followRoute", routeId + "");
                    }
                    if(!scrolling){

                        if(directionsAddedToMap && followingRoute && !routeCompleted){
                            List<LatLng> pointList = followedRoute.getPoints();
                            final double MIN_LAT_LNG_DIFF = 10;
                            if(Utils.distanceBetweenLatLng(pointList.get(pointList.size()-1),curLatLng)
                            < MIN_LAT_LNG_DIFF){ //checks if near end of route flag
                                routeCompleted = true;
                                startRunFinishedDialog();
                            } else {
                                //focus camera on user's next step in route
                                int indexOfClosestPoint = -1;
                                float smallestDistance = Float.MAX_VALUE;
                                for(int i = 0; i < pointList.size(); i++){
                                    LatLng LL = pointList.get(i);
                                    float tempDist = Utils.distanceBetweenLatLng(LL, curLatLng);
                                    if(tempDist < smallestDistance){
                                        indexOfClosestPoint = i;
                                        smallestDistance = tempDist;
                                    }
                                }
                                int nextRouteIndex = indexOfClosestPoint + 1;
                                if(nextRouteIndex >= pointList.size()){
                                    nextRouteIndex = pointList.size()-1;
                                }
                                setFollowingCameraPosition(curLatLng, pointList.get(nextRouteIndex));
                            }
                        } else {
                            //focus camera on user location
                            setCameraPosition(curLatLng);
                        }
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

    //sets the camera position and zoom for when creating a route
    private void setCameraPosition(LatLng curPos){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(curPos)
                    .zoom(ZOOM_DEFAULT)
                    .build();
            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mGoogleMap.animateCamera(cu);
    }

    //sets the camera zoom, bearing, angle and position for following a route
    private void setFollowingCameraPosition(LatLng curPos, LatLng routePnt){
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        LatLng oldCurPos = mGoogleMap.getCameraPosition().target;
        float bearing = mGoogleMap.getCameraPosition().bearing;
        final double MIN_LAT_LNG_DIFF = 0.000001;
        if(Utils.distanceBetweenLatLng(oldCurPos, curPos) > MIN_LAT_LNG_DIFF){
            bearing = Utils.findBearing2(curPos, routePnt);
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(curPos)      // Sets the center of the map to Mountain View
                .zoom(ZOOM_IN)                   // Sets the zoom
                .bearing(bearing)  //sets camera orientation
                .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active to save battery
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
    //once map is loaded, this sets up the functionality to get the user's location
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

    //requests location permissions if the app doesn't currently have them
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

    //sets the Creating Route / Following Route radio button functionality
    //This allows users to switch between creating a route or following a pre-chosen route
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
    }

    //sets the map to route-creation mode
    private void createRouteMode() {
        if (followedRoute != null){
            followedRoute.remove();
            endRouteMarker.remove();
            startRouteMarker.remove();

            Log.i("followRoute",followedRoute.toString());
        }
        if(mLastLocation != null){
            setCameraPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
        followingRouteMode = false;
        directionsAddedToMap = false;
        clock.setVisibility(View.INVISIBLE);
    }

    //sets the map to route-following mode
    private void followRouteMode(int routeId) {
        if(followingRouteMode){
            return;
        }
        if (followedRoute != null){
            followedRoute.remove();
        }
        followingRouteMode = true;
        clock.setVisibility(View.VISIBLE);
        //nextRouteIndex = 1;

        GetRoute.getRoute(getContext(), routeId, new RouteCallback() {
            @Override
            public void onSuccess(UserRouteContainer route) {
                List<LatLng> latLngFollowedRoute = getLatLngList(route.getRoute_f().substring(6, route.getRoute_f().length()-1));

                PolylineOptions options = new PolylineOptions()
                        .color(Color.BLUE)
                        .width(12)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap())
                        .addAll(latLngFollowedRoute);
                followedRoute = mGoogleMap.addPolyline(options);

                endRouteMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .anchor(0.26f, 0.9f)
                        .position(latLngFollowedRoute.get(latLngFollowedRoute.size()-1))
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_baseline_flag_24)));

                startRouteMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(latLngFollowedRoute.get(0))
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_circle_green)));
                addDirectionsToMap(followedRoute);

                if(mLastLocation != null){
                    setCameraPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }
            }
        });
    }

    //queries google maps api for directions to connecting current location to start of route to follow
    private void addDirectionsToMap(Polyline followedRoute) {
        Log.i("directions", "Trying to get directions!!!");
        Log.i("directions", mLastLocation.toString());
        LatLng currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        LatLng startRouteLatLng = new LatLng(followedRoute.getPoints().get(0).latitude,
                followedRoute.getPoints().get(0).longitude);
        GetDirections.getWalkingDirections(getContext(),currentLatLng,startRouteLatLng,
                new DirectionsCallback() {
                    @Override
                    public void onSuccess(DirectionsContainer directions) {
                        Log.i("directions", "Status code: " + directions.getStatus());
                        ArrayList<Step> directionsStepList = directions.getSteps();
                        for(int i = directionsStepList.size()-1; i >= 0; i--){
                            List<LatLng> newList = PolyUtil.decode(directionsStepList.get(i).getPolyline().getPoints());
                            List<LatLng> curList = followedRoute.getPoints();
                            curList.addAll(0, newList);
                            followedRoute.setPoints(curList);
                        }
                        directionsAddedToMap = true;
                        Toast.makeText(context, "Head to the green starting point!", Toast.LENGTH_SHORT).show();
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
        routeCoordinates = new Gson().fromJson(route, listType);
        List<LatLng> finalRoute = new ArrayList<>();

        for(LocationContainer loc : routeCoordinates) {
            LatLng ll = new LatLng(loc.getLat(), loc.getLon());
            finalRoute.add(ll);
        }
        return finalRoute;
    }
}