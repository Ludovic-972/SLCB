package com.tchoutchou;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tchoutchou.fragments.Home;
import com.tchoutchou.util.MainFragmentReplacement;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private FragmentManager fm;
    private Fragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        fm = getSupportFragmentManager();
        mainFragment = fm.findFragmentById(R.id.main);

    }

    @Override
    public void onBackPressed() {
        if(mainFragment instanceof Home) {
            if (doubleBackToExitPressedOnce) {
                finish();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.press_again), Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
        }else{
            MainFragmentReplacement.replace(fm,new Home());
         }
    }


}