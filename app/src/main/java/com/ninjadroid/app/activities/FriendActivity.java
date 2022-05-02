package com.ninjadroid.app.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.adapters.FRouteAdapter;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.adapters.MyRoutesAdapter;
import com.ninjadroid.app.utils.containers.RouteContainer;
import com.ninjadroid.app.webServices.callbacks.VolleyProfileCallback;
import com.ninjadroid.app.utils.containers.ProfileContainer;
import com.ninjadroid.app.webServices.GetProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FriendActivity extends AppCompatActivity {
    private String userID;
    private String name;
    private int points;
    private int calories;
    private double distance;

    /**
     * assigns text views their values and queries the database for the friend's info
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        TextView fUsername = findViewById(R.id.followusername);
        TextView fCalories = findViewById(R.id.totalCaloriesF);
        TextView fDistance = findViewById(R.id.totalDistanceF);
        TextView fPoints = findViewById(R.id.PointsF);


        Intent intent = getIntent();
        userID = intent.getStringExtra("Friend");

        GetProfile.getProfile(this, userID, new VolleyProfileCallback() {
            @Override
            public void onSuccess(ProfileContainer profile) {
                name = profile.getUsername();
                calories = profile.getCalories();
                distance = profile.getDistance();
                points = profile.getPoints();

                String disttemp = String.valueOf(Math.round(distance));

                fUsername.setText(name);
                fCalories.setText("Calories: "+ Integer.toString(calories));
                fDistance.setText("Dist: "+ disttemp);
                fPoints.setText("Points: " + Integer.toString(points));
            }

        });

        queryInfo(getBaseContext(),userID);

    }

    /**
     * requests the friend's info from the server
     * @param context
     * @param userID the userID of the friend
     */
    private void queryInfo(Context context, String userID) {
        // Instantiate the RequestQueue.

        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getUserRoutes())
                .appendQueryParameter("user_id", userID); //replace with userID

        String myUrl = builder.build().toString();
        Log.i("Query", myUrl);
        String message = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Gson gson = new Gson();
                        RouteContainer[] routeArr = gson.fromJson(response, RouteContainer[].class);
                        ArrayList<RouteContainer> routeList = new ArrayList<>(Arrays.asList(routeArr));
                        Collections.reverse(routeList);
                        if(routeList.size() > 0){
                            final RecyclerView recyclerView = findViewById(R.id.rv_friendRouteList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            recyclerView.setAdapter(new MyRoutesAdapter(Integer.parseInt(userID), context, routeList));
                            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                        }else{
                            //no routes.....
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

    public void onBackPressed() {

        Intent intent = new Intent(FriendActivity.this, MainActivity.class);
        //intent.putExtra("routeID", routeID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setResult(RouteActivity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            int routeID = data.getExtras().getInt("routeID");

            Intent intent = new Intent(FriendActivity.this, MainActivity.class);
            intent.putExtra("routeID", routeID);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            setResult(RouteActivity.RESULT_OK, intent);
            finish();
        }
    }
}
