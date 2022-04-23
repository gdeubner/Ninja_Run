package com.ninjadroid.app.utils.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ninjadroid.app.utils.containers.SharedContainer;

import java.util.List;

public class SharedAdapter extends RecyclerView.Adapter<SharedAdapter.ViewHolder> {
    private  List<SharedContainer> data;
    private  Context activity;
    private  int user_id;
    private  String inp_text;
    private  final String KEY = "routeID";


    public SharedAdapter(int user_id, Context activity, List<SharedContainer> data){
        this.activity = activity;
        this.data = data;
        this.user_id = user_id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shared_route,
                parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_townShared.setText(activity.getResources().getString(R.string.history_town,
                data.get(position).getTown()));
        holder.tv_distanceShared.setText(activity.getResources().getString(R.string.history_distance,
                data.get(position).getDistance()));
        holder.tv_sharedBy.setText(activity.getResources().getString(R.string.shared_by,
                data.get(position).getUsername()));
        holder.tv_routeIdShared.setText(activity.getResources().getString(R.string.history_route_id,
                data.get(position).getRoute_id()));
        String name = data.get(position).getTitle();
        if(name != null && name.length() > 0){
            holder.tv_routeName.setText(name);

        }
    }


    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        private TextView tv_townShared;
        private TextView tv_distanceShared;
        private TextView tv_sharedBy;
        private TextView tv_routeIdShared;
        private TextView tv_routeName;
        private ImageView img_routeImage;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.tv_townShared = view.findViewById(R.id.tv_townShared);
            this.tv_distanceShared = view.findViewById(R.id.tv_distanceShared);
            this.tv_sharedBy = view.findViewById(R.id.tv_sharedBy);
            this.tv_routeIdShared = view.findViewById(R.id.tv_routeIdShared);
            this.tv_routeName = view.findViewById(R.id.tv_routeNameShared);
            this.img_routeImage = view.findViewById(R.id.img_routeImage);
        }

        @Override
        public void onClick(View view) {
            Log.i("SharedAdapter", "item was clicked");
            //todo: this is a temporary fix for getting the routeID until this recycler view is fully implemented
            //************
            TextView tv = view.findViewById(R.id.tv_routeIdShared);
            String routeID = tv.getText().toString().split("\n")[0].split(":")[1].trim();
            //************

            Intent intent = new Intent(activity, RouteActivity.class);
            intent.putExtra(KEY, routeID);
            ((Activity)activity).startActivityForResult(intent, R.id.nav_map);
        }

        @Override
        public boolean onLongClick(View view) {
            // Handle long click
            // Return true to indicate the click was handled
            PopupMenu popup = new PopupMenu(view.getContext(),tv_routeIdShared);
            popup.getMenuInflater().inflate(R.menu.popup_menu2, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.remove:
                            shareFunc(data.get(getLayoutPosition()).getRoute_id(),getLayoutPosition());
                    }
                    return true;
                }
            });
            popup.show();
            return true;
        }

    }

    public void shareFunc(int routeId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Are you sure you want to delete this route?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                queryInfo(activity,Integer.toString(routeId), position);
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

    private void queryInfo(Context context,String routeID, int itemPosition) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.deleteShared())
                .appendQueryParameter("route_id",routeID)
                .appendQueryParameter("user_id","" + user_id);//replace with userID

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
                        Toast.makeText(activity,"Successfully deleted route " + routeID,Toast.LENGTH_SHORT).show();

                        data.remove(itemPosition);
                        notifyItemRemoved(itemPosition);
                        notifyItemRangeChanged(itemPosition, getItemCount());

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
