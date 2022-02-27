package com.tchoutchou;



import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tchoutchou.fragments.Home;
import com.tchoutchou.util.FragmentReplacement;

public class MainActivity extends AppCompatActivity {

    private static Context context;

    Handler handler;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View loadingScreen = getLayoutInflater().inflate(R.layout.loading_screen, null);
        View main = getLayoutInflater().inflate(R.layout.activity_main, null);

        setContentView(loadingScreen);
        context = getApplicationContext();
        handler = new Handler();

        Runnable change = () -> setContentView(main);

        Runnable loading = () -> {
            try {
                Thread.sleep(3000);
                handler.post(change);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };

        new Thread( loading ).start();
    }

    public static Context getAppContext() { return context; }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment main = fm.findFragmentById(R.id.main);

        if(main instanceof Home) {
            if (doubleBackToExitPressedOnce) {
                finish();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Appuyez encore une fois sur retour pour sortir", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }else{
            FragmentReplacement.Replace(fm,new Home());
         }
    }
}