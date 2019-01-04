package com.example.canteenchecker.canteenmanager.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.viewmodel.CanteenManagerViewModel;

public class ContactFragment extends FragmentChanges {
    private static final String TAG = ContactFragment.class.getName();

    private TextView edtWebsite;
    private TextView edtPhone;

    private CanteenManagerViewModel model;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        edtWebsite = view.findViewById(R.id.edtWebsite);
        edtPhone = view.findViewById(R.id.edtPhone);

        displayCanteenData();

        return view;
    }

    private void displayCanteenData() {
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
}
