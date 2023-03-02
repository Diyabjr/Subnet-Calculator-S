package com.diab.subnetcalculator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.diab.subnetcalculator.fragments.MaskeFragment;
import com.diab.subnetcalculator.fragments.SubnetFragment;
import com.diab.subnetcalculator.fragments.VLSMFragment;


public class AbschnittePager extends FragmentPagerAdapter {

    AbschnittePager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:{
                return new SubnetFragment();
            }
            case 1:{
                return new VLSMFragment();
            }
            case 2:{
                return new MaskeFragment();
            }
            default:{
                return null;
            }
        }
    }


    @Override
    public int getCount() {
        // Zeige 3 Seiten an
        return 3;
    }
}