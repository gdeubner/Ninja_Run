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
import com.google.gson.Gson;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.webServices.callbacks.VolleyProfileCallback;
import com.ninjadroid.app.utils.containers.ProfileContainer;

/**
 * a class to call the get_profile endpoint in the server
 */
public class GetProfile {

    /**
     *requests a profile from the server
     * @param context
     * @param uid the userID of the profile
     * @param callBack
     */
    public static void getProfile(Context context, String uid,final VolleyProfileCallback callBack) {

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
                        try{
                            //String decoded = java.net.URLDecoder.decode(response, StandardCharsets.UTF_8.name());
                            //updateView(response, context, binding);
                            Gson gson = new Gson();
                            //Gson gson = new GsonBuilder().serializeNulls().create();
                            ProfileContainer profile = gson.fromJson(response, ProfileContainer.class);
                            callBack.onSuccess(profile);

                        } catch (Exception e) {
                            Log.e("getProfile", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Log.e("getProfile", error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}