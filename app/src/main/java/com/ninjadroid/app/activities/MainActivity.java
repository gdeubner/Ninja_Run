package com.ninjadroid.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.ninjadroid.app.activities.menuFragments.FollowersFragment;
import com.ninjadroid.app.activities.menuFragments.HistoryFragment;
import com.ninjadroid.app.activities.menuFragments.MapFragment;
import com.ninjadroid.app.activities.menuFragments.MyRouteFragment;
import com.ninjadroid.app.activities.menuFragments.ProfileFragment;
import com.ninjadroid.app.R;
import com.ninjadroid.app.activities.menuFragments.SearchFragment;
import com.ninjadroid.app.activities.menuFragments.SharedFragment;
import com.ninjadroid.app.activities.menuFragments.FollowingFragment;
import com.ninjadroid.app.webServices.callbacks.VolleyProfileCallback;
import com.ninjadroid.app.utils.containers.ProfileContainer;
import com.ninjadroid.app.webServices.GetProfile;

//this is the activity which most of the fragment pages are created in. It also is responsible for
//the sidebar menu and corresponding page navigation
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    //private ActivityMainBinding binding;
    private int currentNavItemId;

    private String userID;
    private int routeId;
    private ProfileContainer userProfile;
    public static int userIDGlobalScope;

    /**
     * assigns the relevant views, sets up the drawer layout, and sets the current fragment to the
     * map fragment (treated as home base for the app)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(findViewById(R.id.drawer_layout).getWindowToken(), 0);
            }


        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        userID = getIntent().getStringExtra("userID");
        userIDGlobalScope = Integer.parseInt(userID);
        routeId = -1;
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
        getSupportActionBar().setTitle(R.string.map_title);

    }

    /**
     * handles the back button functionality. When pressed while the map's page is loaded, the app
     * will sign the current user out and go to the login page. Otherwise, the app navigates from any
     * page in the side_nav to map page.
     */
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

    /**
     * sets up the nav bar functionality
     * @param item the nav bar item that was selected
     * @returnreturns true if there was a page change, false otherwise
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        currentNavItemId = item.getItemId();
        if(!navigationView.getMenu().findItem(item.getItemId()).isChecked()){
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    Log.i("MainActivity", "Loaded profile page by navbar");
                    ProfileFragment profileFragment = ProfileFragment.newInstance(userProfile);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            profileFragment, "PROFILE_FRAGMENT").commit();
                    break;
                case R.id.nav_map:
                    MapFragment fmapFragment = MapFragment.newInstance(userID, routeId, userProfile);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            fmapFragment, "MAP_FRAGMENT").commit();
                    break;

                case R.id.nav_search:
                    SearchFragment searchFragment = SearchFragment.newInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            searchFragment,"SEARCH_FRAGMENT").commit();
                    break;

                case R.id.nav_history:
                    HistoryFragment histFrag = HistoryFragment.newInstance(userID);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            histFrag,"HIST_FRAGMENT").commit();
                    break;

                case R.id.nav_myroutes:
                    MyRouteFragment routeFrag = MyRouteFragment.newInstance(userID);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            routeFrag,"ROUTE_FRAGMENT").commit();
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

    /**
     *if any data was passed back to the main activity, it will be a route id, so that is stored and
     * used if the user switches to follow-route mode
     * @param requestCode not used but required
     * @param resultCode not used but required
     * @param data the routeID passed back to the main activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("MainActivity", "Checking Activity Result");
        if(data != null && data.getExtras() != null){
            routeId = data.getExtras().getInt("routeID");
            goToMapFragment(routeId);
        } else {
            routeId = -1;
        }
    }

    /**
     * obtains the user profile data when a different activity ends and the app navigates back to the
     * main activity (in case there has been a change to it)
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "resuming");
        GetProfile.getProfile(this, userID,new VolleyProfileCallback() {
            @Override
            public void onSuccess(ProfileContainer profile) {
                userProfile = profile;
                TextView tv_profile = navigationView.findViewById(R.id.tv_profileNameNav);
                tv_profile.setText(userProfile.getName());
            }
        });
    }

    /**
     * a helper method designed to navigate to the map page
     * @param routeID the id of the route to load if the user selects follow-route mode
     */
    private void goToMapFragment(int routeID){
        MapFragment fmapFragment = MapFragment.newInstance(userID, routeID, userProfile);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fmapFragment, "MAP_FRAGMENT").commit();
        navigationView.getMenu().getItem(1).setChecked(true);
        currentNavItemId = R.id.nav_map;
    }
}