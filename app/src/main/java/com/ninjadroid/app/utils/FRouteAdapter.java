package com.ninjadroid.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ninjadroid.app.R;
import com.ninjadroid.app.activities.RouteActivity;

import java.util.List;

public class FRouteAdapter extends RecyclerView.Adapter<FRouteAdapter.ViewHolder>{
    private static List<String> routeID;
    private static List<String> town;
    private static List<String> distance;
    private static Context activity;
    private static int user_id;

    private static final int Activity_REQUEST_CODE = 2;

    public FRouteAdapter(int user_id, Context activity, List<String> routeID, List<String> town, List<String> distance){
        this.activity = activity;
        this.user_id = user_id;
        this.routeID = routeID;
        this.town = town;
        this.distance = distance;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item5, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String routeF = this.routeID.get(position);
        String townF = this.town.get(position);
        String distanceF = this.distance.get(position);
        Log.i("LALLALALAA", "INNNNNSS");

        holder.fDistance.setVisibility(View.VISIBLE);
        holder.fTown.setVisibility(View.VISIBLE);
        holder.fNoRoutes.setVisibility(View.INVISIBLE);
        holder.fRouteID.setVisibility(View.VISIBLE);
        holder.fImage.setVisibility(View.VISIBLE);

        holder.fDistance.setText("Distance: "+ distanceF);
        holder.fRouteID.setText("Route ID: " + routeF);
        holder.fTown.setText("Town: " +townF);
    }

    @Override
    public int getItemCount() {
        return this.town.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView fTown;
        private TextView fDistance;
        private TextView fRouteID;
        private ImageView fImage;
        private TextView fNoRoutes;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.fTown = view.findViewById(R.id.fTown);
            this.fDistance = view.findViewById(R.id.fDistance);
            this.fRouteID = view.findViewById(R.id.fRouteID);
            this.fImage = view.findViewById(R.id.fImage);
            this.fNoRoutes = view.findViewById(R.id.fNoRoutes);

            fDistance.setVisibility(View.INVISIBLE);
            fTown.setVisibility(View.INVISIBLE);
            fNoRoutes.setVisibility(View.VISIBLE);
            fRouteID.setVisibility(View.INVISIBLE);
            fImage.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            int i = getAdapterPosition();
            String routeId = routeID.get(i);
            Log.i("Route", routeId);
            Intent intent = new Intent(activity, RouteActivity.class);
            intent.putExtra("routeID",routeId);
            intent.putExtra("From", "Friend");
            Activity activity = unwrap(view.getContext());
            activity.startActivityForResult(intent, Activity_REQUEST_CODE);
        }

    }
    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
}
