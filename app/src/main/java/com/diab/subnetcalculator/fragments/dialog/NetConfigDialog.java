package com.diab.subnetcalculator.fragments.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.diab.subnetcalculator.MainActivity;
import com.diab.subnetcalculator.R;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class NetConfigDialog extends DialogFragment {
    private HashMap<String, Integer> dSubNetsHashMappe = new LinkedHashMap<>(); // [name: size]
    private NetConfigDialog.NetConfigDialogListener dNetConfigListener;
    private ListView dSubNetsHashMappeListView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(R.layout.net_config, null);

        builder.setView(view)
                .setTitle(R.string.label_net_conf_d_title)
                .setNegativeButton(R.string.button_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.tastaturVerstecken(getContext());
                    }
                }).setPositiveButton(R.string.button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String key : dSubNetsHashMappe.keySet()) {
                            if (dSubNetsHashMappe.get(key) == null || dSubNetsHashMappe.get(key) < 1) {
                                dSubNetsHashMappe.clear();
                                dNetConfigListener.anwendeNetzwerkeMap(dSubNetsHashMappe);
                                Toast.makeText(getActivity().getBaseContext(), "Network config not valid !", Toast.LENGTH_SHORT);
                                return;
                            }
                        }
                        dNetConfigListener.anwendeNetzwerkeMap(dSubNetsHashMappe);
                        MainActivity.tastaturVerstecken(getContext());
                    }
        });


        dSubNetsHashMappeListView = view.findViewById(R.id.subNetHashMapListView);
        FloatingActionButton mAddSubNetFloatingActionButton = view.findViewById(R.id.addSubNetFloatingActionButton);


        mAddSubNetFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dSubNetsHashMappe.put("Network "+String.valueOf(dSubNetsHashMappe.size()+1), null);
                aktualiesiereSubNetsHashMappeListView();
            }
        });

        dSubNetsHashMappe.put("Network 1",null);

        aktualiesiereSubNetsHashMappeListView();
        final Dialog dialog = builder.create();

        dSubNetsHashMappeListView.post(new Runnable() {
            @Override
            public void run() {
               dialog.getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

            }
        });

        return dialog;
    }
    private void aktualiesiereSubNetsHashMappeListView(){
        subnethashmapadapter subNetHashMapAdapter = new subnethashmapadapter(getContext(),R.layout.subnet_grosse_input, dSubNetsHashMappe);
        dSubNetsHashMappeListView.setAdapter(subNetHashMapAdapter);
        MainActivity.tastaturVerstecken(getContext());

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dNetConfigListener = (NetConfigDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+ "Must implement NetConfigDialogListener");
        }
    }
    public interface  NetConfigDialogListener{
        void anwendeNetzwerkeMap(HashMap<String,Integer> subNetsMap);
    }

    private class subnethashmapadapter extends BaseAdapter {
        private Context dContext;
        private HashMap<String, Integer> dData;
        private String[] dSchlüssel;
        private int dResource;

        subnethashmapadapter(Context context, int resource, HashMap<String, Integer> data){
            dContext = context;
            dData = data;
            dResource = resource;
            dSchlüssel = dData.keySet().toArray(new String[data.size()]);
        }

        @Override
        public int getCount() {
            return dData.size();
        }

        @Override
        public Integer getItem(int position) {
            return dData.get(dSchlüssel[position]);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            LayoutInflater inflater= LayoutInflater.from(dContext);

            convertView = inflater.inflate(dResource,parent,false) ;
            try {
                final TextView subNetNameEditText       = convertView.findViewById(R.id.subNetNameEditText);
                final EditText subNetSizeEditText       = convertView.findViewById(R.id.subNetSizeEditText);
                ImageButton delSubNetButton       = convertView.findViewById(R.id.delSubNetButton);

                //show keyBoard on focus
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(subNetSizeEditText, InputMethodManager.SHOW_IMPLICIT);

                final String name = dSchlüssel[pos];
                final Integer size = getItem(pos);

                subNetNameEditText.setText(name);
                if(size != null){
                    subNetSizeEditText.setText(String.valueOf(size));
                }

                subNetNameEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        dData.put(s.toString(),size);
                        aktualiesiereSubNetsHashMappeListView();
                    }
                });
                subNetSizeEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @SuppressLint("ShowToast")
                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            if ((Integer.valueOf(s.toString()) > 0)){
                                dData.put(name, Integer.valueOf(s.toString()));
                                subNetSizeEditText.setTextColor(Color.BLACK);

                            }else {
                                subNetSizeEditText.setTextColor(Color.RED);
                            }

                        }catch (NumberFormatException ignored){

                        }catch (Exception e){
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT);
                        }

                    }
                });
                delSubNetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dData.remove(name);
                        aktualiesiereSubNetsHashMappeListView();
                    }
                });

            }catch (Exception e){
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            return convertView;
        }
    }

}
