package com.ninjadroid.app.webServices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.utils.URLBuilder;

/**
 * a class to call the share_route endpoint in the server
 */
public class ShareRoute {

    public static void share(Context context, int routeId, int sharerId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Please specify who you'd like to share the route with");

        final EditText input1 = new EditText(context);
        input1.setHint("Username of Friend");

        input1.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input1);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inp_text = input1.getText().toString();
                queryInfo(context,Integer.toString(sharerId), inp_text,
                        Integer.toString(routeId));

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private static void queryInfo(Context context, String userID, String sharedUn, String routeID) {
        // Instantiate the RequestQueue.
        Log.i("Justin", "17"); //replace with userID
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getShareRoute())
                .appendQueryParameter("user_id", userID)
                .appendQueryParameter("shared_username",sharedUn)
                .appendQueryParameter("route_id",routeID); //replace with userID

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
                        if (response.equals("\"success\"")) {
                            Toast.makeText(context,"Successfully shared route with " + sharedUn,Toast.LENGTH_SHORT).show();
                        } else if (response.equals("\"duplicate\"")) {
                            Toast.makeText(context,"Route already shared with " + sharedUn,Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,"User " + sharedUn + " does not exist",Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("Get Request Response", error.getMessage());

                } catch (Exception e){
                    Log.e("Get Request Response", "Unspecified server error");
                }

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
