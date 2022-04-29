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
import com.google.gson.Gson;
import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.adapters.MyRoutesAdapter;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.containers.RouteContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MyRouteFragment extends Fragment {
    private String mUserID;
    private static final String USERID = "key";
    private TextView noRoutes;


    public MyRouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userID Parameter 1.
     * @return A new instance of fragment MyRouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyRouteFragment newInstance(String userID) {
        MyRouteFragment fragment = new MyRouteFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_route, container, false);
        // Inflate the layout for this fragment
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Routes");
        queryInfo(getContext(), mUserID,view);
        noRoutes = view.findViewById(R.id.tv_noRoutesMyRoutes);
        noRoutes.setVisibility(View.INVISIBLE);
        return view;
    }

    private void queryInfo(Context context, String userID, View view) {
        // Instantiate the RequestQueue.

        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getUserRoutes())
                .appendQueryParameter("user_id", userID); //replace with userID

        String myUrl = builder.build().toString();
        Log.i("Query", myUrl);
        String message = "";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Gson gson = new Gson();
                        RouteContainer[] routeArr = gson.fromJson(response, RouteContainer[].class);
                        ArrayList<RouteContainer> routeList = new ArrayList<>(Arrays.asList(routeArr));
                        Collections.reverse(routeList);
                        if(routeList.size() > 0){
                            final RecyclerView recyclerView = view.findViewById(R.id.myRouteList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            recyclerView.setAdapter(new MyRoutesAdapter(Integer.parseInt(userID), context, routeList));
                            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                        }else{
                            noRoutes.setVisibility(View.VISIBLE);
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

}
