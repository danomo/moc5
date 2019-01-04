package com.example.canteenchecker.canteenmanager.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.viewmodel.CanteenManagerViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class AddressFragment extends FragmentChanges {
    private static final String TAG = AddressFragment.class.getName();

    private static final int DEFAULT_MAP_ZOOM_FACTOR = 15;

    private CanteenManagerViewModel model;

    private TextView edtAddress;
    private TextView edtName;
    private Button btnShowInMap;
    private Button btnTakeFromMap;
    private SupportMapFragment mpfMap;

    private Marker m;

    public AddressFragment() {
        // Required empty public constructor
    }

    public static AddressFragment newInstance(String param1, String param2) {
        AddressFragment fragment = new AddressFragment();

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
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        edtAddress = view.findViewById(R.id.edtAddress);
        edtName = view.findViewById(R.id.edtName);
        btnShowInMap = view.findViewById(R.id.btnShowInMap);
        btnTakeFromMap = view.findViewById(R.id.btnTakeFromMap);

        displayCanteenData();

        FragmentManager fm = getChildFragmentManager();
        mpfMap = (SupportMapFragment) fm.findFragmentById(R.id.mpfMap);

        mpfMap.getMapAsync(getOnMapReadyCallback());

        btnShowInMap.setOnClickListener(v -> showAddressInMap());
        btnTakeFromMap.setOnClickListener(v -> getAddressFromMap());

        return view;
    }

    private void displayCanteenData() {
        model = ViewModelProviders.of(getActivity()).get(CanteenManagerViewModel.class);
        model.getCanteen().observe(this, canteen -> {
            edtAddress.setText(canteen.getAddress());
            edtName.setText(canteen.getName());
            updateMap(canteen.getAddress());
        });
    }

    @Override
    public void saveChanges() {
        model.getCanteen().getValue().setAddress(edtAddress.getText().toString());
        model.getCanteen().getValue().setName(edtName.getText().toString());
    }

    private void showAddressInMap() {
        updateMap(edtAddress.getText().toString());
    }

    @SuppressLint("StaticFieldLeak")
    private void getAddressFromMap() {
        Log.i(TAG, "marker m    lat: " + m.getPosition().latitude + " lng: " + m.getPosition().longitude);

        new AsyncTask<LatLng, Void, String>() {
            @Override
            protected String doInBackground(LatLng... latLngs) {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity());
                String address = "";
                try {
                    addresses = geocoder.getFromLocation(latLngs[0].latitude, latLngs[0].longitude, 1);

                    if (addresses != null) {
                        address = addresses.get(0).getAddressLine(0);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "exception when calling  geocoder.getFromLocation   with lat = " + latLngs[0].latitude + "   lng = "
                            + latLngs[0].longitude + "    exception: " + e.getMessage());
                    return null;
                }
                return address;
            }

            @Override
            protected void onPostExecute(final String address) {
                if (address == null) {
                    Toast.makeText(getActivity(), "Error fetching address for marker", Toast.LENGTH_SHORT);
                    Log.e(TAG, "error retrieving address for position " + m.getPosition().toString());
                    return;
                }
                edtAddress.setText(address);
            }
        }.execute(m.getPosition());
    }

    @SuppressLint("StaticFieldLeak")
    private void updateMap(String address) {
        new AsyncTask<String, Void, LatLng>() {
            @Override
            protected LatLng doInBackground(String... strings) {

                LatLng location = null;
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(strings[0], 1);
                    if (addresses != null || addresses.size() > 0) {
                        Address adress = addresses.get(0);
                        location = new LatLng(adress.getLatitude(), adress.getLongitude());
                    }
                } catch (Exception e) {
                    Log.w(TAG, String.format("Resolving of address for '%s' failed", strings[0]), e);
                }
                return location;
            }

            @Override
            protected void onPostExecute(final LatLng latLng) {
                mpfMap.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.clear();
                        if (latLng != null) {
                            googleMap.addMarker(new MarkerOptions().position(latLng));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM_FACTOR));
                        } else {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 0));
                        }
                    }
                });

            }
        }.execute(address);
    }

    @NonNull
    private OnMapReadyCallback getOnMapReadyCallback() {
        return googleMap -> {
            UiSettings uiSettings = googleMap.getUiSettings();
            uiSettings.setAllGesturesEnabled(true);
            uiSettings.setZoomControlsEnabled(true);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    googleMap.clear();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    m = googleMap.addMarker(markerOptions);
                }
            });
        };
    }
}
