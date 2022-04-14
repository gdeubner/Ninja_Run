package com.ninjadroid.app.webServices;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.ninjadroid.app.webServices.callbacks.DirectionsCallback;
import com.ninjadroid.app.utils.containers.DirectionsContainers.DirectionsContainer;

public class GetDirections {

    public static void getWalkingDirections(Context context, LatLng origin, LatLng destination,
                                            final DirectionsCallback callBack){

        ApplicationInfo app = null;
        try {
            app = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle bundle = app.metaData;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .encodedAuthority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("directions")
                .appendPath("json")
                .appendQueryParameter("origin", origin.latitude+","+origin.longitude)
                .appendQueryParameter("destination", destination.latitude+","+destination.longitude)
                .appendQueryParameter("mode", "walking")
                .appendQueryParameter("key", bundle.getString("com.google.android.geo.API_KEY"));


        String url = builder.build().toString();
        Log.i("directions", url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            Log.i("directions", response);
                            //updateView(response, context, binding);
                            Gson gson = new Gson();
                            DirectionsContainer directions = gson.fromJson(response, DirectionsContainer.class);
                            callBack.onSuccess(directions);
                        } catch (Exception e) {
                            Log.e("directions", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("directions", error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}

