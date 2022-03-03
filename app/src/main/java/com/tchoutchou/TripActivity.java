package com.tchoutchou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tchoutchou.model.Trip;
import com.tchoutchou.util.TripListAdapter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TripActivity extends AppCompatActivity {


    Bundle tripsInfos;
    private List<Trip> trips = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);
        tripsInfos = getIntent().getExtras();
        initActionBar();


        ListView tripListView = findViewById(R.id.trips);

        SharedPreferences preferences = getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        int user_id = preferences.getInt("userId",0);
        Thread tripsRecuperation = new Thread() {
                @Override
                public void run() { trips = Trip.getTrips(tripsInfos);
                }
            };
        tripsRecuperation.start();
        try {
            tripsRecuperation.join();
            TripListAdapter adapter = new TripListAdapter(this, trips,preferences.getInt("id",0));
            tripListView.setAdapter(adapter);
            if (adapter.getBuyedTripId() != 0){
                if (user_id == 0){
                    SharedPreferences noAccountPreferences = getSharedPreferences("noAccountUser", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = noAccountPreferences.edit();
                    String formertripList = noAccountPreferences.getString("trips","");
                    editor.putString("trips",formertripList+adapter.getBuyedTripId()+"|");
                    editor.apply();
                }
                Intent intent = new Intent(TripActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initActionBar(){
        getSupportActionBar().show();
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.trip_action_bar);

        View view = getSupportActionBar().getCustomView();

        TextView tripDay = view.findViewById(R.id.tripDay);

        String sb = getString(R.string.trip_action_bar_text) +
                "\n" +
                String.join("/", tripsInfos.getString("tripDay").split("-")) +
                " " +
                getString(R.string.trip_action_bar_at) +
                " " +
                tripsInfos.getString("departureHour");
        tripDay.setText(sb);



    }


}