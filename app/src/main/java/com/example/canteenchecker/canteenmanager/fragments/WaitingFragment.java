package com.example.canteenchecker.canteenmanager.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.viewmodel.CanteenManagerViewModel;

public class WaitingFragment extends FragmentChanges {
    public static final String TAG = WaitingFragment.class.getName();

    private SeekBar skbWaitingTime;
    private TextView txvWaitingTime;

    private CanteenManagerViewModel model;

    public WaitingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txvWaitingTime.setText(String.format("%d Minuten", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        };

        // Inflate the layout for this fragment

        Log.e(TAG, "WaitingFragment onCreateView");

        View view = inflater.inflate(R.layout.fragment_waiting, container, false);

        skbWaitingTime = view.findViewById(R.id.sbrWaitingTime);
        txvWaitingTime = view.findViewById(R.id.txvWaitingTime);

        skbWaitingTime.setOnSeekBarChangeListener(seekBarChangeListener);

        displayCanteenData();

        return view;
    }

    private void displayCanteenData() {
        model = ViewModelProviders.of(getActivity()).get(CanteenManagerViewModel.class);
        model.getCanteen().observe(this, canteen -> {
            skbWaitingTime.setProgress(canteen.getAverageWaitingTime());
        });
    }

    @Override
    public void saveChanges() {
        model.getCanteen().getValue().setAverageWaitingTime(skbWaitingTime.getProgress());
    }
}
