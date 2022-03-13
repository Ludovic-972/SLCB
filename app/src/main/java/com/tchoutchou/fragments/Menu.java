package com.tchoutchou.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tchoutchou.R;
import com.tchoutchou.fragments.user.UserAccount;
import com.tchoutchou.fragments.user.UserConnection;
import com.tchoutchou.fragments.user.UserTickets;
import com.tchoutchou.util.MainFragmentReplacement;


public class Menu extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        SharedPreferences preferences = this.requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        fragmentManager = requireActivity().getSupportFragmentManager();

        /*Bouton vers la page d'accueil*/
        ImageButton toHomePage = root.findViewById(R.id.homePageButton);

        /*Bouton vers la page des billets*/
        Button toTickets = root.findViewById(R.id.ticketsPageButton);

        /*Bouton vers la page de compte*/
        ImageButton toAccount = root.findViewById(R.id.accountPageButton);


        toHomePage.setOnClickListener(view -> MainFragmentReplacement.replace(fragmentManager,new Home()));

        toTickets.setOnClickListener(view -> MainFragmentReplacement.replace(fragmentManager,new UserTickets()));


        /*Si à sa dernière utilisation de l'application l'utilisateur ne s'est pas connecté
        *   ou s'est déconnecté ce button le renvoie vers à page de connexion/inscription
        *   sinon vers sa page de compte
        */
        toAccount.setOnClickListener(view -> {
            if(preferences.getString("mail", "").equals(""))
                MainFragmentReplacement.replace(fragmentManager,new UserConnection());
            else
                MainFragmentReplacement.replace(fragmentManager,new UserAccount());
        });

        return root;
    }

}