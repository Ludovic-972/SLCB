package com.tchoutchou;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tchoutchou.util.Towns;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Home extends AppCompatActivity {

    AutoCompleteTextView departureTown, arrivalTown;
    EditText departureTime;
    TextView greetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        departureTown = findViewById(R.id.departureTown);
        arrivalTown = findViewById(R.id.arrivalTown);
        departureTime = findViewById(R.id.departureTime);
        Button goToRides = findViewById(R.id.toRides);

        init();

        departureTime.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    Home.this,
                    (timePicker, hourOfDay, minutes) -> departureTime.setText(String.format("%02d:%02d", hourOfDay, minutes)),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                Towns.getTowns());

        departureTown.setAdapter(adapter);
        arrivalTown.setAdapter(adapter);

        goToRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,Rides.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void init(){
        SharedPreferences preferences = getSharedPreferences("userInfos", MODE_PRIVATE);
        this.greetings = findViewById(R.id.greetings);
        String username = preferences.getString("Surname","");

        String text = greetings.getText().toString();

        greetings.setText(text+" "+username+"\uD83D\uDC4B,");

    }

    private boolean isNetworkConnected(){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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