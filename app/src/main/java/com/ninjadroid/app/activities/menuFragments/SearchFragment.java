package com.ninjadroid.app.activities.menuFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ninjadroid.app.R;
import com.ninjadroid.app.activities.RouteActivity;
import com.ninjadroid.app.utils.SearchedAdapter;
import com.ninjadroid.app.utils.containers.RouteContainer;
import com.ninjadroid.app.webServices.SearchRoutes;
import com.ninjadroid.app.webServices.callbacks.SearchRoutesCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;

public class SearchFragment extends Fragment implements SearchedAdapter.ItemClickListener{
    private static final String ROUTE_ID_KEY = "routeID";

    // TODO: Rename and change types of parameters

    private AutoCompleteTextView dropdown;
    private RecyclerView recyclerView;
    private Button searchBtn;
    private EditText searchBar;
    private SearchedAdapter adapter;
    private TextView noRoutesFound;
    private List<RouteContainer> routeList;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.search_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //Inflate the layout for this fragment
        assignViewObjects(view);
        addDropdownFunctionality();
        addButtonFunctionality();
        return view;
    }

    private void assignViewObjects(View view) {
        dropdown = view.findViewById(R.id.dropdown_searchBy);
        recyclerView = view.findViewById(R.id.rv_searchResults);
        searchBtn = view.findViewById(R.id.btn_search);
        searchBar = view.findViewById(R.id.tv_searchBar);
        noRoutesFound = view.findViewById(R.id.tv_noRouteFound);


        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    final InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    private void addDropdownFunctionality() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.searchBy_array, R.layout.list_item_dropdown);
        dropdown.setAdapter(adapter);

    }

    private void addButtonFunctionality() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(routeList != null){
                    routeList.removeAll(routeList);
                }
                final InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                noRoutesFound.setVisibility(View.INVISIBLE);
                String search = searchBar.getText().toString();
                if(search.equals("")){
                    Toast.makeText(getActivity(), "Search bar is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dropdown.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Must select search type.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String searchType = dropdown.getText().toString().toLowerCase().replace(' ', '_');
                if(searchType.equals("route_name")){
                    searchType = "title";
                }

                SearchRoutes.search(getActivity(), searchType, search, new SearchRoutesCallback() {
                    @Override
                    public void onSuccess(RouteContainer[] routes) {
                        if(routes.length==0){
                            noRoutesFound.setVisibility(View.VISIBLE);
                        }else {
                            setRecyclerView(routes);
                        }
                    }
                });

            }
        });
    }

    private void setRecyclerView(RouteContainer[] routes) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                manager.getOrientation());
        recyclerView.setLayoutManager(manager);
        routeList = new ArrayList<>(Arrays.asList(routes));
        adapter = new SearchedAdapter(getContext(), routeList);
        adapter.setClickListener(this);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), RouteActivity.class);
        intent.putExtra(ROUTE_ID_KEY, Integer.toString(adapter.getItem(position).getRoute_id()));
        getActivity().startActivityForResult(intent, R.id.nav_map);
    }
}
