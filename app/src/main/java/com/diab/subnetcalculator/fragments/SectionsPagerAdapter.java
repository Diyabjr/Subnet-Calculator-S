package com.diab.subnetcalculator.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.diab.subnetcalculator.fragments.SubnetFragment;
import com.diab.subnetcalculator.fragments.VLSMFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    SectionsPagerAdapter(FragmentManager fm) {
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
        // Show 3 total pages.
        return 3;
    }
}