package com.tchoutchou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Inscription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);

        EditText nom = findViewById(R.id.nom);
        EditText prenom = findViewById(R.id.prenom);
        EditText mail = findViewById(R.id.mail);
        EditText anniversaire = findViewById(R.id.anniversaire);
        EditText numero = findViewById(R.id.numero);
        EditText mdp = findViewById(R.id.mdp);

        Button register = findViewById(R.id.inscrire);
        register.setOnClickListener(view -> {
            String liste = nom.getText().toString() + ", " + prenom.getText().toString() + ", " + mail.getText().toString() + ", " + anniversaire.getText().toString() + ", " + numero.getText().toString() + ", " + mdp.getText().toString() + "\n";
            FileOutputStream fileOutputStream;

            try {
                fileOutputStream = openFileOutput("testAndroid.txt", Context.MODE_APPEND);
                fileOutputStream.write(liste.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.close();
                nom.setText("");
                prenom.setText("");
                mail.setText("");
                anniversaire.setText("");
                numero.setText("");
                mdp.setText("");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}