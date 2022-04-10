package com.ninjadroid.app.utils;

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
import android.widget.Button;
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
import com.ninjadroid.app.activities.FriendActivity;
import com.ninjadroid.app.activities.RouteActivity;
import com.ninjadroid.app.activities.menuFragments.ProfileFragment;

import java.util.List;
import java.util.Map;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {
    private static List<String> username;
    private static List<String> followid;
    private static Context activity;
    private static int user_id;
    private static String followType;
    private static final String KEY = "UserID";

    private static final int Activity_REQUEST_CODE = 1;

    public FollowerAdapter(int user_id, Context activity, List<String> username, List<String> friendid, String followType ){
        this.activity = activity;
        this.username = username;
        this.user_id = user_id;
        this.followid =  friendid;
        this.followType = followType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item3, parent, false);
        return new ViewHolder(rowItem);


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String result = this.username.get(position);
        holder.Username.setText(result);
        holder.RemoveBut.setText("Remove");
        holder.RemoveBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int removeusername = username.indexOf(result);
                String removeItem = followid.get(removeusername);
                Log.i("here", removeItem);
                DeleteFollower(activity, removeItem, result, followType);


                username.remove(removeusername);
                followid.remove(removeusername);
                notifyItemRemoved(removeusername);
                notifyItemRangeChanged(removeusername, getItemCount());
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.username.size();
    }
    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView Username;
        private Button  RemoveBut;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.Username = view.findViewById(R.id.nameofuser);
            this.RemoveBut = view.findViewById(R.id.removebutton);

        }

        @Override
        public void onClick(View view) {
            int i = getAdapterPosition();
            String friendid = followid.get(i);
            Log.i("Friend", friendid);
            Intent intent = new Intent(activity, FriendActivity.class);
            intent.putExtra("Friend",friendid);
            intent.putExtra("Type", followType);
            ((Activity)activity).startActivityForResult(intent, R.id.nav_map);

        }

    }

    private static void DeleteFollower(Context context,String removeItem, String removefollow, String follow) {
        // Instantiate the RequestQueue.

        RequestQueue queue = Volley.newRequestQueue(context);
        Uri.Builder builder = new Uri.Builder();
        if(follow.equals("Follower")) {
            builder.scheme(URLBuilder.getScheme())
                    .encodedAuthority(URLBuilder.getEncodedAuthority())
                    .appendPath(URLBuilder.deleteFollow())
                    .appendQueryParameter("user_id", removeItem)
                    .appendQueryParameter("follow_id", String.valueOf(user_id));
        }
        else{
            builder.scheme(URLBuilder.getScheme())
                    .encodedAuthority(URLBuilder.getEncodedAuthority())
                    .appendPath(URLBuilder.deleteFollow())
                    .appendQueryParameter("user_id", String.valueOf(user_id))
                    .appendQueryParameter("follow_id", removeItem);
        }

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
                        Toast.makeText(activity,"Successfully deleted " + removefollow ,Toast.LENGTH_SHORT).show();

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