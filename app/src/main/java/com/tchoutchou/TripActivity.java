package com.tchoutchou;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tchoutchou.model.Trip;
import com.tchoutchou.util.TripListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TripActivity extends AppCompatActivity {

    private List<Trip> trips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);

        getSupportActionBar().show();
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.trip_action_bar);

        View view=getSupportActionBar().getCustomView();
        Bundle tripsInfos = getIntent().getExtras();

        TextView name=view.findViewById(R.id.tripDay);
        name.setText(tripsInfos.getString("tripDay"));

        ListView tripListView = findViewById(R.id.trips);

        Thread tripsRecuperation = new Thread(){
            @Override
            public void run() {
                trips = Trip.getTrips(tripsInfos);
            }
        };
        tripsRecuperation.start();
        try {
            tripsRecuperation.join() ;
            tripListView.setAdapter(new TripListAdapter(this,trips));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}