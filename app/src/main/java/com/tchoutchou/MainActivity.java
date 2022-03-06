package com.tchoutchou;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tchoutchou.fragments.Home;
import com.tchoutchou.fragments.user.UserTickets;
import com.tchoutchou.model.Trip;
import com.tchoutchou.util.MainFragmentReplacement;


public class MainActivity extends AppCompatActivity {

    private static boolean appIsOpen = false;
    private Handler handler;
    private boolean doubleBackToExitPressedOnce = false;
    private FragmentManager fm;
    Fragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View loadingScreen = getLayoutInflater().inflate(R.layout.loading_screen, null);
        View mainView = getLayoutInflater().inflate(R.layout.activity_main, null);

        setContentView(loadingScreen);
        handler = new Handler();
        if (!appIsOpen) {
            Runnable change = () -> setContentView(mainView);

            Runnable loading = () -> {
                try {
                    Thread.sleep(5000);
                    Trip.clean();
                    handler.post(change);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            };

            new Thread(loading).start();
            appIsOpen = true;
        }else{
            setContentView(mainView);
        }

        fm = getSupportFragmentManager();
        mainFragment = fm.findFragmentById(R.id.main);

        if (mainFragment instanceof UserTickets){
            getSupportActionBar().show();
            getSupportActionBar().setTitle("Vos billets");
        }else{
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onBackPressed() {
        if(mainFragment instanceof Home) {
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
            MainFragmentReplacement.replace(fm,new Home());
         }
    }


}