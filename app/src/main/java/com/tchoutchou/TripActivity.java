package com.tchoutchou;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.tchoutchou.model.Towns;
import com.tchoutchou.model.Trip;
import com.tchoutchou.util.TripListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TripActivity extends AppCompatActivity {

    List<Trip> trips = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);

        Bundle tripInfos = getIntent().getExtras();

        ListView tripListView = findViewById(R.id.trips);

        Thread tripsRecuperation = new Thread(){
            @Override
            public void run() {
                trips = Trip.getTrips(tripInfos);
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