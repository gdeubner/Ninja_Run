package com.ninjadroid.app.webLogic;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.Utils;
import com.ninjadroid.app.utils.containers.LocationContainer;
import com.ninjadroid.app.utils.containers.RouteContainer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SendRoute {
    //collects all of the route details and adds them to a RouteContainer
    public static void sendNewRoute(ArrayList<LocationContainer> routeCoordinates, Context context, int uid){
        RouteContainer route = SendRoute.routeDetails(routeCoordinates, context, uid);
        postRoute(route, context);

    }

    public static RouteContainer routeDetails(ArrayList<LocationContainer> routeCoordinates, Context context, int uid) {
        double startLat, startLon, endLat, endLon;
        if(routeCoordinates.size()==0) {
            return null;
        }
        startLat = routeCoordinates.get(0).getLat();
        startLon = routeCoordinates.get(0).getLon();
        endLat = routeCoordinates.get(routeCoordinates.size()-1).getLat();
        endLon = routeCoordinates.get(routeCoordinates.size()-1).getLon();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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

        //Log.i("route", String.format("startLat:%s StartLon:%s EndLat:%s EndLon:%s City:%s Distance:%s",
        //        startLat, startLon, endLat, endLon, cityName, dist));

        RouteContainer route = new RouteContainer();
        route.setVar_lat_start(startLat);
        route.setVar_long_start(startLon);
        route.setVar_lat_start(endLat);
        route.setVar_long_start(endLon);
        route.setVar_uid(uid); //todo: change this once user functionality is done
        route.setVar_dist(dist);
        route.setVar_town(cityName);
        //converts the list of Location objects to json
        Type listType = new TypeToken<ArrayList<LocationContainer>>() {}.getType();
        String mRoute = new Gson().toJson(routeCoordinates, listType).replace('\"', '\'');
        route.setVar_routf(mRoute);
        Log.i("Route", route.getVar_routf() );


        return route;
        //this line will convert the locationlist back from a json to a list
        //ArrayList<LocationContainer> newList = new Gson().fromJson(mRoute.replace('\'', '\"'), listType);
    }

    //sends the route to the database
    private static void postRoute(RouteContainer route, Context context) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

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
                        Log.i("Get Request Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("Get Request Response", error.getMessage());

                } catch (Error e){
                    Log.e("Get Request Response", "Unspecified server error");
                }

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
