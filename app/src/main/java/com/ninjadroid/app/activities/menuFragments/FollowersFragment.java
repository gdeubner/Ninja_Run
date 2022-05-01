package com.ninjadroid.app.activities.menuFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.ninjadroid.app.utils.adapters.FollowerAdapter;
import com.ninjadroid.app.utils.URLBuilder;

import java.util.ArrayList;
import java.util.Collections;

public class FollowersFragment extends Fragment {
    private static final String USERID = "key";

    // TODO: Rename and change types of parameters
    private String mUserID;
    private TextView noFollowers;

    public FollowersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userID
     * @return A new instance of fragment ProfileFragment.
     */
    public static FollowersFragment newInstance(String userID) {
        FollowersFragment fragment = new FollowersFragment();
        Bundle args = new Bundle();
        args.putString(USERID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * sets the global mUserID variable and sets the actionBar title
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserID = getArguments().getString(USERID);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.follower_title);
    }

    /**
     * inflates the fragment_followers xml and launches getFollowers()
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the view created when inflating the fragment_followers xml
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers, container, false);
        noFollowers = view.findViewById(R.id.tv_noFollowers);
        noFollowers.setVisibility(View.INVISIBLE);
        // Inflate the layout for this fragment
        getFollowers(getContext(), mUserID,view);
        return view;
    }

    /**
     * gets the followers of the current user from the server
     * @param context
     * @param userID
     * @param view the view containing the RecyclerView which needs to be bound
     */
    private void getFollowers(Context context, String userID,View view) {
        // Instantiate the RequestQueue.

        RecyclerView recyclerView = view.findViewById(R.id.Followers);
        Log.i("user_id", userID); //replace with userID
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getFollowerList())
                .appendQueryParameter( "user_id", userID); //replace with userID

        String myUrl = builder.build().toString();
        Log.i("Query", myUrl);


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("Get Request Response", response);
                        String[] result = response.split(",");
                        ArrayList<String> username = new ArrayList<String>();
                        Collections.reverse(username);
                        ArrayList<String> userid = new ArrayList<String>();
                        Collections.reverse(userid);
                        int userIDint = Integer.valueOf(userID);
                        if(response.length()  > 2) {
                            username = populateusernameList(result);
                            userid = populateuseridList(result);
                            final RecyclerView recyclerView = getView().findViewById(R.id.Followers);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            recyclerView.setAdapter(new FollowerAdapter(userIDint, context,username, userid, "Follower"));
                            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

                        } else {
                            noFollowers.setVisibility(View.VISIBLE);
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

    /**
     * converts an json Strings into String arraylist, extracting the usernames of followers
     * @param strArr
     * @return an Arraylist of usernames
     */
    public ArrayList<String> populateusernameList(String[] strArr) {
        int count = 0;
        ArrayList<String> data = new ArrayList<>();
        for (String s : strArr) {
            if(count%2 == 0) {
                String temp;
                if(count  == 0){
                    temp = s.substring(14, s.length()-1);
                }
                else {
                    temp = s.substring(13, s.length() - 1);
                }
                data.add(temp);
            }
            count++;
        }
        return data;
    }

    /**
     *creates an ArrayList of userIDs
     * @param strArr
     * @return returns an ArrayList of userIDs
     */
    public ArrayList<String> populateuseridList(String[] strArr) {
        int count = 0;
        ArrayList<String> data = new ArrayList<>();
        for (String s : strArr) {
            if(count%2 == 1) {
                String temp;
                if(s.contains("]")){
                    temp = s.substring(10,s.length()-2);
                }
                else {
                    temp = s.substring( 10,s.length() - 1);
                }
                Log.i("temp is", temp);
                data.add(temp);
            }
            count++;
        }
        return data;
    }
}