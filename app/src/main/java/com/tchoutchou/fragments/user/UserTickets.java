package com.tchoutchou.fragments.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tchoutchou.R;
import com.tchoutchou.fragments.Home;
import com.tchoutchou.model.Tickets;
import com.tchoutchou.model.Trip;
import com.tchoutchou.util.MainFragmentReplacement;
import com.tchoutchou.util.TripListAdapter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class UserTickets extends Fragment {



    public UserTickets() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private List<Trip> tripList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_tickets, container, false);
        SharedPreferences preferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        int userId = preferences.getInt("userId",0);
        Log.d("AZERTY",String.valueOf(userId));
        if (userId == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Vous n'êtes pas connecté")
                    .setCancelable(false)
                    .setMessage("Vous devez être connecté pour acheter des tickets.")
                    .setPositiveButton("Se connecter",(dialog,i) -> {
                        dialog.cancel();
                        MainFragmentReplacement.replace(fragmentManager,new UserConnection());
                    }
                    ).setNegativeButton("Aller à l'accueil",(dialog,i) -> {
                        dialog.cancel();
                        MainFragmentReplacement.replace(fragmentManager,new Home());
                    }
            );

            AlertDialog alert  = builder.create();
            alert.show();
        }else{
            ListView userTickets = view.findViewById(R.id.userTickets);
            Thread tripsRecuperation = new Thread() {
                @Override
                public void run() {
                    tripList = Trip.getUserTrips(userId);
                }
            };
            tripsRecuperation.start();

            try {
                tripsRecuperation.join();
                TripListAdapter adapter = new TripListAdapter(requireContext(),tripList);
                userTickets.setAdapter(adapter);

                userTickets.setOnItemClickListener((adapterView, view1, position, l) -> {
                    Trip trip = tripList.get(position);
                    String tripDateTime = trip.getTripDay()+" "+trip.getDepartureHour();


                    StringBuilder remainingTimeText = new StringBuilder();
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                    String titleText = trip.getDepartureTown()
                            + " -> "
                            + trip.getArrivalTown() + "\n"
                            + "le "
                            + trip.getTripDay() + " "
                            + getString(R.string.at) + " "
                            + trip.getTripTime();

                    TextView title = new TextView(requireContext());
                    title.setText(titleText);
                    title.setBackgroundColor(Color.DKGRAY);
                    title.setPadding(10, 10, 10, 10);
                    title.setGravity(Gravity.CENTER);
                    title.setTextColor(Color.WHITE);
                    title.setTextSize(20);

                    int[] remainingTime = remainingTimeUntilTripDay(tripDateTime);

                    builder.setCustomTitle(title)
                            .setMessage(setDialogMessage(remainingTime))
                            .setCancelable(false)
                            .setNeutralButton("Ok",(dialog,i) -> dialog.dismiss());
                    if (remainingTime[0] > 0){
                        builder.setNegativeButton("Supprimer ce voyage",(dialog,i) ->{
                            Thread ticketDeletion = new Thread(){
                                @Override
                                public void run() {
                                    Tickets.deleteTicket(userId,trip.getTripId());
                                }
                            };
                            ticketDeletion.start();

                            try {
                                ticketDeletion.join();
                                MainFragmentReplacement.replace(
                                        requireActivity().getSupportFragmentManager(),
                                        new UserTickets());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        });
                    }

                    AlertDialog alert  = builder.create();
                    alert.show();

                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int[] remainingTimeUntilTripDay(String tripDateTime){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime tripDay = LocalDateTime.parse(tripDateTime,formatter);

        long diff = now.until(tripDay, ChronoUnit.MILLIS);

        long diffDay = diff/(24*60*60 * 1000);
        diff = diff-(diffDay*24*60*60 * 1000);
        long diffHours = diff/(60*60 * 1000);
        diff = diff - (diffHours*60*60 * 1000);
        long diffMinutes = diff / (60*1000);

        int[] remainingTime = new int[3];

        remainingTime[0] = (int) diffDay;
        remainingTime[1] = (int) diffHours;
        remainingTime[2] = (int) diffMinutes;

        return remainingTime;
    }

    private String setDialogMessage(int[] remainingTime){
        String message = "Il reste ";

        int days = remainingTime[0];
        int hours = remainingTime[1];
        int minutes = remainingTime[2];

        if (minutes <= 30) {
            return "Départ dans moins de 30 minutes";
        }

        message += days + " jours ";
        message += hours + " heures ";
        message += minutes +" minutes ";

        message += "avant le départ";
        return message;
    }


}