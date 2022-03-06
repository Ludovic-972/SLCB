package com.tchoutchou.fragments.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tchoutchou.fragments.Home;
import com.tchoutchou.R;
import com.tchoutchou.model.User;
import com.tchoutchou.util.InexistantUserException;
import com.tchoutchou.util.MainFragmentReplacement;

import java.util.Locale;


public class UserConnection extends Fragment {


    private User user;
    private boolean registered;
    EditText mail,mdp;

    public UserConnection() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_connection, container, false);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        mail = root.findViewById(R.id.mail);
        mdp = root.findViewById(R.id.password);

        Button login = root.findViewById(R.id.login);
        login.setOnClickListener(view -> {
            String loginText = mail.getText().toString();
            String passwordText = mdp.getText().toString();

            if (!loginText.equals("") && !passwordText.equals("")) {
                if(validMail()) {
                    registered = false;
                    Thread verify = new Thread() {
                        @Override
                        public void run() {
                            try {
                                user = User.getInformationsFromDB(loginText, passwordText);
                                registered = true;
                            } catch (InexistantUserException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    verify.start();

                    try {
                        verify.join();
                        if (!registered) {
                            Toast.makeText(requireContext(), "Login ou mot de passe incorrecte", Toast.LENGTH_SHORT).show();
                            mdp.setText("");
                        }else {
                            editor.putInt("userId", user.getId());
                            editor.putString("lastname", user.getLastname());
                            editor.putString("firstname", user.getFirstname());
                            editor.putString("mail", user.getMail());
                            editor.putString("birthdate", user.getBirthdate());
                            editor.putString("phoneNumber", user.getPhoneNumber());
                            editor.putString("password", user.getPassword());

                            editor.apply();
                            Toast.makeText(requireContext(), "Content de vous revoir " + user.getFirstname() + " !", Toast.LENGTH_SHORT).show();
                            MainFragmentReplacement.replace(fragmentManager, new Home());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(requireContext(), "Email invalide", Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();


        });

        Button register = root.findViewById(R.id.register);
        register.setOnClickListener(view -> MainFragmentReplacement.replace(fragmentManager, new UserRegistration()));
        return root;
    }

    private boolean validMail(){
        return mail.getText().toString().toLowerCase(Locale.ROOT).matches("^[a-z0-9.-]+@[a-z.-]+\\.[a-z]{2,}");
    }
}