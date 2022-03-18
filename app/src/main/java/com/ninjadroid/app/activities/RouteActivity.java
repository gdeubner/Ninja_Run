package com.ninjadroid.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ninjadroid.app.R;
import com.ninjadroid.app.databinding.ActivityMainBinding;
import com.ninjadroid.app.databinding.ActivityRouteBinding;
import com.ninjadroid.app.webServices.GetRoute;

public class RouteActivity extends AppCompatActivity {

    ActivityRouteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        binding = ActivityRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GetRoute.getRoute(this, 17, binding);

    }

}