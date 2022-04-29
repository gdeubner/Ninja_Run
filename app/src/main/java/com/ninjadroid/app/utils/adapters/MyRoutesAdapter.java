package com.ninjadroid.app.utils.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
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
import com.ninjadroid.app.utils.containers.RouteContainer;
import com.ninjadroid.app.webServices.ShareRoute;

import java.util.ArrayList;
import java.util.List;

public class MyRoutesAdapter extends RecyclerView.Adapter<MyRoutesAdapter.ViewHolder>{
    private static Context activity;
    private static int user_id;
    private static ArrayList<RouteContainer> mData;

    private static final int Activity_REQUEST_CODE = 2;

    public MyRoutesAdapter(int user_id, Context activity, ArrayList<RouteContainer> mData){
        this.activity = activity;
        this.user_id = user_id;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_searched_route, parent, false);
        return new ViewHolder(rowItem);
    }

    public void onBindViewHolder(@NonNull MyRoutesAdapter.ViewHolder holder, int position) {
        holder.tv_creator.setText(holder.itemView.getContext().getString(R.string.creator,
                mData.get(position).getUsername()));
        holder.tv_town.setText(holder.itemView.getContext().getString(R.string.town,
                mData.get(position).getTown()));
        holder.tv_length.setText(holder.itemView.getContext().getString(R.string.length,
                Math.round(mData.get(position).getDistance()*10)/ 10.0));
        holder.tv_routeID.setText(holder.itemView.getContext().getString(R.string.route_id,
                mData.get(position).getRoute_id()));
        String routeName = mData.get(position).getTitle();
        if(routeName != null && routeName.length() > 0){
            holder.tv_routeName.setText(routeName);
        }
        String date = mData.get(position).getDate();
        if(date != null && date.length() > 0) {
            date = date.split("T")[0];
            holder.tv_date.setText(holder.itemView.getContext().getString(R.string.date_created, date));
        }
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener{
        TextView tv_creator;
        TextView tv_town;
        TextView tv_length;
        TextView tv_routeID;
        TextView tv_routeName;
        TextView tv_date;

        ViewHolder(View itemView) {
            super(itemView);
            tv_creator = itemView.findViewById(R.id.tv_routeCreator);
            tv_town = itemView.findViewById(R.id.tv_routeTown);
            tv_length = itemView.findViewById(R.id.tv_routeDistance);
            tv_routeID = itemView.findViewById(R.id.tv_routeId);
            tv_routeName= itemView.findViewById(R.id.tv_routeName);
            tv_date = itemView.findViewById(R.id.tv_searchDateCreated);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String routeId = Integer.toString(mData.get(getAdapterPosition()).getRoute_id());
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
            PopupMenu popup = new PopupMenu(view.getContext(), tv_routeID);
            popup.getMenuInflater().inflate(R.menu.popup_menue_share_delete, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.share:
                            ShareRoute.share(activity, mData.get(getAdapterPosition()).getRoute_id(),
                                    user_id);
                            break;
                        case R.id.delete:
                            promptUserDeleteRoute();
                            break;
                    }
                    return true;
                }
            });
            popup.show();
            return true;
        }

        private void promptUserDeleteRoute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Are you sure you want to delete this route?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteRoute(builder.getContext(),
                            Integer.toString(mData.get(getAdapterPosition()).getRoute_id()),
                            getAdapterPosition());

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

    }
    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
    private  void deleteRoute(Context context, String routeID, int itemPosition) {
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
                            //Toast.makeText(activity,"Successfully deleted route!",Toast.LENGTH_SHORT).show();
                            mData.remove(itemPosition);
                            notifyItemRemoved(itemPosition);
                            notifyItemRangeChanged(itemPosition, getItemCount());

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
