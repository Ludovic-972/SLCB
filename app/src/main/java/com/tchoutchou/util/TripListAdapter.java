package com.tchoutchou.util;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.tchoutchou.R;
import com.tchoutchou.model.Trip;

import java.util.List;

public class TripListAdapter extends BaseAdapter {


    private final LayoutInflater layoutInflater;
    private final List<Trip> tripList;
    private final String typeCarte;
    private final Context context;


    public TripListAdapter(Context context,List<Trip> tripList, String typeCarte) {

        this.layoutInflater = LayoutInflater.from(context);
        this.tripList = tripList;
        this.typeCarte = typeCarte;
        this.context = context;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.trip_list_item, null);
        }

        Trip trip = tripList.get(i);

        String[] tripDate = trip.getTripDay().split("-");
        String tmp = tripDate[0];
        tripDate[0] = tripDate[2];
        tripDate[2] = tmp;

        ((TextView) view.findViewById(R.id.tripDate)).setText(String.join("/",tripDate));
        ((TextView) view.findViewById(R.id.departureHour)).setText(trip.getDepartureHour());
        ((TextView) view.findViewById(R.id.departureTown)).setText(trip.getDepartureTown());
        ((TextView) view.findViewById(R.id.arrivalHour)).setText(trip.getArrivalHour());
        ((TextView) view.findViewById(R.id.arrivalTown)).setText(trip.getArrivalTown());

        double tmpPrice;

        if (typeCarte.equals(context.getString(R.string.young_card)))
            tmpPrice=trip.getPrice()*0.6;
        else if (typeCarte.equals(context.getString(R.string.old_card)))
            tmpPrice=trip.getPrice()*0.5;
        else
            tmpPrice=trip.getPrice();

        String price = (Math.round(tmpPrice*100.0)/100.0) +" €";
        ((TextView) view.findViewById(R.id.price)).setText(price);

        String tripTime = "~"+ trip.getTripTime() +" min";
        ((TextView) view.findViewById(R.id.tripTime)).setText(tripTime);

        return view;
    }

}
