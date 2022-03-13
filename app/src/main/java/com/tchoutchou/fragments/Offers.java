package com.tchoutchou.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.tchoutchou.NoConnectionActivity;
import com.tchoutchou.R;
import com.tchoutchou.fragments.user.UserAccount;
import com.tchoutchou.model.User;
import com.tchoutchou.util.MainFragmentReplacement;
import com.tchoutchou.util.NoConnectionException;

//Cette classe permet à l'utilisateur de choisir une carte de réduction fonction de son âge
public class Offers extends Fragment {

    private SharedPreferences preferences;

    private RadioButton boutonJeunes;
    private RadioButton boutonVieux;
    private String cardType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_offers, container, false);
        preferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);

        Button boutonSave = root.findViewById(R.id.boutonSave);
        boutonJeunes = root.findViewById(R.id.boutonJeunes);
        boutonVieux = root.findViewById(R.id.boutonVieux);
        cardType = "";

        boutonSave.setOnClickListener(v -> doSave());

        return root;
    }

    //Méthode qui vérifie quel RadioButton a été coché et si l'âge nécessaire pour choisir une carte est respecté
    public void doSave() {

        int age = UserAccount.getAge();

        SharedPreferences.Editor editor = preferences.edit();

        //Vérification de l'âge
        if(age<26){
            //Vérification du RadioButton coché
            if(boutonJeunes.isChecked()) {
                Toast.makeText(requireContext(), getString(R.string.young_card_added), Toast.LENGTH_SHORT).show();
                cardType = "Young";
                editor.putString("Carte", getString(R.string.young_card));
            } else {
                Toast.makeText(requireContext(), getString(R.string.young_user), Toast.LENGTH_SHORT).show();
            }
        } else if(age>64) {
            if(boutonVieux.isChecked()) {
                Toast.makeText(requireContext(), getString(R.string.old_card_added), Toast.LENGTH_SHORT).show();
                cardType = "Senior";
                editor.putString("Carte", getString(R.string.old_card));
            } else {
                Toast.makeText(requireContext(), getString(R.string.old_user), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.discounts_prohibited), Toast.LENGTH_SHORT).show();
        }
        editor.apply();

        //Si la personne n'a rien coché
        if (!cardType.equals("")) {
            Thread cardAddition = new Thread() {
                @Override
                public void run() {
                    try {
                        User.addCardType(preferences.getInt("userId", 0), cardType);
                    } catch (NoConnectionException e) {
                        Intent intent = new Intent(requireActivity(), NoConnectionActivity.class);
                        startActivity(intent);
                    }
                }
            };
            try {
                cardAddition.join();
                MainFragmentReplacement.replace(requireActivity().getSupportFragmentManager(), new UserAccount());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
