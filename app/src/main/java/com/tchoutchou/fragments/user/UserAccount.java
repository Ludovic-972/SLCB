package com.tchoutchou.fragments.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tchoutchou.R;
import com.tchoutchou.fragments.Home;
import com.tchoutchou.util.MainFragmentReplacement;

import java.time.LocalDate;

//Cette classe a pour but de faire en sorte qu'un utilisateur puisse consulter son compte
public class UserAccount extends Fragment {

    private static int age;

    public UserAccount(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_account, container, false);
        SharedPreferences preferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        //Récupération du prénom, nom de famille et âge de la personne
        TextView infos = root.findViewById(R.id.informations);
        String userName = preferences.getString("lastname", "");
        String userFirstname = preferences.getString("firstname", "");
        String dateNaissance = preferences.getString("birthdate", "");

        //Récupération de la date de naissance pour transformation de celle-ci en âge
        String[] date = dateNaissance.split("-");
        LocalDate dateOfBirth = LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
        LocalDate now = LocalDate.now();
        age = dateOfBirth.until(now).getYears();

        //Affichage des informations récupérées
        infos.setText(userFirstname + " " + userName + ", " + age + " " + getString(R.string.years_old));

        //Récupération de l'adresse mail puis affichage
        TextView adresseMail = root.findViewById(R.id.adresseMail);
        String userMail = preferences.getString("mail", "");
        adresseMail.setText(getString(R.string.mail)+"\n" + userMail);

        //Récupération du numéro de téléphone puis affichage
        TextView tel = root.findViewById(R.id.phoneNumber);
        String userPhone = preferences.getString("phoneNumber", "");
        tel.setText(getString(R.string.phone_number)+" :\n" + userPhone+"\n\n");

        TextView cartes = root.findViewById(R.id.carte);

        //Bouton de déconnexion
        Button deconnection = root.findViewById(R.id.deconnection);
        deconnection.setOnClickListener(view -> {
            preferences.edit().clear().apply();
            MainFragmentReplacement.replace(fragmentManager,new Home());
        });

        //Récupération de la carte choisie par l'utilisateur pour l'afficher s'il en a une
        String typeCarte = preferences.getString("Carte", "");

        if(!typeCarte.equals("")) {
            cartes.setText(typeCarte);
        }else{
            cartes.setText(R.string.none);
        }

        return root;
    }

    //Méthode pour récupérer l'âge de l'utilisateur pour les autres classes
    static public int getAge() {return age;}

}
