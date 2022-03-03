package com.tchoutchou.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;

import com.tchoutchou.R;
import com.tchoutchou.model.Trip;
import com.tchoutchou.model.UserTrips;

import java.util.List;

public class TripListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Trip> tripList;
    private final LayoutInflater layoutInflater;
    private int user_id;
    private int buyedTripId;

    public TripListAdapter(Context context, List<Trip> listData,int user_id) {
        this.context = context;
        this.tripList = listData;
        this.layoutInflater = LayoutInflater.from(context);
        this.user_id = user_id;

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
        buyedTripId = 0;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.trip_list_layout, null);
            holder = new TripViewHolder();
            holder.tripDay = view.findViewById(R.id.tripDay);
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

        //holder.tripDay.setText(trip.getTripDay());
        holder.departureHour.setText(trip.getDepartureHour());
        holder.departureTown.setText(trip.getDepartureTown());
        holder.arrivalHour.setText(trip.getArrivalHour());
        holder.arrivalTown.setText(trip.getArrivalTown());
        String price = trip.getPrice() +"€";
        holder.price.setText(price);
        String tripTime = "~"+ trip.getTripTime() +"min";
        holder.tripTime.setText(tripTime);


        view.setOnClickListener(view1 -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

            builder.setCancelable(false)
                    .setView(layoutInflater.inflate(R.layout.trip_infos,null));



            AlertDialog alert = builder.create();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alert.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT ;
            lp.height =  WindowManager.LayoutParams.WRAP_CONTENT ;
            alert.show();
            alert.getWindow().setAttributes(lp);

            TextView title = alert.findViewById(R.id.title);
            title.setText(trip.getDepartureTown()+" -> "+trip.getArrivalTown());

            Button close = alert.findViewById(R.id.closingButton);
            close.setOnClickListener(v -> alert.dismiss());

            Button buyButton = alert.findViewById(R.id.buyButton);

            buyButton.setOnClickListener(v -> {
                Thread buy = new Thread(){
                    @Override
                    public void run(){
                        UserTrips.addUserTrip(trip.getTripId(),user_id);
                    }
                };

                buy.start();
                try {
                    buy.join();
                    this.buyedTripId = trip.getTripId();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
                alert.dismiss();
            });
        });


        return view;
    }

    public int getBuyedTripId(){
        return buyedTripId;
    }

    static class TripViewHolder{
        TextView
                tripDay,
                departureHour,
                departureTown,
                arrivalHour,
                arrivalTown,
                price,
                tripTime;
    }
}
