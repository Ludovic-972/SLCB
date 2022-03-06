package com.tchoutchou.fragments.user;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tchoutchou.R;
import com.tchoutchou.fragments.Home;
import com.tchoutchou.model.Tickets;
import com.tchoutchou.model.Trip;
import com.tchoutchou.util.MainFragmentReplacement;
import com.tchoutchou.util.TripListAdapter;

import java.io.File;
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
                    String[] tripDate = trip.getTripDay().split("-");
                    String tmp = tripDate[0];
                    tripDate[0] = tripDate[2];
                    tripDate[2] = tmp;
                    String tripDateTime = trip.getTripDay()+" "+trip.getDepartureHour();

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());


                    String titleText = trip.getDepartureTown()
                            + " -> "
                            + trip.getArrivalTown() + "\n"
                            + "le "
                            + String.join("/",tripDate) + " "
                            + getString(R.string.at) + " "
                            + trip.getDepartureHour();

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
                            .setPositiveButton("Imprimer ce billet", (dialog,i) ->{
                                generateTicketPDF(trip);
                                dialog.dismiss();
                            });
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
                                Toast.makeText(requireContext(), "Voyage supprimé", Toast.LENGTH_SHORT).show();
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

        if (days == 0 && hours ==0 && minutes <= 30) {
            return "Départ dans moins de 30 minutes";
        }

        message += days + " jours ";
        message += hours + " heures ";
        message += minutes +" minutes ";

        message += "avant le départ";
        return message;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateTicketPDF(Trip trip){
        if (checkPermission()) {
            File dir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "Mes Billets TchouTchou");

            if (!dir.exists()){
                dir.mkdirs();
            }

            File file = new File(dir,"");
            Toast.makeText(requireContext(), "Votre billet à été enregistré dans "+dir.toString(), Toast.LENGTH_SHORT).show();
        } else {
            requireActivity().requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

    }

    private static final int PERMISSION_REQUEST_CODE = 200;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = requireActivity().checkSelfPermission(WRITE_EXTERNAL_STORAGE);
        int permission2 = requireActivity().checkSelfPermission(READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }


}