package com.tchoutchou.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tchoutchou.R;
import com.tchoutchou.TripActivity;
import com.tchoutchou.model.Towns;
import com.tchoutchou.util.MainFragmentReplacement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class Home extends Fragment implements LocationListener {

    public Home() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private SharedPreferences preferences;
    private List<String> towns = new ArrayList<>();
    private LocationManager locationManager;
    private EditText tripDay,departureHour;
    private AutoCompleteTextView departureTown,arrivalTown;
    private Button goToRides;
    private ImageButton userLocation;
    private double latitude;
    private double longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        preferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);

        tripDay = root.findViewById(R.id.tripDate);
        departureHour = root.findViewById(R.id.departureHour);
        departureTown = root.findViewById(R.id.departureTown);
        userLocation = root.findViewById(R.id.userLocation);
        arrivalTown = root.findViewById(R.id.arrivalTown);
        goToRides = root.findViewById(R.id.toRides);

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

        tripDay.setOnClickListener(view -> {

            Calendar c = Calendar.getInstance();

            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
                String date = "";
                date+= (dayOfMonth<10) ? "0"+dayOfMonth+"-" : dayOfMonth+"-";
                date+= (monthOfYear<10) ? "0"+(monthOfYear+1)+"-" : (monthOfYear+1)+"-";
                date+= year;
                tripDay.setText(date);
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                    ,dateSetListener,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

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

        userLocation.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                requireActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1000);
            }else{
                try {
                    locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    showLocation();
                }catch(SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
        

        goToRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noEmptyInputs()) {
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

            private boolean noEmptyInputs() {
                return !departureTown.getText().toString().equals("")
                        && !arrivalTown.getText().toString().equals("")
                        && !departureHour.getText().toString().equals("")
                        && !tripDay.getText().toString().equals("");
            }
        });



        Button offers = root.findViewById(R.id.offers);
        if (preferences.getInt("userId", 0) != 0){
            offers.setVisibility(View.VISIBLE);
            offers.setOnClickListener(view -> MainFragmentReplacement.replace(
                    requireActivity().getSupportFragmentManager(),
                    new Offers()
            ));
        }
        return root;
    }

    private void showLocation(){
        String cityName = "";
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        List<Address> adresses;

        try{
            adresses = geocoder.getFromLocation(latitude,longitude,10);

            if (adresses.size() > 0){
                for (Address adr : adresses){
                    if (adr.getLocality() != null && adr.getLocality().length() >0){
                        cityName = adr.getLocality();
                        break;
                    }
                }
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            String finalCityName = cityName;
            builder.setTitle("Localisation")
                    .setCancelable(false)
                    .setMessage(getString(R.string.show_location_1)+" "+cityName+"."+getString(R.string.show_location_2))
                    .setNegativeButton("Non",(dialog,i) -> dialog.dismiss())
                    .setPositiveButton("Oui", ((dialog, i) -> {
                        departureTown.setText(finalCityName);
                        dialog.dismiss();
                    }));
            AlertDialog alert = builder.create();
            alert.show();
        }catch (IOException e){
            e.printStackTrace();
        }


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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}