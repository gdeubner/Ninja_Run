package com.ninjadroid.app.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.R;
import com.ninjadroid.app.activities.RouteActivity;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.webServices.ShareRoute;

import java.util.List;

public class MyRoutesAdapter extends RecyclerView.Adapter<MyRoutesAdapter.ViewHolder>{
    private static List<String> routeID;
    private static List<String> town;
    private static List<String> distance;
    private static Context activity;
    private static int user_id;

    private static final int Activity_REQUEST_CODE = 2;

    public MyRoutesAdapter(int user_id, Context activity, List<String> routeID, List<String> town, List<String> distance){
        this.activity = activity;
        this.user_id = user_id;
        this.routeID = routeID;
        this.town = town;
        this.distance = distance;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_routes, parent, false);
        return new ViewHolder(rowItem);
    }

    public void onBindViewHolder(@NonNull MyRoutesAdapter.ViewHolder holder, int position) {
        String myroute = this.routeID.get(position);
        String mytown = this.town.get(position);
        String mydistance = this.distance.get(position);
        Log.i("LALLALALAA", mydistance);

        holder.myDistance.setVisibility(View.VISIBLE);
        holder.myTown.setVisibility(View.VISIBLE);
        holder.myNoRoutes.setVisibility(View.INVISIBLE);
        holder.myRouteID.setVisibility(View.VISIBLE);
        holder.myImage.setVisibility(View.VISIBLE);
        holder.myDelete.setVisibility(View.VISIBLE);

        if(mydistance.length()>8){
            mydistance = mydistance.substring(0,8);
        }

        holder.myDistance.setText("Distance: "+ mydistance);
        holder.myRouteID.setText("Route ID: " + myroute);
        holder.myTown.setText("Town: " +mytown);
        holder.myDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int removeRoute = routeID.indexOf(myroute);
                Log.i("here", Integer.toString(removeRoute));
                DeleteRoute(activity, myroute);

                routeID.remove(removeRoute);
                town.remove(removeRoute);
                distance.remove(removeRoute);
                notifyItemRemoved(removeRoute);
                notifyItemRangeChanged(removeRoute, getItemCount());

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.town.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener{
        private TextView myTown;
        private TextView myDistance;
        private TextView myRouteID;
        private ImageView myImage;
        private TextView myNoRoutes;
        private Button myDelete;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.myTown = view.findViewById(R.id.myTown);
            this.myDistance = view.findViewById(R.id.myDistance);
            this.myRouteID = view.findViewById(R.id.myRouteID);
            this.myImage = view.findViewById(R.id.myImage);
            this.myNoRoutes = view.findViewById(R.id.myNoRoutes);
            this.myDelete = view.findViewById(R.id.myDelete);

            myDistance.setVisibility(View.INVISIBLE);
            myTown.setVisibility(View.INVISIBLE);
            myNoRoutes.setVisibility(View.VISIBLE);
            myRouteID.setVisibility(View.INVISIBLE);
            myImage.setVisibility(View.INVISIBLE);
            myDelete.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            String routeId = routeID.get(getAdapterPosition());
            Log.i("Route", routeId);
            Intent intent = new Intent(activity, RouteActivity.class);
            intent.putExtra("routeID",routeId);
            intent.putExtra("From", "Friend");
            Activity activity = unwrap(view.getContext());
            activity.startActivityForResult(intent, Activity_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View view) {
            // Handle long click
            // Return true to indicate the click was handled
            PopupMenu popup = new PopupMenu(view.getContext(),myDelete);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.share:
                            ShareRoute.share(activity, Integer.parseInt(routeID.get(getAdapterPosition())),
                                    user_id);
                    }
                    return true;
                }
            });
            popup.show();
            return true;
        }

    }
    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
    private static void DeleteRoute(Context context, String routeID) {
        // Instantiate the RequestQueue.

        RequestQueue queue = Volley.newRequestQueue(context);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                    .encodedAuthority(URLBuilder.getEncodedAuthority())
                    .appendPath(URLBuilder.deleteRoute())
                    .appendQueryParameter("route_id", routeID);

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
                        if(response.substring(1,response.length()-1).equals("success")){
                            Toast.makeText(activity,"Successfully deleted route!",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity,"Route could not be deleted!",Toast.LENGTH_SHORT).show();
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
