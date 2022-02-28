package com.tchoutchou.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tchoutchou.R;
import com.tchoutchou.fragments.user.*;
import com.tchoutchou.util.FragmentReplacement;


public class Menu extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    FragmentManager fragmentManager;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        SharedPreferences preferences = this.getActivity().getSharedPreferences("userinfos", Context.MODE_PRIVATE);
        fragmentManager = requireActivity().getSupportFragmentManager();

        ImageButton toHomePage = root.findViewById(R.id.homePageButton);
        Button toTickets = root.findViewById(R.id.ticketsPageButton);
        ImageButton toAccount = root.findViewById(R.id.accountPageButton);


        toHomePage.setOnClickListener(view -> FragmentReplacement.Replace(fragmentManager,new Home()));

        toTickets.setOnClickListener(view -> FragmentReplacement.Replace(fragmentManager,new UserTickets()));

        toAccount.setOnClickListener(view -> {
            if(preferences.getString("Surname", "").equals(""))
                FragmentReplacement.Replace(fragmentManager,new UserConnection());
            else
                FragmentReplacement.Replace(fragmentManager,new UserAccount());
        });

        return root;
    }

}