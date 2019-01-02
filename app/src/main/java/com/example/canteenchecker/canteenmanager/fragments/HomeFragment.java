package com.example.canteenchecker.canteenmanager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.activity.LoginActivity;

public class HomeFragment extends FragmentChanges {
    private static final String TAG = HomeFragment.class.getName();

    private static final int LOGIN_FOR_HOME_FRAGMENT = 124;

    private Button btnHomeLogin;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();

        // TODO: save parameter
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnHomeLogin = view.findViewById(R.id.btnHomeLogin);
        btnHomeLogin.setOnClickListener(v -> startActivityForResult(LoginActivity.createIntent(getActivity()), LOGIN_FOR_HOME_FRAGMENT));

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_FOR_HOME_FRAGMENT && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "login successful");
        }
    }

    @Override
    public void saveChanges() {
        // do nothing
    }

    @Override
    public void updateView() {
        // do nothing
    }
}
