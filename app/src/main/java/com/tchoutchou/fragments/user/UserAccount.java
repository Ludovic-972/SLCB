package com.tchoutchou.fragments.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.tchoutchou.R;

import java.time.LocalDate;
import java.time.Month;

public class UserAccount extends Fragment {

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


        TextView infos = root.findViewById(R.id.informations);
        String userName = preferences.getString("lastname", "");
        String userFirstname = preferences.getString("firstname", "");
        String dateNaissance = root.findViewById(R.id.anniversaire).toString();
        String[] detailDate = dateNaissance.split("-");
        LocalDate dateOfBirth = LocalDate.of(Integer.parseInt(detailDate[2]), Month.valueOf(detailDate[1]), Integer.parseInt(detailDate[0]));
        LocalDate now = LocalDate.now();
        int age = dateOfBirth.until(now).getYears();
        infos.setText(userFirstname + " " + userName + ", " + age + " ans");

        TextView adresseMail = root.findViewById(R.id.adresseMail);
        String userMail = preferences.getString("mail", "");
        adresseMail.setText("Adresse mail : " + userMail);

        TextView tel = root.findViewById(R.id.phoneNumber);
        String userPhone = preferences.getString("phoneNumber", "");
        tel.setText("Numero de téléphone : " + userPhone);

        return root;
    }

}
