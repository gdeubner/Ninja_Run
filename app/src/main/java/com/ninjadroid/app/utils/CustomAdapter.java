package com.ninjadroid.app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ninjadroid.app.R;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static List<String> data;
    private static Context activity;
    private static int user_id;
    private static String inp_text;

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
        holder.textView.setText(this.data.get(position));
    }


    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.textView = view.findViewById(R.id.textview);
        }

        @Override
        public void onClick(View view) {
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
        int pos2 = s.indexOf('\n');
        int pos3 = s.indexOf(':');
        int uid = user_id;

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
