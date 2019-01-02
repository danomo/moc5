package com.example.canteenchecker.canteenmanager.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication1;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.activity.LoginActivity;
import com.example.canteenchecker.canteenmanager.viewmodel.CanteenManagerViewModel;

public class WaitingFragment extends FragmentChanges  {
    public static  final  String TAG = WaitingFragment.class.getName();
    private static final int LOGIN_FOR_WAITING_FRAGMENT = 127;

    private SeekBar skbWaitingTime;

    private CanteenManagerViewModel model;

    public WaitingFragment() {
        // Required empty public constructor
    }


    public static WaitingFragment newInstance(String param1, String param2) {
        WaitingFragment fragment = new WaitingFragment();

        // TODO: implement ...
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.e(TAG, "WaitingFragment onCreateView");

        View view = inflater.inflate(R.layout.fragment_waiting, container, false);

        skbWaitingTime = view.findViewById(R.id.sbrWaitingTime);

        if (!CanteenManagerApplication1.getInstance().isAuthenticated()) {
            Log.i(TAG, "not logged in -> log in ");
            startActivityForResult(LoginActivity.createIntent(getActivity()), LOGIN_FOR_WAITING_FRAGMENT);
        } else {
            loadCanteenData();
        }
        // Inflate the layout for this fragment
        return view;
    }

    private void loadCanteenData() {
        model = ViewModelProviders.of(getActivity()).get(CanteenManagerViewModel.class);
        model.getCanteen().observe(this, canteen -> {
            skbWaitingTime.setProgress(canteen.getAverageWaitingTime());
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_FOR_WAITING_FRAGMENT && resultCode == Activity.RESULT_OK) {
            loadCanteenData();
        }
    }

    @Override
    public void saveChanges() {
        model.getCanteen().getValue().setAverageWaitingTime(skbWaitingTime.getProgress());
    }

    @Override
    public void updateView() {
        // do nothing
    }
}
