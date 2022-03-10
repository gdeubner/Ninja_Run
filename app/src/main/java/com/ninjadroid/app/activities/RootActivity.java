package com.ninjadroid.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.ninjadroid.app.activities.fragments.MapFragment;
import com.ninjadroid.app.activities.fragments.ProfileFragment;
import com.ninjadroid.app.R;
import com.ninjadroid.app.databinding.ActivityRootBinding;

public class RootActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ActivityRootBinding binding;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRootBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();
        userID = intent.getStringExtra((LoginPage.KEY));

    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);  //closes drawer on left side of screen
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                ProfileFragment profileFragment = ProfileFragment.newInstance(userID);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        profileFragment, "PROFILE_FRAGMENT").commit();
                break;
            case R.id.nav_map:
                MapFragment fmapFragment = MapFragment.newInstance(userID);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fmapFragment, "MAP_FRAGMENT").commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}