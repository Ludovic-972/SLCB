package com.tchoutchou.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tchoutchou.R;
import com.tchoutchou.TripActivity;
import com.tchoutchou.model.Towns;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Home extends Fragment {

    public Home() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    AutoCompleteTextView departureTown, arrivalTown;
    EditText departureHour, tripDay;

    SharedPreferences preferences;
    private List<String> towns = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        preferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        // Reset shared preferences
        //preferences.edit().clear().commit();

        /*Reset database
        UserBD userBD = new UserBD(getContext());
        userBD.removeAllUsers();
         */

        /* Détecter la position avec le GPS et entrer dans  departure Town*/
        departureTown = root.findViewById(R.id.departureTown);
        arrivalTown = root.findViewById(R.id.arrivalTown);
        departureHour = root.findViewById(R.id.departureHour);
        Button goToRides = root.findViewById(R.id.toRides);

        TextView greetings = root.findViewById(R.id.greetings);
        String username = preferences.getString("firstname","");

        String text = greetings.getText().toString();

        greetings.setText(text+" "+username+"\uD83D\uDC4B,");

        Thread townsRecuperation = new Thread(){
            @Override
            public void run() {
                towns = Towns.getAllTowns();
            }
        };
        townsRecuperation.start();


        try {
            townsRecuperation.join() ;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    towns);

            departureTown.setAdapter(adapter);
            arrivalTown.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        departureHour.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (timePicker, hourOfDay, minutes) -> departureHour.setText(String.format("%02d:%02d", hourOfDay, minutes)),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });

        goToRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notEmptyInputs()) {
                    if (towns.contains(departureTown.getText().toString()) && towns.contains(arrivalTown.getText().toString())) {
                        Intent intent = new Intent(requireActivity(), TripActivity.class);

                        intent.putExtra("departureTown", departureTown.getText().toString());
                        intent.putExtra("arrivalTown", arrivalTown.getText().toString());
                        intent.putExtra("departureHour", departureHour.getText().toString());
                        intent.putExtra("tripDay", tripDay.getText().toString());

                        startActivity(intent);
                    }else{
                        Toast.makeText(requireContext(), "Veuillez entrer des villes existantes",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(requireContext(), "Veuillez remplir tous les champs",
                            Toast.LENGTH_SHORT).show();
                }
            }

            private boolean notEmptyInputs() {
                return !departureTown.getText().toString().equals("")
                        && !arrivalTown.getText().toString().equals("")
                        && !departureHour.getText().toString().equals("")
                        && !tripDay.getText().toString().equals("");
            }
        });

        tripDay = root.findViewById(R.id.tripDay);
        tripDay.setOnClickListener(view -> {

            Calendar c = Calendar.getInstance();

            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
                String date = "";
                date+= (dayOfMonth<10) ? "0"+dayOfMonth+"-" : dayOfMonth+"-";
                date+= (monthOfYear<10) ? "0"+(monthOfYear+1)+"-" : (monthOfYear+1)+"-";
                date+= year;
                tripDay.setText(date);
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                    ,dateSetListener,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });


        Button offers = root.findViewById(R.id.offers);
        if (!preferences.getString("mail", "").equals("")){
            offers.setVisibility(View.VISIBLE);
        }


        return root;
    }

    private boolean isNetworkConnected(){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] networkInfo = null;

            if (connectivityManager != null) {
                networkInfo = connectivityManager.getAllNetworkInfo();
            }
            Log.d("InternetConnection","Device is connected to Internet");
            return networkInfo != null & networkInfo[0].isConnected();
        }catch(NullPointerException e){
            Log.e("InternetConnection","Device is not connected to Internet");
            return false;
        }
    }

}