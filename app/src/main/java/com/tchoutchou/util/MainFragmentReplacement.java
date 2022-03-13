package com.tchoutchou.util;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tchoutchou.MainActivity;
import com.tchoutchou.R;

public class MainFragmentReplacement {
    private MainFragmentReplacement(){}


    /*Permet de remplacer le fragment principal de l'activité*/
    public static void replace(FragmentManager fragmentManager, Fragment frag){
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.main);
        if(!frag.getClass().toString().equals(currentFragment.getTag())) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, frag);
            fragmentTransaction.commit();
        }
    }
}
