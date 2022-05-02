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
import com.ninjadroid.app.utils.containers.DirectionsContainers.DirectionsContainer;
import com.ninjadroid.app.utils.containers.RouteContainer;
import com.ninjadroid.app.utils.containers.UserRouteContainer;
import com.ninjadroid.app.webServices.callbacks.SearchRoutesCallback;

import java.nio.charset.StandardCharsets;

/**
 * a class to call the search_route endpoint in the server
 */
public class SearchRoutes {
    /**
     * queries the server for a list of routes that correspond to the search type and search
     * parameters
     * @param context
     * @param searchBy the mode of searching (route_id, route_name, username of creator, town route
     *                 was created in
     * @param searchParam the parameters of the search
     * @param callBack
     */
    public static void search(Context context, String searchBy, String searchParam,
                              final SearchRoutesCallback callBack){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getSearchRoutes())
                .appendQueryParameter("search_by", searchBy)
                .appendQueryParameter("search_param", searchParam);


        String url = builder.build().toString();
        Log.i("SearchRoutes", url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("SearchRoutes", response);
                        try{
                            String decoded = java.net.URLDecoder.decode(response, StandardCharsets.UTF_8.name());
                            //updateView(response, context, binding);
                            Gson gson = new Gson();
                            RouteContainer[] routes = gson.fromJson(decoded, RouteContainer[].class);
                            callBack.onSuccess(routes);
                        } catch (Exception e ) {

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    Log.e("SearchRoutes", error.getMessage());
                } catch (Exception e){
                    Log.e("SearchRoutes", "Unknown error on server");

                }
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
