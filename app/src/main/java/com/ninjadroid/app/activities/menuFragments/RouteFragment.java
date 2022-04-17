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
import com.ninjadroid.app.utils.MyRoutesAdapter;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.containers.HistoryContainer;

import java.util.ArrayList;

public class RouteFragment extends Fragment {
    private String mUserID;
    private static final String USERID = "key";

    public RouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userID Parameter 1.
     * @return A new instance of fragment RouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RouteFragment newInstance(String userID) {
        RouteFragment fragment = new RouteFragment();
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
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        // Inflate the layout for this fragment
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Routes");
        queryInfo(getContext(), mUserID,view);
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
                        String[] result = response.split(",");
                        final RecyclerView recyclerView = view.findViewById(R.id.myRouteList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                        if(result.length > 1) {
                            ArrayList<ArrayList<String>> data = populate(result);
                            recyclerView.setAdapter(new MyRoutesAdapter(Integer.parseInt(userID), context, data.get(0), data.get(1), data.get(2)));
                        }else{
                            ArrayList<String> routeid = new ArrayList<>();
                            ArrayList<String> town = new ArrayList<>();
                            ArrayList<String> dist = new ArrayList<>();
                            recyclerView.setAdapter(new MyRoutesAdapter(Integer.parseInt(userID), context, routeid, town, dist));
                        }
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

    public ArrayList<ArrayList<String>> populate(String[] str) {
        ArrayList<String> route = new ArrayList<>();
        ArrayList<String> town = new ArrayList<>();
        ArrayList<String> dist = new ArrayList<>();
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

        int count = 0;
        for (String s : str) {
            String temp = "";
            Log.i("RouteIDD", s);
            if (count == 0) {
                temp = temp + s.substring(13);
                Log.i("RouteID", temp);
                route.add(temp);
            } else if (s.contains("]")) {
                temp = temp + s.substring(11, s.length() - 2);
                Log.i("Distance", temp);
                dist.add(temp);
            } else if (count % 3 == 0) {
                temp = temp + s.substring(12);
                Log.i("RouteID", temp);
                route.add(temp);
            } else if (count % 3 == 1) {
                temp = temp + s.substring(8, s.length()-1);
                Log.i("Town", temp);
                town.add(temp);
            } else if (count % 3 == 2) {
                temp = temp + s.substring(11, s.length()-1);
                Log.i("Distance", temp);
                dist.add(temp);
            }
            Log.i("infooo", temp);
            count++;
        }
        data.add(route);
        data.add(town);
        data.add(dist);
        return data;
    };
}
