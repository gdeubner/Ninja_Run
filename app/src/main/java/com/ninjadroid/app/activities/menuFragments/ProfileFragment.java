package com.ninjadroid.app.activities.menuFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.ninjadroid.app.R;

import com.ninjadroid.app.activities.EditProfileActivity;
import com.ninjadroid.app.utils.containers.ProfileContainer;
import com.ninjadroid.app.webServices.GetProfile;
import com.ninjadroid.app.webServices.callbacks.VolleyProfileCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // private static final String USERID = "key";

    // TODO: Rename and change types of parameters
    private int userId;
    private String usernameP;
    private double weight;
    private int heightFt;
    private double heightIn;
    private int points;
    private int calories;
    private double distance;
    private String name;

    private ProfileContainer profileC;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param profile Parameter 1.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(ProfileContainer profile) {
        Log.i("PROFILE PAGE", "MADE IT");

        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt("userID", profile.getUserId());
        args.putString("username", profile.getUsername());
        args.putDouble("weight", profile.getWeight());
        args.putInt("heightFt", profile.getHeight_ft());
        args.putDouble("heightIn", profile.getHeight_in());
        args.putInt("points", profile.getPoints());
        args.putInt("calories", profile.getCalories());
        args.putDouble("distance", profile.getDistance());
        args.putString("name", profile.getName());
        args.putSerializable("container", profile);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("PROFILE PAGE", "on create");
        if (getArguments() != null) {
            userId = getArguments().getInt("userID");
            usernameP = getArguments().getString("username");
            weight = getArguments().getDouble("weight");
            heightFt = getArguments().getInt("heightFt");
            heightIn = getArguments().getDouble("heightIn");
            points = getArguments().getInt("points",0);
            calories = getArguments().getInt("calories", 0);
            distance = getArguments().getDouble("distance", 0);
            name = getArguments().getString("name");
            profileC = (ProfileContainer) getArguments().getSerializable("container");
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.profile_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("HIII", "make it?");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        //setInfo(getContext(), mUserID, view);
        Log.i("PROFILE PAGE", "on create view");

        Button editPButton = view.findViewById(R.id.editPButton);
        editPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("profileObject", profileC);
                //startActivityForResult(intent, R.id.nav_profile);
                getActivity().startActivityForResult(intent, 72);

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("PROFILE PAGE", "on resume");
        Log.i("PROFILE PAGE", "requery on resume");
        GetProfile.getProfile(getContext(), String.valueOf(userId),new VolleyProfileCallback() {
            @Override
            public void onSuccess(ProfileContainer profile) {
                profileC = profile;
                usernameP= profileC.getUsername();
                weight=profile.getWeight();
                heightFt=profileC.getHeight_ft();
                heightIn= profileC.getHeight_in();
                points = profileC.getPoints();
                calories = profileC.getCalories();
                distance = profileC.getDistance();
                name = profileC.getName();

                Log.i("PROFILE PAGE new weight", String.valueOf(weight));

                final TextView nameView = getView().findViewById(R.id.username);
                final TextView usernameView = getView().findViewById(R.id.usernameP);
                final TextView userIDView = getView().findViewById(R.id.UserIDP);
                final TextView weightView= getView().findViewById(R.id.weightP);
                final TextView heightView= getView().findViewById(R.id.heightP);
                final TextView pointsView= getView().findViewById(R.id.pointsP);
                final TextView totCalView= getView().findViewById(R.id.totalCaloriesP);
                final TextView totDistView= getView().findViewById(R.id.totalDistanceP);

                Log.i("PROFILE PAGE", "on resume reformatting text");
                SpannableStringBuilder userNamestr = new SpannableStringBuilder("Username: "+ usernameP);
                userNamestr.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableStringBuilder userIDstr = new SpannableStringBuilder("User ID: "+ userId);
                userIDstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableStringBuilder weightstr = new SpannableStringBuilder("Weight: " + Math.round(weight*100)/100.0);
                weightstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableStringBuilder heightstr = new SpannableStringBuilder("Height: " + heightFt+"ft "+ Math.round(heightIn*100)/100.0+"in");
                heightstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableStringBuilder pointsstr = new SpannableStringBuilder("Points: " + String.valueOf(points));
                pointsstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableStringBuilder totCalstr = new SpannableStringBuilder("Total Calories: " + String.valueOf(calories));
                totCalstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableStringBuilder totDiststr = new SpannableStringBuilder("Total Distance: " + String.valueOf(Math.round(distance*100)/100.0));
                totDiststr.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                Log.i("PROFILE PAGE", "on resume setting text");
                nameView.setText(name);
                usernameView.setText(userNamestr);
                userIDView.setText(userIDstr);
                weightView.setText(weightstr);
                heightView.setText(heightstr);
                pointsView.setText(pointsstr);
                totCalView.setText(totCalstr);
                totDistView.setText(totDiststr);
            }
        });
    }
}