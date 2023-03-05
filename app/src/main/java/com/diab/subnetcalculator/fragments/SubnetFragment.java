package com.diab.subnetcalculator.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diab.subnetcalculator.R;
import com.diab.subnetcalculator.model.NetzwerkManager;

public class SubnetFragment extends Fragment {
    private EditText[] dIpAddressEditText;
    private EditText dIpMaskeEditText;
    private SeekBar dIpMaskeSeekBar;
    private TextView dResNetzwerkTextView;
    private TextView dResMaskeTextView;
    private TextView dResHostsBereichTextView;
    private TextView dResBroadcastTextView;
    private TextView dResNetGrösseTextView;
    private TextView dResWildcardMaskeTextView;
    private TableLayout dLösungTableLayout;

    private void initViews(View rootView) {
        dIpAddressEditText = new EditText[4];
        dIpAddressEditText[0] = rootView.findViewById(R.id.ipPartAEditText);
        dIpAddressEditText[1] = rootView.findViewById(R.id.ipPartBEditText);
        dIpAddressEditText[2] = rootView.findViewById(R.id.ipPartCEditText);
        dIpAddressEditText[3] = rootView.findViewById(R.id.ipPartDEditText);
        dIpMaskeEditText = rootView.findViewById(R.id.ipMaskEditText);
        dIpMaskeSeekBar = rootView.findViewById(R.id.ipMaskSeekBar);

        dLösungTableLayout = rootView.findViewById(R.id.resultTableLayout);
        dResNetzwerkTextView = rootView.findViewById(R.id.resNetworkTextView);
        dResMaskeTextView = rootView.findViewById(R.id.resMaskTextView);
        dResWildcardMaskeTextView = rootView.findViewById(R.id.resWildcardMaskTextView);
        dResHostsBereichTextView = rootView.findViewById(R.id.resHostsRangeTextView);
        dResBroadcastTextView = rootView.findViewById(R.id.resBroadcastTextView);
        dResNetGrösseTextView = rootView.findViewById(R.id.resNetSizeTextView);

        löscheAlleViews();

        setListenerOnEditableViews();
        ImageButton clearImageButton = rootView.findViewById(R.id.clearImageButton);
        clearImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                löscheAlles(v);
            }
        });


    }

    private void setListenerOnEditableViews() {
        for (int i = 0; i < dIpAddressEditText.length; i++) {
            final int finalI = i;
            dIpAddressEditText[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    String ss = s.toString().toLowerCase();
                    Log.d("Debug", "last char: " + ss);
                    if (s.toString().endsWith(".")){
                        bewegeCursorToNext(finalI);
                    }

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    /*move cursor when reach 3 char or press (dot)*/
                    if (s.length() >= 3) {
                        bewegeCursorToNext(finalI);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    for(int i = 0; i<s.length(); i++){
                    }
                    if (s.length() > 0) {
                        try {

                            if (s.length() > 0 && Integer.parseInt(s.toString()) < 0 || Integer.parseInt(s.toString()) > 255) {
                                dIpAddressEditText[finalI].setTextColor(Color.RED);
                                dLösungTableLayout.setVisibility(View.INVISIBLE);
                            } else {
                                dIpAddressEditText[finalI].setTextColor(Color.BLACK);
                                lösungAktualisieren();
                            }
                        } catch (NumberFormatException e) {
                            Log.e("Exception", Log.getStackTraceString(e));

                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }

                }
            });

            dIpAddressEditText[i].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        bewegeCursorToNext(finalI);
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
                if (s.length() > 0) {
                    try {
                        if (Integer.parseInt(s.toString()) < 0 || Integer.parseInt(s.toString()) > 32) {
                            dIpMaskeEditText.setTextColor(Color.rgb(249, 2, 23));
                            dLösungTableLayout.setVisibility(View.INVISIBLE);
                        } else if (Integer.parseInt(s.toString()) == 0 || Integer.parseInt(s.toString()) > 30) {
                            dIpMaskeEditText.setTextColor(Color.rgb(242, 114, 2));

                        } else {
                            dIpMaskeEditText.setTextColor(Color.BLACK);
                            dIpMaskeSeekBar.setProgress(Integer.parseInt(dIpMaskeEditText.getText().toString()));
                            lösungAktualisieren();
                        }

                    } catch (NumberFormatException ignored) {

                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dIpMaskeEditText.setSelection(dIpMaskeEditText.getText().length());
                }
            }
        });

        dIpMaskeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dIpMaskeEditText.setText(String.valueOf(progress));
                lösungAktualisieren();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void bewegeCursorToNext(int currentIndex) {
        EditText next;
        if (currentIndex + 1 < dIpAddressEditText.length)
            next = dIpAddressEditText[currentIndex + 1];
        else
            next = dIpMaskeEditText;

        Selection.setSelection(next.getText(), next.getSelectionStart());
        next.requestFocus();
    }

    private void löscheAlleViews() {
        dLösungTableLayout.setVisibility(View.INVISIBLE);

        for (final EditText ipPartEditText : dIpAddressEditText) {
            ipPartEditText.setText("");
        }
        dIpMaskeEditText.setText("");

    }

    private void lösungAktualisieren() {
        try {
            String ipAddress = dIpAddressEditText[0].getText().toString() + "."
                    + dIpAddressEditText[1].getText().toString() + "."
                    + dIpAddressEditText[2].getText().toString() + "."
                    + dIpAddressEditText[3].getText().toString() + "/"
                    + dIpMaskeEditText.getText().toString();
            NetzwerkManager.Subnet mIpNetwork = new NetzwerkManager.Subnet(ipAddress);
            int mask = Integer.valueOf(dIpMaskeEditText.getText().toString());

            String networkAddress = mIpNetwork.addresse;
            String networkRange = mIpNetwork.bereich;
            String networkBroadcast = mIpNetwork.broadcast;
            String networkAllocatedSize = String.valueOf(mIpNetwork.zugeteileteGröße);
            String wildcardMask = NetzwerkManager.toDecWildCardMaske(mask);
            if (mask == 0) {
                networkAddress = "0.0.0.0";
                networkRange = "0.0.0.0 - 255.255.255.254";
                networkBroadcast = "255.255.255.255";

            } else if (mask > 30) {
                networkRange = "NONE";
                networkAllocatedSize = "NONE";
                networkBroadcast = "NONE";
            }

            dResNetzwerkTextView.setText(networkAddress);
            dResMaskeTextView.setText(mIpNetwork.decMask);
            dResWildcardMaskeTextView.setText(wildcardMask);
            dResHostsBereichTextView.setText(networkRange);
            dResBroadcastTextView.setText(networkBroadcast);
            dResNetGrösseTextView.setText(networkAllocatedSize);

            dLösungTableLayout.setVisibility(View.VISIBLE);
            //** hide Keyboard ?

        } catch (IllegalArgumentException e) {
            /*-----expected EXCEPTION -------*/
            dLösungTableLayout.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public void löscheAlles(View view) {
        löscheAlleViews();
        Snackbar.make(view, "Clear", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subnet, container, false);
        initViews(rootView);
        return rootView;
    }
}
