package com.tchoutchou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;


public class Menu extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        SharedPreferences preferences = this.getActivity().getSharedPreferences("userinfos", Context.MODE_PRIVATE);

        ImageView toHomePage = root.findViewById(R.id.homePageButton);
        Button toTickets = root.findViewById(R.id.ticketsPageButton);
        ImageView toAccount = root.findViewById(R.id.accountPageButton);

        toHomePage.setOnClickListener(view -> {
            if(!(requireActivity().getClass()).equals(Home.class))
                goTo(Home.class);
        });

        toTickets.setOnClickListener(view -> {
            if(!(requireActivity().getClass()).equals(UserTickets.class))
                goTo(UserTickets.class);
        });

        toAccount.setOnClickListener(view -> {
            if(/*!(this.getActivity().getClass()).equals(UserAccount.class) ||*/ !(requireActivity().getClass()).equals(Connexion.class))
                if(preferences.getString("Surname","") == "")
                    goTo(Connexion.class);
               /* else
                    goTo(UserAccount.class);*/
        });

        return root;
    }

    public void goTo(Class cl){
        Intent intent = new Intent(this.getActivity(),cl);
        this.startActivity(intent);
        this.requireActivity().finish();
    }
}