package com.example.canteenchecker.canteenmanager.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication1;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.activity.LoginActivity;
import com.example.canteenchecker.canteenmanager.viewmodel.CanteenManagerViewModel;


public class ContactFragment extends FragmentChanges {
    private static final int LOGIN_FOR_CONTACT_FRAGMENT = 125;
    private static final String TAG = ContactFragment.class.getName();

    private TextView edtWebsite;
    private TextView edtPhone;

    private CanteenManagerViewModel model;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
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
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        edtWebsite = view.findViewById(R.id.edtWebsite);
        edtPhone = view.findViewById(R.id.edtPhone);

        if (!CanteenManagerApplication1.getInstance().isAuthenticated()) {
            Log.i(TAG, "not logged in -> log in ");
            startActivityForResult(LoginActivity.createIntent(getActivity()), LOGIN_FOR_CONTACT_FRAGMENT);
        } else {
            loadCanteenData();
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_FOR_CONTACT_FRAGMENT && resultCode == Activity.RESULT_OK) {
            loadCanteenData();
        }
    }

    private void loadCanteenData() {
        model = ViewModelProviders.of(getActivity()).get(CanteenManagerViewModel.class);
        model.getCanteen().observe(this, canteen -> {
            edtWebsite.setText(canteen.getWebsite());
            edtPhone.setText(canteen.getPhone());
        });
    }

    @Override
    public void saveChanges() {
        model.getCanteen().getValue().setWebsite(edtWebsite.getText().toString());
        model.getCanteen().getValue().setPhone(edtPhone.getText().toString());
    }

    @Override
    public void updateView() {
        // do nothing
    }
}