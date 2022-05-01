package com.ninjadroid.app.webServices;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import java.math.*;

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

/**
 * a class to call the add_history endpoint in the server
 */
public class AddHistory {

    /**
     * sends the run history for a user's run to the server
     * duration expected in seconds. distance expected in miles
     * @param context
     * @param user_id user who did the run
     * @param datetime date of the run
     * @param calories calories burned during the run
     * @param duration duration of the run
     * @param distance total distance of the run
     * @param route_id routeID corresponding to the route the user followed/created
     */
    public static void sendHistoryUsingVolley(Context context, int user_id, String datetime, int calories,
                                               int duration, double distance, int route_id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getAddHistory())
                .appendQueryParameter("user_id", String.valueOf(user_id))
                .appendQueryParameter("datetime", datetime)
                .appendQueryParameter("calories", String.valueOf(calories))
                .appendQueryParameter("duration", String.valueOf(duration))
                .appendQueryParameter("distance", String.valueOf(distance))
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
        int points = (int)((distance * 100) + (calories * .85));
        System.out.println("POINTS" + points);
        updatePointsUsingVolley(context,points,user_id,String.valueOf(distance),calories);
    }

    /**
     *  sends the point information for a new run to the server
     * @param context
     * @param points the points to be added to the user's point total for this run
     * @param user_id ID of the user
     * @param dist the distance run
     * @param calories the calories burned
     */
    public static void updatePointsUsingVolley(Context context, int points, int user_id,String dist, int calories) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.updatePoints())
                .appendQueryParameter("points", String.valueOf(points))
                .appendQueryParameter("user_id", String.valueOf(user_id))
                .appendQueryParameter("dist", dist)
                .appendQueryParameter("cals", String.valueOf(calories));

        String url = builder.build().toString();
        Log.i("updatePoints", url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("updatePoints", response);
                        try{
                            Log.i("updatePoints", response);
                        } catch (Exception e) {
                            Log.e("updatePoints", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("updatePoints", error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
