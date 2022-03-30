package com.ninjadroid.app.utils;

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

import java.util.List;

public class SharedAdapter extends RecyclerView.Adapter<SharedAdapter.ViewHolder> {
    private static List<String> data;
    private static Context activity;
    private static int user_id;
    private static String inp_text;
    private static final String KEY = "routeID";

    private static final int Activity_REQUEST_CODE = 1;

    public SharedAdapter(int user_id, Context activity, List<String> data){
        this.activity = activity;
        this.data = data;
        this.user_id = user_id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String result[] = this.data.get(position).split(";");
        if (result.length == 0) {
            return;
        }
        holder.textView.setText(result[1]);
        holder.textView7.setText(result[0]);
        holder.textView3.setText(result[2]);
        holder.textView4.setText("Shared by:" + result[3].substring(result[3].indexOf(":") + 1));
    }


    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView textView;
        private TextView textView3;
        private TextView textView4;
        private TextView textView7;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.textView = view.findViewById(R.id.textview);
            this.textView3 = view.findViewById(R.id.textview3);
            this.textView4 = view.findViewById(R.id.textview4);
            this.textView7 = view.findViewById(R.id.textview7);
        }

        @Override
        public void onClick(View view) {
            //todo: this is a temporary fix for getting the routeID until this recycler view is fully implemented
            //************
            TextView tv = view.findViewById(R.id.textview);
            String routeID = tv.getText().toString().split("\n")[0].split(":")[1];
            //************

            Intent intent = new Intent(activity, RouteActivity.class);
            intent.putExtra(KEY, routeID);
            ((Activity)activity).startActivityForResult(intent, Activity_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View view) {
            // Handle long click
            // Return true to indicate the click was handled
            PopupMenu popup = new PopupMenu(view.getContext(),textView);
            popup.getMenuInflater().inflate(R.menu.popup_menu2, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.remove:
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
            builder.setTitle("Are you sure you want to delete this route?");


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    shareFunc2(getLayoutPosition(),activity);
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

    public static void shareFunc2(int pos, Context activity) {
        String s = data.get(pos);
        int pos2 = s.indexOf(';');
        int pos3 = s.indexOf(':');

        queryInfo(activity,s.substring(pos3+1,pos2));


    }

    private static void queryInfo(Context context,String routeID) {
        // Instantiate the RequestQueue.
        Log.i("Justin", "17"); //replace with userID
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
