package com.tchoutchou.fragments.user;

import android.content.Context;
import android.content.Intent;
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

import com.tchoutchou.NoConnectionActivity;
import com.tchoutchou.fragments.Home;
import com.tchoutchou.R;
import com.tchoutchou.model.User;
import com.tchoutchou.util.InexistantUserException;
import com.tchoutchou.util.MainFragmentReplacement;
import com.tchoutchou.util.NoConnectionException;

import java.util.Locale;

//Cette classe a pour but de faire en sorte qu'un utilisateur puisse se connecter à son compte
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

            //On vérifie si les champs sont vides ou non
            if (!loginText.equals("") && !passwordText.equals("")) {
                if(validMail()) {
                    registered = false;
                    //Vérification de la présence du compte dans la base de données
                    Thread verify = new Thread() {
                        @Override
                        public void run() {
                            try {
                                user = User.getInformationsFromDB(loginText, passwordText);
                                registered = true;
                            } catch (NoConnectionException e) {
                                Intent intent = new Intent(requireActivity(), NoConnectionActivity.class);
                                startActivity(intent);
                            }catch (InexistantUserException ignored){}
                        }
                    };
                    verify.start();

                    try {
                        verify.join();
                        if (!registered) {
                            Toast.makeText(requireContext(), requireActivity().getString(R.string.incorrect_login_or_pwd), Toast.LENGTH_SHORT).show();
                            mdp.setText("");
                        }else {

                            String cardType = setUserCardType(user.getCardType());
                            editor.putInt("userId", user.getId());
                            editor.putString("lastname", user.getLastname());
                            editor.putString("firstname", user.getFirstname());
                            editor.putString("mail", user.getMail());
                            editor.putString("birthdate", user.getBirthdate());
                            editor.putString("phoneNumber", user.getPhoneNumber());
                            editor.putString("password", user.getPassword());
                            editor.putString("Carte", cardType);
                            editor.apply();
                            Toast.makeText(requireContext(), getString(R.string.nice_to_see_you_again)+" "+ user.getFirstname() + " !", Toast.LENGTH_SHORT).show();
                            MainFragmentReplacement.replace(fragmentManager, new Home());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(requireContext(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(requireContext(), getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();


        });

        Button register = root.findViewById(R.id.register);
        register.setOnClickListener(view -> MainFragmentReplacement.replace(fragmentManager, new UserRegistration()));
        return root;
    }

    //Permet de définir la carte pour la page de compte en récupérant ce qui a été inséré dans la page des cartes de réduction
    private String setUserCardType(String cardType) {
        switch (cardType){
            case "Young": return getString(R.string.young_card);
            case "Senior": return getString(R.string.old_card);
            default: return "";
        }
    }

    //Obligation de faire respecter un format de mail pour vérifier sa validité
    private boolean validMail(){
        return mail.getText().toString().toLowerCase(Locale.ROOT).matches("^[a-z0-9.-]+@[a-z.-]+\\.[a-z]{2,}");
    }
}