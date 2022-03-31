package com.ninjadroid.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.ninjadroid.app.activities.LoginActivity;
import com.ninjadroid.app.activities.MainActivity;
import com.ninjadroid.app.activities.RouteActivity;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static List<String> data;
    private static Context activity;
    private static int user_id;
    private static String inp_text;
    private static final String KEY = "routeID";

    private static final int Activity_REQUEST_CODE = 1;

    public CustomAdapter (int user_id, Context activity, List<String> data){
        this.activity = activity;
        this.data = data;
        this.user_id = user_id;
    }

    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(CustomAdapter.ViewHolder holder, int position) {
        String result[] = this.data.get(position).split(";");
        if (result.length == 0) {
            holder.textView.setVisibility(View.INVISIBLE);
            holder.textView7.setVisibility(View.INVISIBLE);
            holder.textView3.setVisibility(View.INVISIBLE);
            holder.textView4.setVisibility(View.INVISIBLE);
            holder.textView5.setVisibility(View.INVISIBLE);
            holder.textView12.setVisibility(View.INVISIBLE);
            holder.textView8.setVisibility(View.VISIBLE);
            holder.imageView4.setVisibility(View.INVISIBLE);
            return;
        }
        holder.textView.setText(result[0]);
        if (result[1].equals("null")) {
            holder.textView7.setText("Date: N/A");
        } else {
            holder.textView7.setText("Date" + result[1].substring(result[1].indexOf(":"),result[1].indexOf("T")));
        }
        holder.textView3.setText(result[2]);
        holder.textView4.setText(result[3]);
        if (result[4].length() >= 20) {
            holder.textView5.setText(result[4].substring(0,result[4].indexOf(".") + 5));
        } else {
            holder.textView5.setText(result[4]);
        }
        holder.textView12.setText(result[5]);
    }


    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView textView;
        private TextView textView3;
        private TextView textView4;
        private TextView textView5;
        private TextView textView7;
        private TextView textView12;
        private ImageView imageView4;
        private TextView textView8;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.textView = view.findViewById(R.id.textview);
            this.textView3 = view.findViewById(R.id.textview3);
            this.textView4 = view.findViewById(R.id.textview4);
            this.textView5 = view.findViewById(R.id.textview5);
            this.textView7 = view.findViewById(R.id.textview7);
            this.textView12 = view.findViewById(R.id.textview12);
            this.imageView4 = view.findViewById(R.id.imageview4);
            this.textView8 = view.findViewById(R.id.textView8);
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
        int uid = user_id;

        System.out.println(s);
        queryInfo(activity,"" + uid,inp_text,s.substring(pos3+1,pos2));


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
