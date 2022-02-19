package com.tchoutchou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Connexion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        EditText login = findViewById(R.id.login);
        EditText mdp = findViewById(R.id.motdepasse);

        Button show = findViewById(R.id.connecter);
        show.setOnClickListener(view -> {
            Context context = getApplicationContext();

            File myfilePath = new File(context.getFilesDir() + "/" + "testAndroid.txt");
            File file = new File("/data/data/com.example.tchoutchou/files/testAndroid.txt");
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }

            
        });
    }
}