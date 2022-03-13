package com.tchoutchou.fragments.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tchoutchou.NoConnectionActivity;
import com.tchoutchou.R;
import com.tchoutchou.TicketPdfActivity;
import com.tchoutchou.fragments.Home;
import com.tchoutchou.model.Tickets;
import com.tchoutchou.model.Trip;
import com.tchoutchou.util.MainFragmentReplacement;
import com.tchoutchou.util.NoConnectionException;
import com.tchoutchou.util.TripListAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;


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
        /*Si l'utilisateur n'est pas connecté il aura le choix d'aller vers la page d'accueil ou vers
        * la page de connexion*/
        if (userId == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(getString(R.string.must_be_connected))
                    .setCancelable(false)
                    .setMessage(getString(R.string.must_be_connected_to_buy))
                    .setPositiveButton(R.string.log_in,(dialog, i) -> {
                        dialog.cancel();
                        MainFragmentReplacement.replace(fragmentManager,new UserConnection());
                    }
                    ).setNegativeButton(getString(R.string.go_to_homepage),(dialog, i) -> {
                        dialog.cancel();
                        MainFragmentReplacement.replace(fragmentManager,new Home());
                    }
            );

            AlertDialog alert  = builder.create();
            alert.show();
        }else{
            //Récupération des trajets de l'utilisateur
            ListView userTickets = view.findViewById(R.id.userTickets);
            Thread tripsRecuperation = new Thread() {
                @Override
                public void run() {
                    try {
                        tripList = Trip.getUserTrips(userId);
                    } catch (NoConnectionException e) {
                        Intent intent = new Intent(requireActivity(), NoConnectionActivity.class);
                        startActivity(intent);
                    }
                }
            };
            tripsRecuperation.start();

            try {
                tripsRecuperation.join();
                TripListAdapter adapter = new TripListAdapter(requireContext(),tripList,preferences.getString("Carte",""));
                userTickets.setAdapter(adapter);

                userTickets.setOnItemClickListener((adapterView, view1, position, l) -> {
                    Trip trip = tripList.get(position);

                    String[] tripDate = trip.getTripDay().split("-");
                    String tmp = tripDate[0];
                    tripDate[0] = tripDate[2];
                    tripDate[2] = tmp;
                    String tripDateTime = trip.getTripDay()+" "+trip.getDepartureHour();

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());


                    String titleText = trip.getDepartureTown()
                            + " -> "
                            + trip.getArrivalTown() + "\n"
                            + getString(R.string.the)
                            + String.join("/",tripDate) + " "
                            + getString(R.string.at) + " "
                            + trip.getArrivalHour();

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
                            .setNeutralButton("Ok",(dialog,i) -> dialog.dismiss())
                            .setPositiveButton(getString(R.string.see_ticket), (dialog, i) ->{
                                Intent intent = new Intent(requireActivity(), TicketPdfActivity.class);
                                intent.putExtra("Trip",trip);
                                startActivity(intent);
                                dialog.dismiss();
                            });

                    /*Si le temps avant le trajet est supérieur à 24 heures alors l'utilisateur
                    * aura la possibilité de supprimer son billet*/
                    if (remainingTime[0] > 0){
                        builder.setNegativeButton(getString(R.string.delete_trip),(dialog, i) ->{
                            Thread ticketDeletion = new Thread(){
                                @Override
                                public void run() {
                                    try {
                                        Tickets.deleteTicket(userId,trip.getTripId());
                                    } catch (NoConnectionException e) {
                                        Intent intent = new Intent(requireActivity(), NoConnectionActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            };
                            ticketDeletion.start();

                            try {
                                ticketDeletion.join();
                                Toast.makeText(requireContext(), getString(R.string.trip_deleted), Toast.LENGTH_SHORT).show();
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

    /*Retourne un tableau avec en position:
    * -0 : le nombre de jour avant de départ
    * -1 : le nombre d'heures avant de départ
    * -2 : le nombre de minutes avant de départ
    */
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

    /*Renvoie le message qui dira à l'utilisateur combien de temps il reste avec son départ*/
    private String setDialogMessage(int[] remainingTime){
        String message = getString(R.string.remaining_time1)+" ";

        int days = remainingTime[0];
        int hours = remainingTime[1];
        int minutes = remainingTime[2];

        if (days == 0 && hours ==0) {
            if(minutes <= 30)
                return getString(R.string.departure_in_less_than_30);
            else if (minutes == 0)
                return getString(R.string.bus_already_gone);
        }

        message += days + " "+getString(R.string.days)+" ";
        message += hours + " "+getString(R.string.hours)+" ";
        message += minutes +" "+getString(R.string.minutes)+" ";

        message += getString(R.string.remaining_time2);
        return message;
    }

}