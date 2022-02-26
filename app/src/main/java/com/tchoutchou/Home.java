package com.tchoutchou;

import android.annotation.SuppressLint;
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

import com.tchoutchou.util.Towns;

import java.util.Calendar;


public class Home extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Home() {}


    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    AutoCompleteTextView departureTown, arrivalTown;
    EditText departureHour;
    TextView greetings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        departureTown = root.findViewById(R.id.departureTown);
        arrivalTown = root.findViewById(R.id.arrivalTown);
        departureHour = root.findViewById(R.id.departureHour);
        Button goToRides = root.findViewById(R.id.toRides);

        init(root);

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


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Towns.getTowns());

        departureTown.setAdapter(adapter);
        arrivalTown.setAdapter(adapter);

        goToRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notEmptyInputs()) {
                    Intent intent = new Intent(requireActivity(), Rides.class);
                    intent.putExtra("departure", departureTown.getText().toString());
                    intent.putExtra("arrival", arrivalTown.getText().toString());
                    intent.putExtra("departureHour", departureHour.getText().toString());
                    startActivity(intent);
                }else{
                    Toast.makeText(requireContext(), "Veuillez remplir tous les champs",
                            Toast.LENGTH_SHORT).show();
                }
            }

            private boolean notEmptyInputs() {
                return !departureTown.getText().toString().equals("")
                        && !arrivalTown.getText().toString().equals("")
                        && !departureHour.getText().toString().equals("");
            }
        });

        /* Récupérer la position de la personne*/
        return root;
    }


    @SuppressLint("SetTextI18n")
    public void init(View root){
        SharedPreferences preferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        this.greetings = root.findViewById(R.id.greetings);
        String username = preferences.getString("Surname","");

        String text = greetings.getText().toString();

        greetings.setText(text+" "+username+"\uD83D\uDC4B,");

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