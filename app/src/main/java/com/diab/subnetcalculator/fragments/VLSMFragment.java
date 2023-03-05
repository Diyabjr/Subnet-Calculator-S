package com.diab.subnetcalculator.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diab.subnetcalculator.MainActivity;
import com.diab.subnetcalculator.R;
import com.diab.subnetcalculator.model.NetzwerkManager;
import com.diab.subnetcalculator.fragments.dialog.NetConfigDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VLSMFragment extends Fragment implements NetConfigDialog.NetConfigDialogListener {
    private EditText[] dIpAddresseEditText;
    private EditText dIpMaskeEditText;
    private SeekBar dIpMaskeSeekBar;

    private ListView dLösungListView;

    private HashMap<String,Integer> dSubNetsMappe;
    private List<NetzwerkManager.Subnet> dLösungSubNets;
    private String dMajorNetwork;


    private void initviews(View rootView){
        dIpAddresseEditText = new EditText[4];
        dIpAddresseEditText[0] = rootView.findViewById(R.id.ipPartAEditText);
        dIpAddresseEditText[1] = rootView.findViewById(R.id.ipPartBEditText);
        dIpAddresseEditText[2] = rootView.findViewById(R.id.ipPartCEditText);
        dIpAddresseEditText[3] = rootView.findViewById(R.id.ipPartDEditText);
        dIpMaskeEditText = rootView.findViewById(R.id.ipMaskEditText);
        dIpMaskeSeekBar = rootView.findViewById(R.id.ipMaskSeekBar);


        dSubNetsMappe = new HashMap<>();
        dLösungSubNets = new ArrayList<>();

        dLösungListView = rootView.findViewById(R.id.resultListView);

        löscheAlleViews();

        setListenerAnEditierbareViews(rootView);

    }
    private void setListenerAnEditierbareViews(View rootView){

        for (int i = 0; i< dIpAddresseEditText.length; i++) {
            final int finalI = i;
            dIpAddresseEditText[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() >=3){
                        bewegeCursorZuNext(finalI);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if(s.length() >0){
                            if (s.length()>0 &&Integer.parseInt(s.toString()) < 0 || Integer.parseInt(s.toString()) > 255){
                                dIpAddresseEditText[finalI].setTextColor(Color.RED);
                            }
                            else{
                                dIpAddresseEditText[finalI].setTextColor(Color.BLACK);
                                //OK
                            }
                        }
                    }catch (NumberFormatException e){
                        Log.e("Exception",Log.getStackTraceString(e));

                    }catch (Exception e){
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                    }

                }
            });

            dIpAddresseEditText[i].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        bewegeCursorZuNext(finalI);
                    }
                    return false;
                }
            });
        }

        dIpMaskeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    try {
                        if (Integer.parseInt(s.toString()) < 1 || Integer.parseInt(s.toString()) >30){
                            dIpMaskeEditText.setTextColor(Color.RED);
                        }else {
                            dIpMaskeEditText.setTextColor(Color.BLACK);
                            dIpMaskeSeekBar.setProgress(Integer.parseInt(dIpMaskeEditText.getText().toString()));
                            //OK
                        }
                        dIpMaskeEditText.setSelection(dIpMaskeEditText.getText().length());
                    }catch (NumberFormatException e){
                        Log.e("Exception",Log.getStackTraceString(e));


                    }catch (Exception e){
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });

        dIpMaskeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dIpMaskeEditText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ImageButton clearImageButton = rootView.findViewById(R.id.clearImageButton);
        clearImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll(v);
            }
        });

        Button netConfigButton = rootView.findViewById(R.id.netConfigButton);
        netConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dMajorNetwork = dIpAddresseEditText[0].getText().toString()+"."
                        + dIpAddresseEditText[1].getText().toString()+"."
                        + dIpAddresseEditText[2].getText().toString()+"."
                        + dIpAddresseEditText[3].getText().toString()+"/"
                        + dIpMaskeEditText.getText().toString();
                if(NetzwerkManager.checkIfValidNetworkAddress(dMajorNetwork)){
                    öffneNetConfig();
                }else Snackbar.make(view, "Major Network Address not valid", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                MainActivity.tastaturVerstecken(getActivity());
            }
        });
    }
    private void öffneNetConfig(){
        NetConfigDialog netConfigDialog = new NetConfigDialog();
        assert getFragmentManager() != null;
        netConfigDialog.show(getFragmentManager(),getString(R.string.net_conf_dialog));
        netConfigDialog.setTargetFragment(VLSMFragment.this,1);
    }
    private void löscheAlleViews(){
        for (final EditText ipPartEditText : dIpAddresseEditText){
            ipPartEditText.setText("");
        }
        dIpMaskeEditText.setText("");
    }
    public void clearAll(View view){
        löscheAlleViews();
        Snackbar.make(view, "Clear", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        dLösungListView.setVisibility(View.INVISIBLE);
    }
    private void bewegeCursorZuNext(int currentIndex){
        EditText next;
        if (currentIndex+1 < dIpAddresseEditText.length)
            next = dIpAddresseEditText[currentIndex+1];
        else
            next = dIpMaskeEditText;

        Selection.setSelection(next.getText(), next.getSelectionStart());
        next.requestFocus();
    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vlsm, container, false);
        initviews(rootView);
        return rootView;
    }
    @Override
    public void anwendeNetzwerkeMap(HashMap<String, Integer> subNetsMap) {
        dSubNetsMappe = subNetsMap;
        dLösungSubNets = NetzwerkManager.berechneVLSM(dMajorNetwork, dSubNetsMappe);
        SubnetLösungAdapter subnetLösungAdapter = new SubnetLösungAdapter(getContext(),R.layout.subnet_losung, dLösungSubNets);
        dLösungListView.setAdapter(subnetLösungAdapter);
        dLösungListView.setVisibility(View.VISIBLE);

    }
    private class SubnetLösungAdapter extends ArrayAdapter<NetzwerkManager.Subnet>{
        private Context dKontext;
        private int dResource;
        private List<NetzwerkManager.Subnet> dSubNetList;

        SubnetLösungAdapter(Context context, int resource, List<NetzwerkManager.Subnet> subNetList) {
            super(context, resource, subNetList);
            dKontext = context;
            dResource = resource;
            dSubNetList = subNetList;

        }

        @Override
        public int getCount() {
            return dSubNetList.size();
        }

        @NonNull
        @Override
        public NetzwerkManager.Subnet getItem(int position) {
            return dSubNetList.get(position);
        }

        @SuppressLint({"StringFormatInvalid", "ViewHolder"})
        @NonNull
        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            LayoutInflater inflater= LayoutInflater.from(dKontext);

            convertView = inflater.inflate(dResource,parent,false) ;
            try {
                TextView curResNetworkNameTextView       = convertView.findViewById(R.id.resNetworkNameTextView);
                TextView currNetworkTextView             = convertView.findViewById(R.id.resNetworkTextView);
                TextView currNetHostsRangeTextView       = convertView.findViewById(R.id.resHostsRangeTextView);
                TextView CurrNetBroadcastTextView        = convertView.findViewById(R.id.resBroadcastTextView);
                TextView currNetSizeRequiredTextView     = convertView.findViewById(R.id.resNetSizeRequiredTextView);
                TextView resNetSizeAllocatedTextView     = convertView.findViewById(R.id.resNetSizeAllocatedTextView);

                curResNetworkNameTextView.setText(getItem(position).name);
                currNetworkTextView.setText(Html.fromHtml((getString(R.string.res_network,getItem(position).addresse,getItem(position).maskeCIDR))));
                currNetHostsRangeTextView.setText(getItem(position).bereich);
                CurrNetBroadcastTextView.setText(getItem(position).broadcast);
                int percentage = (int)(((float)getItem(position).benötigteGröße / (float)getItem(position).zugeteileteGröße)*100);
                currNetSizeRequiredTextView.setText(Html.fromHtml(getString(R.string.res_net_size_req,getItem(position).benötigteGröße)));
                resNetSizeAllocatedTextView.setText(Html.fromHtml(getString(R.string.res_net_size_alloc,getItem(position).zugeteileteGröße,percentage)));
            }catch (NullPointerException e){
                Log.e("Exception", Log.getStackTraceString(e));
            }
            catch (Exception e){
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

            }
            return convertView;
        }
    }



}
