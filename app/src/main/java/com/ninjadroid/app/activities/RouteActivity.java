package com.ninjadroid.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ninjadroid.app.R;
import com.ninjadroid.app.databinding.ActivityRouteBinding;
import com.ninjadroid.app.webServices.ShareRoute;
import com.ninjadroid.app.webServices.callbacks.RouteCallback;
import com.ninjadroid.app.utils.containers.UserRouteContainer;
import com.ninjadroid.app.webServices.GetRoute;

//This page displays all the relevant information about a given route
public class RouteActivity extends AppCompatActivity {

    ActivityRouteBinding binding;
    private static final String ROUTE_ID_KEY = "routeID";
    private static int routeID;

    /**
     * inflates the layout, gets the route information, and assigns the onClickListener functionality
     * for the view's buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        binding = ActivityRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        routeID = Integer.parseInt(getIntent().getStringExtra(ROUTE_ID_KEY));
        GetRoute.getRoute(this, routeID, new RouteCallback() {
            //when the get request returns, assign the route data to the page's text views
            @Override
            public void onSuccess(UserRouteContainer route) {
                binding.tvCreator.setText(route.getUsername());
                binding.tvLocation.setText(route.getTown());
                Log.i("RouteActivity", "" + route.getDistance());
                binding.tvDistance.setText(getString(R.string.distance_string, route.getDistance()));
                String title = route.getRoute().getTitle();
                if(title != null && title.length() > 0){
                    binding.tvRouteTitle.setText(title);
                }
                //binding.tvElevation.setText(route.getElevation);
                String date = route.getRoute().getDate();
                if(date != null && date.length() > 0){
                    binding.tvDateCreated.setText(date.split("T")[0]);
                }
                double elevation = route.getRoute().getElevation();
                if(elevation!= -1){
                    binding.tvElevation.setText(Double.toString(elevation) + " feet");
                }
            }
        });

        binding.btnStartRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RouteActivity.this, MainActivity.class);
                intent.putExtra(ROUTE_ID_KEY, routeID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareRoute.share(v.getContext(),routeID, MainActivity.userIDGlobalScope);
            }
        });
    }

}