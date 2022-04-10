package com.ninjadroid.app.activities.menuFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("HIII", "make it?");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        //setInfo(getContext(), mUserID, view);

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

        name = profileC.getName();

        final TextView nameView = view.findViewById(R.id.username);
        final TextView usernameView = view.findViewById(R.id.usernameP);
        final TextView userIDView = view.findViewById(R.id.UserIDP);
        final TextView weightView= view.findViewById(R.id.weightP);
        final TextView heightView= view.findViewById(R.id.heightP);
        final TextView pointsView= view.findViewById(R.id.pointsP);
        final TextView totCalView= view.findViewById(R.id.totalCaloriesP);
        final TextView totDistView= view.findViewById(R.id.totalDistanceP);

        SpannableStringBuilder userNamestr = new SpannableStringBuilder("Username: "+ usernameP);
        userNamestr.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder userIDstr = new SpannableStringBuilder("User ID: "+ userId);
        userIDstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder weightstr = new SpannableStringBuilder("Weight: " + weight);
        weightstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder heightstr = new SpannableStringBuilder("Height: " + heightFt+"ft "+ heightIn+"in");
        heightstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder pointsstr = new SpannableStringBuilder("Points: " + String.valueOf(points));
        pointsstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder totCalstr = new SpannableStringBuilder("Total Calories: " + String.valueOf(calories));
        totCalstr.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder totDiststr = new SpannableStringBuilder("Total Distance: " + String.valueOf(distance));
        totDiststr.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        nameView.setText(name);
        usernameView.setText(userNamestr);
        userIDView.setText(userIDstr);
        weightView.setText(weightstr);
        heightView.setText(heightstr);
        pointsView.setText(pointsstr);
        totCalView.setText(totCalstr);
        totDistView.setText(totDiststr);
        return view;
    }
}