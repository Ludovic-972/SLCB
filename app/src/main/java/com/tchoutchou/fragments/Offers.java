package com.tchoutchou.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.tchoutchou.R;
import com.tchoutchou.fragments.user.UserAccount;

public class Offers extends Fragment {

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
        View root = inflater.inflate(R.layout.fragment_user_registration, container, false);
        SharedPreferences preferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);

        this.boutonSave = root.findViewById(R.id.boutonSave);
        this.boutonJeunes = root.findViewById(R.id.boutonJeunes);
        this.boutonVieux = root.findViewById(R.id.boutonVieux);
        this.radioGroup = root.findViewById(R.id.listeRadioButton);

        boutonSave.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                doSave(root);
            };
        });

        return root;
    }

    public void doSave(View root) {
        int verify = this.radioGroup.getCheckedRadioButtonId();
        TextView cartes = root.findViewById(R.id.carte);
        RadioButton which = (RadioButton) root.findViewById(verify);

        int age = UserAccount.getAge();
        if(age>= 18 && age <=25 && which==boutonJeunes) {
            cartes.setText("Carte jeunes");
        } else if(age>=60 && which==boutonVieux) {
            cartes.setText("Carte sénior");
        }
    }
}
