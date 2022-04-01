package com.ninjadroid.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.ninjadroid.app.activities.menuFragments.FollowersFragment;
import com.ninjadroid.app.activities.menuFragments.HistoryFragment;
import com.ninjadroid.app.activities.menuFragments.MapFragment;
import com.ninjadroid.app.activities.menuFragments.ProfileFragment;
import com.ninjadroid.app.R;
import com.ninjadroid.app.activities.menuFragments.SharedFragment;
import com.ninjadroid.app.activities.menuFragments.FollowingFragment;
import com.ninjadroid.app.databinding.ActivityMainBinding;
import com.ninjadroid.app.utils.VolleyProfileCallback;
import com.ninjadroid.app.utils.containers.ProfileContainer;
import com.ninjadroid.app.webServices.GetProfile;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActivityMainBinding binding;
    private int currentNavItemId;

    private String userID;
    private ProfileContainer userProfile;

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

        userID = getIntent().getStringExtra("userID");
        Log.i("MainActivity", userID);

        GetProfile.getProfile(this, userID,new VolleyProfileCallback() {
            @Override
            public void onSuccess(ProfileContainer profile) {
                userProfile = profile;
                TextView tv_profile = navigationView.findViewById(R.id.tv_profileNameNav);
                tv_profile.setText(userProfile.getName());

                //start map fragment
                if(savedInstanceState == null) {
                    goToMapFragment(-1);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);  //closes drawer on left side of screen
        } else {

            if(currentNavItemId == R.id.nav_map) {
                Log.i("Main", "backPressed");
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
                goToMapFragment(-1);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        currentNavItemId = item.getItemId();
        if(!navigationView.getMenu().findItem(item.getItemId()).isChecked()){
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    ProfileFragment profileFragment = ProfileFragment.newInstance(userProfile);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            profileFragment, "PROFILE_FRAGMENT").commit();
                    break;
                case R.id.nav_map:
                    MapFragment fmapFragment = MapFragment.newInstance(userID, -1, userProfile);
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

                case R.id.nav_following:
                    FollowingFragment followingFrag = FollowingFragment.newInstance(userID);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            followingFrag,"FOLLOWING_FRAGMENT").commit();
                    break;

                case R.id.nav_followers:
                    FollowersFragment follFrag = FollowersFragment.newInstance(userID);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            follFrag,"FOLL_FRAGMENT").commit();
                    break;
                case R.id.nav_logOut:
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else {
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            int routeID = data.getExtras().getInt("routeID");
            goToMapFragment(routeID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        goToMapFragment(-1);
        if(getIntent().getIntExtra("ProfileFragment",0)==1){
            Log.i("Profile page first", "came directly");
            userID = getIntent().getStringExtra("userID");
            GetProfile.getProfile(this, userID,new VolleyProfileCallback() {
                @Override
                public void onSuccess(ProfileContainer profile) {
                    userProfile = profile;
                    ProfileFragment profileFragment = ProfileFragment.newInstance(profile);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            profileFragment, "PROFILE_FRAGMENT").commit();
                }
            });
       }else{
            Log.i("itttt", "didnt workkkkk");
        }
    }

    @Override
    public void onNewIntent (Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void goToMapFragment(int routeID){
        MapFragment fmapFragment = MapFragment.newInstance(userID, routeID, userProfile);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fmapFragment, "MAP_FRAGMENT").commit();
        navigationView.getMenu().getItem(1).setChecked(true);
        currentNavItemId = R.id.nav_map;
    }
}