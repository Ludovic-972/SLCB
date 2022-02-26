package com.tchoutchou.fragments.user;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.tchoutchou.R;
import com.tchoutchou.database.UserBD;
import com.tchoutchou.fragments.Home;
import com.tchoutchou.model.User;

import java.util.Calendar;


public class UserRegistration extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText nom, prenom,mail,anniversaire,numero,mdp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_registration, container, false);

        nom = root.findViewById(R.id.nom);
        prenom = root.findViewById(R.id.prenom);
        mail = root.findViewById(R.id.mail);
        anniversaire = root.findViewById(R.id.anniversaire);
        numero = root.findViewById(R.id.numero);
        mdp = root.findViewById(R.id.mdp);

        Button register = root.findViewById(R.id.inscrire);
        register.setOnClickListener(view -> addUserInDB());

        anniversaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String date = "";
                        date+= (dayOfMonth<10) ? "0"+dayOfMonth+"-" : dayOfMonth+"-";

                        date+= (monthOfYear<10) ? "0"+monthOfYear+"-" : monthOfYear+"-";

                        date+= year;

                        anniversaire.setText(date);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                        ,dateSetListener,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        return root;
    }

    public void addUserInDB(){
        User user = new User();
        user.setLastname(nom.getText().toString());
        user.setFirstname(prenom.getText().toString());
        user.setMail(mail.getText().toString());
        user.setBirthdate(anniversaire.getText().toString());
        user.setPhoneNumber(numero.getText().toString());
        user.setPassword(mdp.getText().toString());

        UserBD db = new UserBD(getContext());
        db.addUser(user);
        db.close();

        saveUser();
    }

    public void saveUser() {
        if (nom.getText().toString().matches("") || prenom.getText().toString().matches("")
                || mail.getText().toString().matches("") || anniversaire.getText().toString().matches("")
                || numero.getText().toString().matches("") || mdp.getText().toString().matches("")){
            Toast.makeText(getContext(), "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
        }else{
            SharedPreferences sharedPreferences= requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("lastname", nom.getText().toString());
            editor.putString("firstname",prenom.getText().toString());
            editor.putString("mail",mail.getText().toString());
            editor.putString("birthdate",anniversaire.getText().toString());
            editor.putString("phoneNumber",numero.getText().toString());
            editor.putString("password",mdp.getText().toString());

            editor.apply();

            Toast.makeText(getContext(),"Bienvenue chez nous !",Toast.LENGTH_LONG).show();
            nom.setText("");
            prenom.setText("");
            mail.setText("");
            anniversaire.setText("");
            numero.setText("");
            mdp.setText("");
            goTo(new Home());
        }
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