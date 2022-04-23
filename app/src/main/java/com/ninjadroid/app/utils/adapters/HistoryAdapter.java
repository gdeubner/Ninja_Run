package com.ninjadroid.app.utils.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.R;
import com.ninjadroid.app.activities.RouteActivity;
import com.ninjadroid.app.utils.URLBuilder;
import com.ninjadroid.app.utils.Utils;
import com.ninjadroid.app.utils.containers.HistoryContainer;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private static List<HistoryContainer> data;
    private static Context activity;
    private static int user_id;
    private static String inp_text;
    private static final String KEY = "routeID";

    public HistoryAdapter(int user_id, Context activity, List<HistoryContainer> data){
        this.activity = activity;
        this.data = data;
        this.user_id = user_id;
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_history, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        holder.tv_routeID.setText(activity.getResources().getString(R.string.history_route_id,
                data.get(position).getRoute_id()));
        holder.tv_calories.setText(activity.getResources().getString(R.string.history_calories,
                data.get(position).getCalories()));
        int time = data.get(position).getDuration();
        int min = time /60;
        int sec = time % 60;
        holder.tv_duration.setText(activity.getResources().getString(R.string.history_duration,
                min, sec));
        holder.tv_historyDistance.setText(activity.getResources().getString(R.string.history_distance,
                data.get(position).getDistance()));
        holder.tv_date.setText(activity.getResources().getString(R.string.history_date,
                data.get(position).getDatetime().split("T")[0]));
        holder.tv_town.setText(activity.getResources().getString(R.string.history_town,
                data.get(position).getTown()));
        String name = data.get(position).getTitle();
        if(name != null && name.length() >0) {
            holder.tv_name.setText(name);
        }
    }


    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView tv_routeID;
        private TextView tv_calories;
        private TextView tv_duration;
        private TextView tv_historyDistance;
        private TextView tv_date;
        private TextView tv_town;
        private TextView tv_name;
        private ImageView img_routeImage;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.tv_routeID = view.findViewById(R.id.tv_routeID);
            this.tv_calories = view.findViewById(R.id.tv_calories);
            this.tv_duration = view.findViewById(R.id.tv_duration);
            this.tv_historyDistance = view.findViewById(R.id.tv_historyDistance);
            this.tv_date = view.findViewById(R.id.tv_date);
            this.tv_town = view.findViewById(R.id.tv_town);
            this.tv_name = view.findViewById(R.id.tv_historyRouteName);
            this.img_routeImage = view.findViewById(R.id.img_routeImage);
        }

        @Override
        public void onClick(View view) {
            int routeId = data.get(getLayoutPosition()).getRoute_id();
            Intent intent = new Intent(activity, RouteActivity.class);
            intent.putExtra(KEY, routeId + "");
            ((Activity)activity).startActivityForResult(intent, R.id.nav_map);
        }

        @Override
        public boolean onLongClick(View view) {
            // Handle long click
            // Return true to indicate the click was handled
            PopupMenu popup = new PopupMenu(view.getContext(),tv_routeID);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.share:
                            shareFunc();
                    }
                    return true;
                }
            });
            popup.show();
            return true;
        }

        public void shareFunc() {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Please specify who you'd like to share the route with");

            final EditText input1 = new EditText(activity);
            input1.setHint("Username of Friend");

            input1.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input1);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    inp_text = input1.getText().toString();
                    queryInfo(activity,Integer.toString(user_id), inp_text,
                            Integer.toString(data.get(getLayoutPosition()).getRoute_id()));

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

    private static void queryInfo(Context context, String userID, String sharedUn, String routeID) {
        // Instantiate the RequestQueue.
        Log.i("Justin", "17"); //replace with userID
        RequestQueue queue = Volley.newRequestQueue(context);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(URLBuilder.getScheme())
                .encodedAuthority(URLBuilder.getEncodedAuthority())
                .appendPath(URLBuilder.getShareRoute())
                .appendQueryParameter("user_id", userID)
                .appendQueryParameter("shared_username",sharedUn)
                .appendQueryParameter("route_id",routeID); //replace with userID

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
                        if (response.equals("\"success\"")) {
                            Toast.makeText(activity,"Successfully shared route with " + inp_text,Toast.LENGTH_SHORT).show();
                        } else if (response.equals("\"duplicate\"")) {
                            Toast.makeText(activity,"Route already shared with " + inp_text,Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity,"User " + inp_text + " does not exist",Toast.LENGTH_SHORT).show();
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
