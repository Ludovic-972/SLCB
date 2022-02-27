package com.tchoutchou.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.tchoutchou.R;
import com.tchoutchou.model.Trip;

import java.util.List;

public class TripListAdapter extends BaseAdapter {

    private Context context;
    private List<Trip> tripList;
    private  LayoutInflater layoutInflater;


    public TripListAdapter(Context context, List<Trip> listData) {
        this.context = context;
        this.tripList = listData;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tripList.size();
    }

    @Override
    public Object getItem(int i) {
        return tripList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TripViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.trip_list_layout, null);
            holder = new TripViewHolder();
            holder.departureHour = view.findViewById(R.id.departureHour);
            holder.departureTown =  view.findViewById(R.id.departureTown);
            holder.arrivalHour =  view.findViewById(R.id.arrivalHour);
            holder.arrivalTown =  view.findViewById(R.id.arrivalTown);
            holder.price =  view.findViewById(R.id.price);
            holder.tripTime =  view.findViewById(R.id.tripTime);
            view.setTag(holder);
        } else {
            holder = (TripViewHolder) view.getTag();
        }

        Trip trip = this.tripList.get(i);

        holder.departureHour.setText(trip.getDepartureHour().substring(0,5));
        holder.departureTown.setText(trip.getDepartureTown());
        holder.arrivalHour.setText(trip.getArrivalHour().substring(0,5));
        holder.arrivalTown.setText(trip.getArrivalTown());
        String price = Float.toString(trip.getPrice())+"€";
        holder.price.setText(price);
        String tripTime = "~"+Integer.toString(trip.getTripTime())+"min";
        holder.tripTime.setText(tripTime);



        return view;
    }

    static class TripViewHolder{
        TextView
                departureHour,
                departureTown,
                arrivalHour,
                arrivalTown,
                price,
                tripTime;
    }
}
