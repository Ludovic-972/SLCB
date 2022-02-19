package com.tchoutchou;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tchoutchou.utils.ExternalDB;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class Home extends AppCompatActivity {

    AutoCompleteTextView departureTown, arrivalTown;
    EditText departureTime;
    TextView greetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(!hasActiveInternetConnection()){ // Si la connexion à la base de données a échouée
            /*Renvoyer vers la page hors connexion*/
        }

        departureTown = findViewById(R.id.departureTown);
        arrivalTown = findViewById(R.id.arrivalTown);
        departureTime = findViewById(R.id.departureTime);

        init();

        departureTime.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(Home.this, (timePicker, hourOfDay, minutes) -> {
                departureTime.setText(String.format("%02d:%02d", hourOfDay, minutes));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        Button goToRides = findViewById(R.id.toRides);



        goToRides.setOnClickListener(view -> {

        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500); urlc.connect();
                return (urlc.getResponseCode() == 200); }
            catch (IOException e) {
                Log.e("InternetConnexion", "Error checking internet connection", e);
            }
        } else {
            Log.e("InternetConnexion", "No network available!");
        } return false;
    }



    public void init(){
        SharedPreferences preferences = getSharedPreferences("userInfos", MODE_PRIVATE);
        this.greetings = findViewById(R.id.greetings);
        String username = preferences.getString("Username","");

        String text = greetings.getText().toString();

        greetings.setText(text+" "+username+"\uD83D\uDC4B,");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, ExternalDB.getAllTowns());

        departureTown.setAdapter(adapter);
        arrivalTown.setAdapter(adapter);




    }




}