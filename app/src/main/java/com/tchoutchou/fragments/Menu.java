package com.tchoutchou.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tchoutchou.R;
import com.tchoutchou.fragments.user.UserConnection;


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
                goTo(new Home());
        });

        toTickets.setOnClickListener(view -> {
                goTo(new UserTickets());
        });

        toAccount.setOnClickListener(view -> {
            if(preferences.getString("Surname","") == "")
                goTo(new UserConnection());
           /* else
                goTo(new UserAccount());*/
        });

        return root;
    }

    public void goTo(Fragment fragment){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.main);
        if(!fragment.getClass().toString().equals(currentFragment.getTag())) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, fragment);
            fragmentTransaction.commit();
        }
    }
}