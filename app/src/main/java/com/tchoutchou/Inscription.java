package com.tchoutchou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Inscription extends AppCompatActivity {

    EditText nom = findViewById(R.id.nom);
    EditText prenom = findViewById(R.id.prenom);
    EditText mail = findViewById(R.id.mail);
    EditText anniversaire = findViewById(R.id.anniversaire);
    EditText numero = findViewById(R.id.numero);
    EditText mdp = findViewById(R.id.mdp);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);

        Button register = findViewById(R.id.inscrire);
        register.setOnClickListener(view -> {
            addUserInDB();
        });
    }

    public void addUserInDB(){
        User user = new User();
        user.setLastname(nom.getText().toString());
        user.setFirstname(prenom.getText().toString());
        user.setMail(mail.getText().toString());
        user.setBirthday(anniversaire.getText().toString());
        user.setPhone(numero.getText().toString());
        user.setPassword(mdp.getText().toString());

        UserBD db = new UserBD(this);
        db.addUser(user);
        db.close();
    }

    public void saveUser() {
        if (nom.getText().toString().matches("") || prenom.getText().toString().matches("")
                || mail.getText().toString().matches("") || anniversaire.getText().toString().matches("")
                || numero.getText().toString().matches("") || mdp.getText().toString().matches("")){
            Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
        }else{
            SharedPreferences sharedPreferences= this.getSharedPreferences("userInfos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("nom", nom.getText().toString());
            editor.putString("prenom",prenom.getText().toString());
            editor.putString("mail",mail.getText().toString());
            editor.putString("anniversaire",anniversaire.getText().toString());
            editor.putString("numero",numero.getText().toString());
            editor.putString("mdp",mdp.getText().toString());

            editor.apply();

            Toast.makeText(this,"Infos sauvegardées !",Toast.LENGTH_LONG).show();
            nom.setText("");
            prenom.setText("");
            mail.setText("");
            anniversaire.setText("");
            numero.setText("");
            mdp.setText("");
        }
    }
}