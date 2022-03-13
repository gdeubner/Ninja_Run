package com.ninjadroid.app.webServices;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.containers.ProfileContainer;

import java.util.ArrayList;

public class GetProfile {
    static ProfileContainer profileInfoIn;

    public static ProfileContainer getProfile(Context context, String uid) {
        Log.i("Query", "myUrl");
        getUserInfo(context, uid);

        return profileInfoIn;
    }
    private static void shareResult(String[] result){
        String userId = result[0].substring(2);
        String username = result[1].substring(1, result[1].length() - 1);
        String password = result[2].substring(1, result[2].length() - 1);
        String weight = result[3];
        String heightft = result[4];
        String heightin = result[5];
        String points = result[6];
        String calories = result[7];
        String distance = result[8];
        String name = result[9].substring(1, result[9].length() - 1);
        String isAdmin = result[10];
        ProfileContainer newProfile = new ProfileContainer(
                userId, username, password, weight, heightft, heightin,
                points, calories, distance, name, isAdmin
        );
        Log.i("Amyyyy", newProfile.getName());
        profileInfoIn = newProfile;
    }

    private static void getUserInfo(Context context, String uid) {
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getUserProfilePath())
                .appendQueryParameter("user_id", uid);

        String myUrl = builder.build().toString();
        Log.i("Query", myUrl);
        String message = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("Get Request Response", response);
                        String[] result = response.split(",");
//                        String userId = result[0].substring(2);
//                        String username = result[1].substring(1, result[1].length() - 1);
//                        String password = result[2].substring(1, result[2].length() - 1);
//                        String weight = result[3];
//                        String heightft = result[4];
//                        String heightin = result[5];
//                        String points = result[6];
//                        String calories = result[7];
//                        String distance = result[8];
//                        String name = result[9].substring(1, result[9].length() - 1);
//                        String isAdmin = result[10];
//                        ProfileContainer newProfile = new ProfileContainer(
//                                userId, username, password, weight, heightft, heightin,
//                                points, calories, distance, name, isAdmin
//                        );
//                        updateObject(newProfile);
                        Log.i("Amyyyy", result[0]);
                        shareResult(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("Get Request Response", error.getMessage());

                } catch (Exception e) {
                    Log.e("Get Request Response", "Unspecified server error");
                }

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private static void updateObject(ProfileContainer newProfile){
        profileInfoIn.setname(newProfile.getName());
        profileInfoIn.setUsername(newProfile.getUsername());
        profileInfoIn = newProfile;
    }
}