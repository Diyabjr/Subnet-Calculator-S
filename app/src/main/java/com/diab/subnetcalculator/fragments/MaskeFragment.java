package com.diab.subnetcalculator.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.diab.subnetcalculator.R;
import com.diab.subnetcalculator.model.NetworkManager;

import java.util.ArrayList;
import java.util.List;

public class MaskeFragment extends Fragment {
    private Spinner dMaskeSpinner;
    private SeekBar dmaskeseekbar;
    private TextView resMaskeTextView;
    private TextView dRsWildcardMaskeTextView;
    private TextView dResMaskenCIDRTextView;
    private TextView dResNetGrösseTextView;
    private TextView dResMaskeBinTextView;
    private TextView dResWildcardMaskeBinTextView;



    private void ergebnisAktualisierenView(int mask){
        try {
            String netSize = String.valueOf(NetworkManager.findUsableHosts(mask));
            if(mask > 30)
                netSize = "NONE";

            resMaskeTextView.setText(NetworkManager.toDecMask(mask));
            dRsWildcardMaskeTextView.setText(NetworkManager.toDecWildCardMask(mask));
            dResMaskenCIDRTextView.setText(getString(R.string.slash_mask, mask));
            dResNetGrösseTextView.setText(netSize);
            dResMaskeBinTextView.setText(NetworkManager.toBinOctets(NetworkManager.toIntMask(mask)));
            dResWildcardMaskeBinTextView.setText(NetworkManager.toBinOctets(NetworkManager.toIntWildcardMask(mask)));

        }catch (Exception e){
            Log.e("Exception", Log.getStackTraceString(e));
        }

    }

    private void initViews(View rootView){
        dMaskeSpinner = rootView.findViewById(R.id.maskSpinner);
        dmaskeseekbar = rootView.findViewById(R.id.maskSeekBar);

        resMaskeTextView = rootView.findViewById(R.id.resMaskTextView);
        dRsWildcardMaskeTextView = rootView.findViewById(R.id.resWildcardMaskTextView);
        dResMaskenCIDRTextView = rootView.findViewById(R.id.resMaskCIDRTextView);
        dResNetGrösseTextView = rootView.findViewById(R.id.resNetSizeTextView);
        dResMaskeBinTextView = rootView.findViewById(R.id.resMaskBinTextView);
        dResWildcardMaskeBinTextView = rootView.findViewById(R.id.resWildcardMaskBinTextView);


        List<String> mMaskList = new ArrayList<>();
        for (int mask =0; mask<33; mask++){
            mMaskList.add(NetworkManager.toDecMask(mask));
        }
        Context  context= getContext();
        if (context != null){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, mMaskList);
            dMaskeSpinner.setAdapter(adapter);
            ViewsListener();
            dmaskeseekbar.setProgress(24);
        }

    }

    private void ViewsListener(){
        dMaskeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dmaskeseekbar.setProgress(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dmaskeseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dMaskeSpinner.setSelection(progress);
                ergebnisAktualisierenView(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater  inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maske, container, false);
        initViews(rootView);
        return rootView;
    }
}
