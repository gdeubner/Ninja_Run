package com.ninjadroid.app.activities.menuFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.ninjadroid.app.utils.CustomAdapter;
import com.ninjadroid.app.utils.URLBuilder;

import java.util.ArrayList;

public class FollowersFragment extends Fragment {
    private static final String USERID = "key";

    // TODO: Rename and change types of parameters
    private String mUserID;

    public FollowersFragment() {
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
    public static FollowersFragment newInstance(String userID) {
        FollowersFragment fragment = new FollowersFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers, container, false);
        // Inflate the layout for this fragment
        queryInfo(getContext(), mUserID,view);
        return view;
    }

    private void queryInfo(Context context, String userID,View view) {
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
        String message = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("Get Request Response", response);
                        String[] result = response.split(",");

                        int userIDint = Integer.valueOf(userID);
                        ArrayList<String> username = populateusernameList(result);
                        ArrayList<String> userid = populateuseridList(result);

                        final RecyclerView recyclerView = getView().findViewById(R.id.Followers);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(new CustomAdapter(userIDint, context,username));
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


    public ArrayList<String> populateusernameList(String[] str) {
        int count = 0;
        ArrayList<String> data = new ArrayList<>();
        for (String s : str) {
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
    public ArrayList<String> populateuseridList(String[] str) {
        int count = 0;
        ArrayList<String> data = new ArrayList<>();
        for (String s : str) {
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
