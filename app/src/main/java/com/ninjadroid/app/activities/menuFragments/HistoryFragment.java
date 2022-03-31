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
import com.ninjadroid.app.utils.containers.HistoryContainer;
import com.ninjadroid.app.utils.containers.ProfileContainer;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private static final String USERID = "key";

    // TODO: Rename and change types of parameters
    private String mUserID;
    private HistoryContainer historyC;

    public HistoryFragment() {
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
    public static HistoryFragment newInstance(String userID) {
        HistoryFragment fragment = new HistoryFragment();
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
        historyC = (HistoryContainer) getArguments().getSerializable("container");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        // Inflate the layout for this fragment
        queryInfo(getContext(), mUserID,view);

        return view;
    }

    private void queryInfo(Context context, String userID,View view) {
        // Instantiate the RequestQueue.

        RecyclerView recyclerView = view.findViewById(R.id.histRec);
        Log.i("Justin", userID); //replace with userID
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getRouteHistoryPath())
                .appendQueryParameter("user_id", userID); //replace with userID

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


                        ArrayList<String> data = populateList(result);

                        final RecyclerView recyclerView = getView().findViewById(R.id.histRec);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                        recyclerView.setAdapter(new CustomAdapter(Integer.parseInt(userID), context,data)); //change later from 17 to uid
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
        int count = 0;
        String temp = "";
        ArrayList<String> data = new ArrayList<>();
        for (String s : str) {
            if (count == 6) {
                count = 0;

                //System.out.println(temp);
                data.add(temp);
                temp = "";
            }
            s = s.replace("[", "");
            s = s.replace("{","");
            s = s.replace("}","");
            s = s.replace("\"","");
            String s1 = (s.charAt(0) + "").toUpperCase() + s.substring(1);
            //System.out.println(s1);
            temp += s1 + ";";
            count++;
        }
        temp = temp.replace("]","");
        temp =  temp.replace("{","");
        temp = temp.replace("}","");
        temp = temp.replace("\"","");
        System.out.println(temp);
        data.add(temp);

        return data;
    }


}
