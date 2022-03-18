package com.ninjadroid.app.webServices;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.containers.LocationContainer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddHistory {

    //duration expected in seconds. distance expected in miles
    public static void sendHistoryUsingVolley(Context context, int user_id, String datetime, int calories,
                                               int duration, double distance, int route_id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getAddHistory())
                .appendQueryParameter("user_id", String.valueOf(route_id))
                .appendQueryParameter("calories", String.valueOf(calories))
                .appendQueryParameter("duration_of_run", String.valueOf(duration))
                .appendQueryParameter("distance_run", String.valueOf(distance))
                .appendQueryParameter("route_id", String.valueOf(route_id));

        String url = builder.build().toString();
        Log.i("addHistory", url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("addHistory", response);
                        try{
                            Log.i("addHistory", response);
                        } catch (Exception e) {
                            Log.e("addHistory", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("addHistory", error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
