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
import com.ninjadroid.app.activities.fragments.HistoryFragment;
import com.ninjadroid.app.activities.fragments.MapFragment;
import com.ninjadroid.app.activities.fragments.ProfileFragment;
import com.ninjadroid.app.R;
import com.ninjadroid.app.activities.fragments.SharedFragment;
import com.ninjadroid.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActivityMainBinding binding;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();
        userID = intent.getStringExtra((LoginPage.KEY));

        if(savedInstanceState == null) {
            MapFragment fmapFragment = MapFragment.newInstance(userID);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fmapFragment, "MAP_FRAGMENT").commit();
        }
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
        if(!navigationView.getMenu().findItem(item.getItemId()).isChecked()){
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
                case R.id.nav_history:
                    //todo: create and replace the history fragment here Justin!
                    HistoryFragment histFrag = HistoryFragment.newInstance(userID);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            histFrag,"HIST_FRAGMENT").commit();
                    break;

                case R.id.nav_shared:
                    SharedFragment sharedFrag = SharedFragment.newInstance(userID);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            sharedFrag,"SHARED_FRAGMENT").commit();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }

    }
}