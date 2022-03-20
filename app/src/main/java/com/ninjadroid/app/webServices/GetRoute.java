package com.ninjadroid.app.webServices;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ninjadroid.app.R;
import com.ninjadroid.app.databinding.ActivityRouteBinding;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.VolleyRouteCallback;
import com.ninjadroid.app.utils.containers.RouteContainer;

import java.nio.charset.StandardCharsets;

public class GetRoute {

    public static void getRoute(Context context, int route_id,
                                final VolleyRouteCallback callBack){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getGetRoutePath())
                .appendQueryParameter("route_id", String.valueOf(route_id));
        String url = builder.build().toString();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            String decoded = java.net.URLDecoder.decode(response, StandardCharsets.UTF_8.name());
                            //updateView(response, context, binding);
                            Gson gson = new Gson();
                            RouteContainer route = gson.fromJson(decoded, RouteContainer.class);
                            callBack.onSuccess(route);
                        } catch (Exception e) {
                            Log.e("getRoute", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("getRoute", error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
