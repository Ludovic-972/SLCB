package com.tchoutchou.fragments.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tchoutchou.fragments.Home;
import com.tchoutchou.R;
import com.tchoutchou.database.UserBD;
import com.tchoutchou.model.User;

import java.util.List;


public class UserConnection extends Fragment {

    public UserConnection() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_connection, container, false);
        EditText mail =root.findViewById(R.id.mail);
        EditText mdp = root.findViewById(R.id.password);

        Button login = root.findViewById(R.id.login);
        login.setOnClickListener(view -> {
            String loginText = mail.getText().toString();
            String passwordText = mdp.getText().toString();

            if (!loginText.equals("") && !passwordText.equals("")) {
                UserBD db = new UserBD(requireContext());
                List<User> users= db.getAllUsers();

                if (!User.isRegistered(loginText,passwordText)){
                    Toast.makeText(requireContext(), "Login ou mot de passe incorrecte", Toast.LENGTH_SHORT).show();
                }else{
                    User user = User.getInformations(loginText,passwordText);
                    saveUser(user);
                    goTo(new Home());
                }
            }
        });

        Button register = root.findViewById(R.id.register);
        register.setOnClickListener(view -> goTo(new UserRegistration()));
        return root;
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

    public void saveUser(User user){
        SharedPreferences sharedPreferences= requireActivity().getSharedPreferences("userInfos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("lastname", user.getLastname());
        editor.putString("firstname",user.getFirstname());
        editor.putString("mail",user.getMail());
        editor.putString("birthdate",user.getBirthdate());
        editor.putString("phoneNumber",user.getPhoneNumber());
        editor.putString("password",user.getPassword());

        editor.apply();
        Toast.makeText(requireContext(), "Content de vous revoir !", Toast.LENGTH_SHORT).show();
    }
}