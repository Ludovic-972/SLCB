package com.tchoutchou.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tchoutchou.MainActivity;
import com.tchoutchou.R;
import com.tchoutchou.fragments.user.UserAccount;
import com.tchoutchou.util.MainFragmentReplacement;

public class Offers extends Fragment {

    SharedPreferences preferences;

    Button boutonSave;
    RadioButton boutonJeunes;
    RadioButton boutonVieux;
    RadioGroup radioGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_offers, container, false);
        preferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        boutonSave = root.findViewById(R.id.boutonSave);
        boutonJeunes = root.findViewById(R.id.boutonJeunes);
        boutonVieux = root.findViewById(R.id.boutonVieux);
        radioGroup = root.findViewById(R.id.listeRadioButton);

        boutonSave.setOnClickListener(v -> doSave(v));

        return root;
    }

    public void doSave(View root) {
        int verify = this.radioGroup.getCheckedRadioButtonId();
        RadioButton which = (RadioButton) root.findViewById(verify);
        int age = UserAccount.getAge();

        SharedPreferences.Editor editor = preferences.edit();

        if(age<26){
            if(boutonJeunes.isChecked()) {
                Toast.makeText(requireContext(), "Ajout carte jeune", Toast.LENGTH_SHORT).show();
                editor.putString("Carte", "Carte jeune");
            } else {
                Toast.makeText(requireContext(), "Vous êtes jeune", Toast.LENGTH_SHORT).show();
            }
        } else if(age>64) {
            if(boutonVieux.isChecked()) {
                Toast.makeText(requireContext(), "Ajout carte vieux", Toast.LENGTH_SHORT).show();
                editor.putString("Carte", "Carte sénior");
            } else {
                Toast.makeText(requireContext(), "Vous êtes vieux", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Vous n'avez pas le droit à des réductions", Toast.LENGTH_SHORT).show();
        }
        editor.apply();
        MainFragmentReplacement.replace(requireActivity().getSupportFragmentManager(), new UserAccount());
    }
}
