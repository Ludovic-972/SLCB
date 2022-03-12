package com.tchoutchou.fragments.user;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tchoutchou.NoConnectionActivity;
import com.tchoutchou.R;
import com.tchoutchou.fragments.Home;
import com.tchoutchou.model.User;
import com.tchoutchou.util.MainFragmentReplacement;
import com.tchoutchou.util.NoConnectionException;

import java.util.Calendar;
import java.util.Locale;


public class UserRegistration extends Fragment {

    private User user;

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
        register.setOnClickListener(view -> {
            if (!verifyInputs()){
                Toast.makeText(requireContext(), getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
            }else {
                if (validMail()) {
                    Thread registration = new Thread() {

                        @Override
                        public void run() {
                            try {
                                user = User.addUser(
                                        nom.getText().toString(), prenom.getText().toString(),
                                        mail.getText().toString().toLowerCase(Locale.ROOT), anniversaire.getText().toString(),
                                        numero.getText().toString(), mdp.getText().toString());
                            } catch (NoConnectionException e) {
                                Intent intent = new Intent(requireActivity(), NoConnectionActivity.class);
                                startActivity(intent);
                            }


                        }
                    };

                    registration.start();

                    try {
                        registration.join();
                        if (user != null) {
                            Toast.makeText(requireContext(), "Bienvenue chez nous !", Toast.LENGTH_LONG).show();
                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putInt("userId", user.getId());
                            editor.putString("lastname", user.getLastname());
                            editor.putString("firstname", user.getFirstname());
                            editor.putString("mail", user.getMail());
                            editor.putString("birthdate", user.getBirthdate());
                            editor.putString("phoneNumber", user.getPhoneNumber());
                            editor.putString("password", user.getPassword());

                            editor.apply();
                            nom.setText("");
                            prenom.setText("");
                            mail.setText("");
                            anniversaire.setText("");
                            numero.setText("");
                            mdp.setText("");
                            MainFragmentReplacement.replace(requireActivity().getSupportFragmentManager(), new Home());
                        } else
                            Toast.makeText(requireContext(), getString(R.string.email_already_used), Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(requireContext(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                }
            }
        });

        anniversaire.setOnClickListener(view -> {

            Calendar c = Calendar.getInstance();

            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
                String date = "";
                date+= (dayOfMonth<10) ? "0"+dayOfMonth+"-" : dayOfMonth+"-";
                date+= ((monthOfYear+1)<10) ? "0"+(monthOfYear+1)+"-" : (monthOfYear+1)+"-";
                date+= year;
                anniversaire.setText(date);
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar
                    ,dateSetListener,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        return root;
    }


    private boolean verifyInputs() {
        return !nom.getText().toString().equals("") && !prenom.getText().toString().equals("")
                && !mail.getText().toString().equals("") && !anniversaire.getText().toString().equals("")
                && !numero.getText().toString().equals("") && !mdp.getText().toString().equals("");
    }

    private boolean validMail(){
        return mail.getText().toString().toLowerCase(Locale.ROOT).matches("^[a-z0-9.-]+@[a-z.-]+\\.[a-z]{2,}");
    }

}