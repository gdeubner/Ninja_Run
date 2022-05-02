package com.ninjadroid.app.webServices;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.Utils;
import com.ninjadroid.app.utils.containers.LocationContainer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * a class to call the post_route endpoint in the server
 */
public class PostRoute {

    /**
     * a driver method to send new route data to the server
     * @param routeCoordinates an ArrayList of LocationContainer objects, storing the given route
     * @param context
     * @param uid uid of the user who created the route
     * @param calories calories burned while making the route
     * @param title title of the route
     * @param date date route was created
     */
    public static void postRoute(ArrayList<LocationContainer> routeCoordinates, Context context,
                                 int uid, int calories, String title, String date){
        String url = buildUrl(routeCoordinates, context, uid, title, date);
        postDataUsingVolley(context, url, routeCoordinates, uid, calories);
    }

    /**
     * creates the URl used to send the route data
     * @param routeCoordinates an ArrayList of LocationContainer objects, storing the given route
     * @param context
     * @param uid uid of the user who created the route
     * @param title title of the route
     * @param date date route was created
     * @return returns the url to be used to send the route data
     */
    private static String buildUrl(ArrayList<LocationContainer> routeCoordinates, Context context,
                                    int uid, String title, String date){
        Uri.Builder builder = new Uri.Builder();

        double startLat, startLon, endLat, endLon, altitudeChange;
        startLat = routeCoordinates.get(0).getLat();
        startLon = routeCoordinates.get(0).getLon();
        endLat = routeCoordinates.get(routeCoordinates.size()-1).getLat();
        endLon = routeCoordinates.get(routeCoordinates.size()-1).getLon();
        altitudeChange = Utils.calcElevationChange(routeCoordinates);

        //gets location address, town, state
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String startAddress = "";
        String cityName = "";
        String stateName = "";
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(startLat, startLon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            startAddress = addresses.get(0).getAddressLine(0);
            cityName = startAddress.split(",")[1].trim();
            stateName = startAddress.split(",")[2].trim();

        }

        double dist = Utils.calcDistanceTraveled(routeCoordinates );

        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getSendRoutePath())
                .appendQueryParameter("var_lat_start", String.valueOf(startLat))
                .appendQueryParameter("var_long_start", String.valueOf(startLon))
                .appendQueryParameter("var_lat_end", String.valueOf(endLat))
                .appendQueryParameter("var_long_end", String.valueOf(endLon))
                .appendQueryParameter("var_town", cityName)
                .appendQueryParameter("var_dist", String.valueOf(dist))
                .appendQueryParameter("var_uid", String.valueOf(uid))
                .appendQueryParameter("var_title", String.valueOf(title))
                .appendQueryParameter("var_date", String.valueOf(date))
                .appendQueryParameter("var_elevation", String.valueOf(altitudeChange));

        return builder.build().toString();
    }

    /**
     * sends the route data to the server. The ArrayList of LocationContainers is converted
     * to json and stored in the body of the Post request
     * @param context
     * @param url the url used to send the route
     * @param routeCoordinates an ArrayList of LocationContainer objects, storing the given route
     * @param uid the UID of the route creator
     * @param calories calories burned while creating the route
     */
    private static void postDataUsingVolley(Context context, String url, ArrayList<LocationContainer> routeCoordinates,
                                            int uid, int calories) {
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    int route_id = Integer.parseInt(response.substring(2, response.length()-2));

                    Toast.makeText(context, "Route saved!", Toast.LENGTH_SHORT).show();
                    AddHistory.sendHistoryUsingVolley(context, uid, Utils.formatDateTime(routeCoordinates.get(0).getTime()),
                            calories, Utils.getRunDuration(routeCoordinates), Utils.calcDistanceTraveled(routeCoordinates),
                            route_id);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(context, "Unable to save route: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                Type listType = new TypeToken<ArrayList<LocationContainer>>() {}.getType();
                String mRoute = new Gson().toJson(routeCoordinates, listType).replace('\"', '\'');
                params.put("route", mRoute);

                ArrayList<LocationContainer> newList = new Gson().fromJson(mRoute.replace('\'', '\"'), listType);

                return params;
            }
        };

        queue.add(request);
    }

}
