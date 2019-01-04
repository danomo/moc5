package com.example.canteenchecker.canteenmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.activity.LoginActivity;

public class HomeFragment extends FragmentChanges {
    private static final String TAG = HomeFragment.class.getName();

    private static final int LOGIN_FOR_HOME_FRAGMENT = 123;

    private Button btnHomeLogin;

    public HomeFragment() {
        // Required empty public constructor
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
    public void saveChanges() {
        // do nothing
    }
}
