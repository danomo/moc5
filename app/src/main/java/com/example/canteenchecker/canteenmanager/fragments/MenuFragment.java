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
    private static final int LOGIN_FOR_MENU_FRAGMENT = 124;

    private CanteenManagerViewModel model;

    private TextView edtMenuName;
    private TextView edtMenuPrice;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();

        // TODO: save data to bundle ...
        // Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //fragment.setArguments(args);´
        Log.i(TAG, "MenuFragment newInstance");

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "MenuFragment onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "MenuFragment onCreateView");

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        edtMenuName = view.findViewById(R.id.edtMenuName);
        edtMenuPrice = view.findViewById(R.id.edtMenuPrice);

        displayCanteenData();

        // Inflate the layout for this fragment
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
        model.getCanteen().getValue().setMealPrice(Float.parseFloat(edtMenuPrice.getText().toString()));
    }

    @Override
    public void updateView() {
        // do nothing
    }
}
