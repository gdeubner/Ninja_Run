package com.ninjadroid.app.activities.menuFragments;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ninjadroid.app.utils.containers.RouteContainer;
import com.ninjadroid.app.webServices.SearchRoutes;
import com.ninjadroid.app.webServices.callbacks.SearchRoutesCallback;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private static final String USERID = "key";

    // TODO: Rename and change types of parameters

    private Spinner spinner;
    private RecyclerView recyclerView;
    private Button searchBtn;
    private TextView searchBar;

    public SearchFragment() {
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
    public static SearchFragment newInstance(String userID) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(USERID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //Inflate the layout for this fragment
        assignViewObjects(view);
        addSpinnerfunctionality();
        addButtonFunctionality();
        return view;
    }

    private void assignViewObjects(View view) {
        spinner = view.findViewById(R.id.spn_searchBy);
        recyclerView = view.findViewById(R.id.rv_searchResults);
        searchBtn = view.findViewById(R.id.btn_search);
        searchBar = view.findViewById(R.id.tv_searchBar);
    }

    private void addSpinnerfunctionality() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.searchBy_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void addButtonFunctionality() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchBar.getText().toString();
                if(search.equals("")){
                    Toast.makeText(getActivity(), "Search bar is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int searchTypeIndex = spinner.getSelectedItemPosition();
                String searchType = getResources().getStringArray(R.array.searchBy_arrayEnglish)[searchTypeIndex];

                SearchRoutes.search(getActivity(), searchType, search, new SearchRoutesCallback() {
                    @Override
                    public void onSuccess(RouteContainer[] routes) {

                    }
                });

            }
        });
    }

}
