package com.ninjadroid.app.activities.menuFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.ninjadroid.app.utils.FollowerAdapter;
import com.ninjadroid.app.utils.URLBuilder;

import java.util.ArrayList;

public class FollowingFragment extends Fragment {
    private static final String USERID = "key";

    // TODO: Rename and change types of parameters
    private String mUserID;
    private String username = "";

    public FollowingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userID Parameter 1.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingFragment newInstance(String userID) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString(USERID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserID = getArguments().getString(USERID);
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.following_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        // Inflate the layout for this fragment
        final Button followButton = view.findViewById(R.id.followButton);
        final EditText followUsername = view.findViewById(R.id.followUsername);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = followUsername.getText().toString();
                getUsername(getContext(), mUserID, username, view);
            }
        });
        showFollowingList(getContext(), mUserID, view);

        return view;
    }

    private void showFollowingList(Context context, String userID, View view) {
        // Instantiate the RequestQueue.

        RecyclerView recyclerView = view.findViewById(R.id.followingList);
        Log.i("AMYYY", "18"); //replace with userID
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.showFollowingList())
                .appendQueryParameter("user_id", mUserID); //replace with userID

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
                        ArrayList<String> username = new ArrayList<String>();
                        ArrayList<String> userid = new ArrayList<String>();
                        int userIDint = Integer.valueOf(userID);
                        if(response.length()  > 2) {
                            username = populateList(result);
                            userid = populateuseridList(result);
                        }
                        final RecyclerView recyclerView = getView().findViewById(R.id.followingList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(new FollowerAdapter(userIDint, context, username, userid, "Following")); //change later from 17 to uid
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

    public ArrayList<String> populateList(String[] str) {
        ArrayList<String> data = new ArrayList<>();
        int count = 0;
        for (String s : str) {
            String temp = "";
            if(count==0){
                temp = temp + s.substring(21,s.length()-1);
                data.add(temp);
            }else if(count%2==0){
                temp = temp + s.substring(20,s.length()-1);
                data.add(temp);
            }
            count++;
        }
        return data;
    };
    public ArrayList<String> populateuseridList(String[] str) {
        int count = 0;
        ArrayList<String> data = new ArrayList<>();
        for (String s : str) {
            if(count%2 == 1) {
                String temp;
                if(s.contains("]")){
                    temp = s.substring(12,s.length()-2);
                }
                else {
                    temp = s.substring( 12,s.length() - 1);
                }
                Log.i("temp is", temp);
                data.add(temp);
            }
            count++;
        }
        return data;
    }
    private void addFollow(Context context, String userID, String myUsername, String toFollowUsername) {
        // Instantiate the RequestQueue.

        Log.i("AMYYY", "progressss");
        Log.i("username", myUsername);//replace with userID
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.addFollow())
                .appendQueryParameter("user_id", userID)
                .appendQueryParameter("username", myUsername)
                .appendQueryParameter("follow_username", toFollowUsername);

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
                        if((response.substring(1,3)).equals("No")){
                            Toast.makeText(context, "Entered Username Not Found!",
                                    Toast.LENGTH_SHORT).show();
                        }else if((response.substring(1,5)).equals("fail")) {
                            Toast.makeText(context, "You are already following them!",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Successfully Followed!",
                                    Toast.LENGTH_SHORT).show();
                            showFollowingList(getContext(), mUserID, getView());
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
    private void getUsername(Context context, String userID, String toFollowusername, View view) {
        // Instantiate the RequestQueue.

        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getUserProfilePath())
                .appendQueryParameter("user_id", userID);

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
                        String myUsername = result[1].substring(12, result[1].length()-1);

                        addFollow(context, userID, myUsername, toFollowusername);
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
        queue.add(stringRequest);
    }

}