package com.tchoutchou.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.tchoutchou.R;
import com.tchoutchou.fragments.user.UserAccount;
import com.tchoutchou.util.MainFragmentReplacement;

public class Offers extends Fragment {

    private SharedPreferences preferences;

    private RadioButton boutonJeunes;
    private RadioButton boutonVieux;

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

        boutonSave.setOnClickListener(v -> doSave());

        return root;
    }

    public void doSave() {

        int age = UserAccount.getAge();

        SharedPreferences.Editor editor = preferences.edit();

        if(age<26){
            if(boutonJeunes.isChecked()) {
                Toast.makeText(requireContext(), getString(R.string.young_card_added), Toast.LENGTH_SHORT).show();
                editor.putString("Carte", getString(R.string.young_card));
            } else {
                Toast.makeText(requireContext(), getString(R.string.young_user), Toast.LENGTH_SHORT).show();
            }
        } else if(age>64) {
            if(boutonVieux.isChecked()) {
                Toast.makeText(requireContext(), getString(R.string.old_card_added), Toast.LENGTH_SHORT).show();
                editor.putString("Carte", getString(R.string.old_card));
            } else {
                Toast.makeText(requireContext(), getString(R.string.old_user), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.discounts_prohibited), Toast.LENGTH_SHORT).show();
        }
        editor.apply();
        MainFragmentReplacement.replace(requireActivity().getSupportFragmentManager(), new UserAccount());
    }
}
