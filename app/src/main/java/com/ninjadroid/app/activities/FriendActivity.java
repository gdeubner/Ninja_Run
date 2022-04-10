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
import com.ninjadroid.app.R;
import com.ninjadroid.app.databinding.ActivityRouteBinding;
import com.ninjadroid.app.utils.CustomAdapter;
import com.ninjadroid.app.utils.FRouteAdapter;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.VolleyProfileCallback;
import com.ninjadroid.app.utils.containers.ProfileContainer;
import com.ninjadroid.app.webServices.GetProfile;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FriendActivity extends AppCompatActivity {
    private String userID;
    private String fType;
    private String name;
    private int points;
    private int calories;
    private double distance;
    private ProfileContainer fProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Log.i("VEE", "yoooooooooo");

        TextView fUsername = findViewById(R.id.followusername);
        TextView fCalories = findViewById(R.id.totalCaloriesF);
        TextView fDistance = findViewById(R.id.totalDistanceF);
        TextView fPoints = findViewById(R.id.PointsF);

        Log.i("ZZZZZZEE", "yoooooooooo");

        Intent intent = getIntent();
        userID = intent.getStringExtra("Friend");
        fType = intent.getStringExtra("Type");

        GetProfile.getProfile(this, userID, new VolleyProfileCallback() {
            @Override
            public void onSuccess(ProfileContainer profile) {
                Log.i("NAMEEEEEE", "yoooooooooo");
                fProfile = profile;
                name = profile.getUsername();
                calories = profile.getCalories();
                distance = profile.getDistance();
                points = profile.getPoints();

                Log.i("NAMEEEEEE", name);
                String disttemp = String.valueOf(Math.round(distance));
                Log.i("DISTTT", disttemp);

                fUsername.setText(name);
                fCalories.setText("Calories: "+ Integer.toString(calories));
                fDistance.setText("Dist: "+ disttemp);
                fPoints.setText("Points: " + Integer.toString(points));
            }

        });

        queryInfo(getBaseContext(),userID);

    }

    private void queryInfo(Context context, String userID) {
        // Instantiate the RequestQueue.

        //RecyclerView recyclerView = findViewById(R.id.listoffriendroute);
        Log.i("Justin", userID); //replace with userID
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
                        // Display the first 500 characters of the response string.
                        Log.i("Get Request Response", response);
                        String[] result = response.split(",");

                        final RecyclerView recyclerView = findViewById(R.id.listoffriendroute);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                        if(result.length > 1) {
                            ArrayList<ArrayList<String>> data = populate(result);
                            recyclerView.setAdapter(new FRouteAdapter(Integer.parseInt(userID), context, data.get(0), data.get(1), data.get(2)));
                        }else{
                            ArrayList<String> routeid = new ArrayList<>();
                            ArrayList<String> town = new ArrayList<>();
                            ArrayList<String> dist = new ArrayList<>();
                            recyclerView.setAdapter(new FRouteAdapter(Integer.parseInt(userID), context, routeid, town, dist));
                        }
                        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));


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

    public ArrayList<ArrayList<String>> populate(String[] str) {
        ArrayList<String> route = new ArrayList<>();
        ArrayList<String> town = new ArrayList<>();
        ArrayList<String> dist = new ArrayList<>();
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

        int count = 0;
        for (String s : str) {
                String temp = "";
                Log.i("RouteIDD", s);
                if (count == 0) {
                    temp = temp + s.substring(13);
                    Log.i("RouteID", temp);
                    route.add(temp);
                } else if (s.contains("]")) {
                    temp = temp + s.substring(11, s.length() - 2);
                    Log.i("Distance", temp);
                    dist.add(temp);
                } else if (count % 3 == 0) {
                    temp = temp + s.substring(12);
                    Log.i("RouteID", temp);
                    route.add(temp);
                } else if (count % 3 == 1) {
                    temp = temp + s.substring(8, s.length()-1);
                    Log.i("Town", temp);
                    town.add(temp);
                } else if (count % 3 == 2) {
                    temp = temp + s.substring(11, s.length()-1);
                    Log.i("Distance", temp);
                    dist.add(temp);
                }
                Log.i("infooo", temp);
                count++;
            }
        data.add(route);
        data.add(town);
        data.add(dist);
        return data;
    };


    public void onBackPressed() {
//        Intent intent = new Intent(getBaseContext(),MainActivity.class);
//        if(fType.equals("Follower")){
//            intent.putExtra("FollowersFragment",1);
//        }else{
//            intent.putExtra("FollowingFragment",1);
//        }
//        startActivity(intent);

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
