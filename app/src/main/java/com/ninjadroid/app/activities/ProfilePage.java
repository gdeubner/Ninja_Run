package com.ninjadroid.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.URLBuilder;

public class ProfilePage extends AppCompatActivity {
    public static final String KEY = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Intent intent = getIntent();

        String userID = intent.getStringExtra((MapActivity.KEY));
        queryInfo(getBaseContext(), userID);

        final Button mapButton = findViewById(R.id.map);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfilePage.this, MapActivity.class);

                intent.putExtra(KEY, userID);
                startActivity(intent);

            }
        });

    }
    private void queryInfo(Context context, String userID) {
        // Instantiate the RequestQueue.
        Log.i("Amy", userID);
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getUserProfilePath())
                .appendQueryParameter("user_id", userID);

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

                        final TextView nameView = findViewById(R.id.nameP);
                        final TextView usernameView = findViewById(R.id.usernameP);
                        final TextView userIDView = findViewById(R.id.UserIDP);
                        final TextView weightView= findViewById(R.id.weightP);
                        final TextView heightView= findViewById(R.id.heightP);
                        final TextView pointsView= findViewById(R.id.pointsP);
                        final TextView totCalView= findViewById(R.id.totalCaloriesP);
                        final TextView totDistView= findViewById(R.id.totalDistanceP);

                        int userIdIndex = 0;
                        int userNameIndex = 1;
                        int weightIndex = 3;
                        int heightFtIndex = 4;
                        int heightInIndex = 5;
                        int pointsIndex = 6;
                        int totCalIndex = 7;
                        int totDistIndex = 8;
                        int nameIndex = 9;

                        SpannableStringBuilder userNamestr = new SpannableStringBuilder("Username: "+ result[userNameIndex].substring(1,result[userNameIndex].length()-1));
                        userNamestr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder userIDstr = new SpannableStringBuilder("User ID: "+ result[userIdIndex].substring(2));
                        userIDstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder weightstr = new SpannableStringBuilder("Weight: " + result[weightIndex]);
                        weightstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder heightstr = new SpannableStringBuilder("Height: " + result[heightFtIndex]+"ft "+ result[heightInIndex]+"in");
                        heightstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder pointsstr = new SpannableStringBuilder("Points: " + String.valueOf(pointsIndex));
                        pointsstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder totCalstr = new SpannableStringBuilder("Total Calories: " + String.valueOf(totCalIndex));
                        totCalstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder totDiststr = new SpannableStringBuilder("Total Distance: " + String.valueOf(totDistIndex));
                        totDiststr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        nameView.setText(result[nameIndex].substring(1,result[nameIndex].length()-1));
                        usernameView.setText(userNamestr);
                        userIDView.setText(userIDstr);
                        weightView.setText(weightstr);
                        heightView.setText(heightstr);
                        pointsView.setText(pointsstr);
                        totCalView.setText(totCalstr);
                        totDistView.setText(totDiststr);

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
