package com.tchoutchou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tchoutchou.model.Tickets;
import com.tchoutchou.model.Trip;
import com.tchoutchou.util.NoConnectionException;
import com.tchoutchou.util.TripListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TripActivity extends AppCompatActivity{


    private Bundle tripsInfos;
    private List<Trip> tripList = new ArrayList<>();
    private boolean ticketAlreadyExists;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        tripsInfos = getIntent().getExtras();
        initActionBar();


        ListView tripListView = findViewById(R.id.trips);

        SharedPreferences preferences = getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        int user_id = preferences.getInt("userId",0);

        Thread tripsRecuperation = new Thread() {
                @Override
                public void run() {
                    try {
                        tripList = Trip.getTrips(tripsInfos);
                    } catch (NoConnectionException e) {
                        Intent intent = new Intent(TripActivity.this, NoConnectionActivity.class);
                        startActivity(intent);
                    }
                }
            };
        tripsRecuperation.start();
        try {
            tripsRecuperation.join();
            TripListAdapter adapter = new TripListAdapter(TripActivity.this,tripList, preferences.getString("Carte", ""));
            tripListView.setAdapter(adapter);

            tripListView.setOnItemClickListener((adapterView, view, position, l) -> {
                if(user_id != 0) {

                    Thread existenceOfTicket = new Thread(){
                        @Override
                        public void run() {
                            try {
                                ticketAlreadyExists = Tickets.ticketExists(user_id,tripList.get(position).getTripId());
                            } catch (NoConnectionException e) {
                                Intent intent = new Intent(TripActivity.this, NoConnectionActivity.class);
                                startActivity(intent);
                            }
                        }
                    };
                    existenceOfTicket.start();

                    try{
                        existenceOfTicket.join();
                        if(!ticketAlreadyExists) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(TripActivity.this);

                            Trip trip = tripList.get(position);

                            TextView title = new TextView(TripActivity.this);
                            String titleText = trip.getDepartureTown()
                                    + " -> "
                                    + trip.getArrivalTown() + "\n"
                                    + getString(R.string.the)+" "
                                    + String.join("/", tripsInfos.getString("tripDay").split("-")) + " "
                                    + getString(R.string.at) + " "
                                    + trip.getDepartureHour();

                            title.setText(titleText);
                            title.setBackgroundColor(Color.DKGRAY);
                            title.setPadding(10, 10, 10, 10);
                            title.setGravity(Gravity.CENTER);
                            title.setTextColor(Color.WHITE);
                            title.setTextSize(20);

                            builder.setCustomTitle(title)
                                    .setCancelable(false)
                                    .setMessage("Voulez achetez ce billet ?")
                                    .setPositiveButton("Oui", (dialogInterface, i) -> {
                                        Thread buy = new Thread() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Tickets.addTickets(user_id,trip.getTripId());
                                                } catch (NoConnectionException e) {
                                                    Intent intent = new Intent(TripActivity.this, NoConnectionActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        };
                                        buy.start();
                                        try {
                                            buy.join();
                                            dialogInterface.dismiss();
                                            Toast.makeText(TripActivity.this,"Votre achat à été réalisé.",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(TripActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    })
                                    .setNegativeButton("Non", (dialogInterface, i) -> dialogInterface.dismiss());

                            AlertDialog alert = builder.create();
                            alert.show();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(TripActivity.this);

                            builder.setTitle("Achat Impossible")
                                    .setCancelable(false)
                                    .setMessage("Vous avez déjà achetez ce ticket")
                                    .setNeutralButton("Ok",(dialog,i2) -> dialog.dismiss());

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }catch(InterruptedException ignored){}
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initActionBar(){
        Objects.requireNonNull(getSupportActionBar()).show();
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.trip_action_bar);

        View view = getSupportActionBar().getCustomView();

        TextView tripDay = view.findViewById(R.id.tripActionBarText);

        String sb = getString(R.string.trip_action_bar_text) +
                "\n" +
                String.join("/", tripsInfos.getString("tripDay").split("-")) +
                " " +
                getString(R.string.at) +
                " " +
                tripsInfos.getString("departureHour");
        tripDay.setText(sb);



    }


}