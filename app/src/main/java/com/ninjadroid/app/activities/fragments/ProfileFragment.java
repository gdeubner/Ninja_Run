package com.ninjadroid.app.activities.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.URLBuilder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USERID = "key";

    // TODO: Rename and change types of parameters
    private String mUserID;

    public ProfileFragment() {
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
    public static ProfileFragment newInstance(String userID) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        queryInfo(getContext(), mUserID);

        return view;
    }

    private void queryInfo(Context context, String userID) {
        // Instantiate the RequestQueue.
        Log.i("Amy", userID);
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

                        final TextView nameView = getView().findViewById(R.id.nameP);
                        final TextView usernameView = getView().findViewById(R.id.usernameP);
                        final TextView userIDView = getView().findViewById(R.id.UserIDP);
                        final TextView weightView= getView().findViewById(R.id.weightP);
                        final TextView heightView= getView().findViewById(R.id.heightP);
                        final TextView pointsView= getView().findViewById(R.id.pointsP);
                        final TextView totCalView= getView().findViewById(R.id.totalCaloriesP);
                        final TextView totDistView= getView().findViewById(R.id.totalDistanceP);

                        int userIdIndex = 0;
                        int userNameIndex = 1;
                        int weightIndex = 3;
                        int heightFtIndex = 4;
                        int heightInIndex = 5;
                        int pointsIndex = 6;
                        int totCalIndex = 7;
                        int totDistIndex = 8;
                        int nameIndex = 9;

                        SpannableStringBuilder userNamestr = new SpannableStringBuilder("Username: "+ result[userNameIndex].substring(1,result[userNameIndex].length()-1));
                        userNamestr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder userIDstr = new SpannableStringBuilder("User ID: "+ result[userIdIndex].substring(2));
                        userIDstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder weightstr = new SpannableStringBuilder("Weight: " + result[weightIndex]);
                        weightstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder heightstr = new SpannableStringBuilder("Height: " + result[heightFtIndex]+"ft "+ result[heightInIndex]+"in");
                        heightstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder pointsstr = new SpannableStringBuilder("Points: " + String.valueOf(pointsIndex));
                        pointsstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder totCalstr = new SpannableStringBuilder("Total Calories: " + String.valueOf(totCalIndex));
                        totCalstr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder totDiststr = new SpannableStringBuilder("Total Distance: " + String.valueOf(totDistIndex));
                        totDiststr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        nameView.setText(result[nameIndex].substring(1,result[nameIndex].length()-1));
                        usernameView.setText(userNamestr);
                        userIDView.setText(userIDstr);
                        weightView.setText(weightstr);
                        heightView.setText(heightstr);
                        pointsView.setText(pointsstr);
                        totCalView.setText(totCalstr);
                        totDistView.setText(totDiststr);

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
}