package com.tchoutchou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Connexion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        EditText login = findViewById(R.id.login);
        EditText mdp = findViewById(R.id.motdepasse);

        Button show = findViewById(R.id.connecter);
        show.setOnClickListener(view -> {
            UserBD db = new UserBD(this);
            List<User> userList = db.getAllUsers();

            for(int i = 0; i<userList.size(); i++) {
                if(login.getText().toString().equals(userList.get(i).getMail()) &&
                mdp.getText().toString().equals(userList.get(i).getPassword())) {
                    Intent intent = new Intent();
                } else {
                    Toast.makeText(getApplicationContext(), "Login ou mot de passe incorrecte", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}