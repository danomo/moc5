package com.example.canteenchecker.canteenmanager.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.viewmodel.CanteenManagerViewModel;

import java.text.NumberFormat;

public class MenuFragment extends FragmentChanges {
    private static final String TAG = MenuFragment.class.getName();

    private CanteenManagerViewModel model;

    private TextView edtMenuName;
    private TextView edtMenuPrice;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "MenuFragment onCreateView");

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        edtMenuName = view.findViewById(R.id.edtMenuName);
        edtMenuPrice = view.findViewById(R.id.edtMenuPrice);

        displayCanteenData();

        return view;
    }

    private void displayCanteenData() {
        model = ViewModelProviders.of(getActivity()).get(CanteenManagerViewModel.class);
        model.getCanteen().observe(this, canteen -> {
            edtMenuName.setText(canteen.getMeal());
            edtMenuPrice.setText(NumberFormat.getNumberInstance().format(canteen.getMealPrice()));
        });
    }

    @Override
    public void saveChanges() {
        model.getCanteen().getValue().setMeal(edtMenuName.getText().toString());
        String s = edtMenuPrice.getText().toString();
        s = s.replace(",", ".");
        model.getCanteen().getValue().setMealPrice(Float.parseFloat(s));
    }
}
