package com.tchoutchou.util;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tchoutchou.MainActivity;
import com.tchoutchou.R;

public class FragmentReplacement {
    private FragmentReplacement(){}

    public static void Replace(FragmentManager fragmentManager,Fragment frag){
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.main);
        if(!frag.getClass().toString().equals(currentFragment.getTag())) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main, frag);
            fragmentTransaction.commit();
        }
    }
}
