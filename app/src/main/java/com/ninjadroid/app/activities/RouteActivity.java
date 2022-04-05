package com.ninjadroid.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ninjadroid.app.R;
import com.ninjadroid.app.databinding.ActivityRouteBinding;
import com.ninjadroid.app.utils.RouteCallback;
import com.ninjadroid.app.utils.containers.RouteContainer;
import com.ninjadroid.app.webServices.GetRoute;

public class RouteActivity extends AppCompatActivity {

    ActivityRouteBinding binding;
    private static final String ROUTE_ID_KEY = "routeID";
    private static int routeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        binding = ActivityRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        routeID = Integer.parseInt(getIntent().getStringExtra(ROUTE_ID_KEY));
        GetRoute.getRoute(this, routeID, new RouteCallback() {
            @Override
            public void onSuccess(RouteContainer route) {
                binding.tvCreator.setText(route.getUsername());
                binding.tvLocation.setText(route.getTown());
                binding.tvDistance.setText(getString(R.string.distance_string, route.getDistance()));
                //binding.tvElevation.setText(route.getElevation);
                //binding.tvDateCreated.setText(route.getDate());
            }
        });

        binding.btnStartRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RouteActivity.this, MainActivity.class);
                intent.putExtra(ROUTE_ID_KEY, routeID);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(R.id.map, intent);
                finish();
            }
        });

    }

}