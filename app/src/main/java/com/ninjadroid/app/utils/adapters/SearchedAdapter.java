package com.ninjadroid.app.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ninjadroid.app.R;
import com.ninjadroid.app.utils.containers.RouteContainer;

import java.util.List;

public class SearchedAdapter extends RecyclerView.Adapter<SearchedAdapter.ViewHolder>{
    private List<RouteContainer> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public SearchedAdapter(Context context, List<RouteContainer> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_searched_route, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public RouteContainer getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}