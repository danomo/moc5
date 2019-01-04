package com.example.canteenchecker.canteenmanager.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.domainobjects.Canteen;
import com.example.canteenchecker.canteenmanager.domainobjects.CanteenRating;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxyManager;
import com.example.canteenchecker.canteenmanager.service.MyFirebaseMessagingService;
import com.example.canteenchecker.canteenmanager.viewmodel.CanteenManagerViewModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RatingFragment extends FragmentChanges {
    private static final String TAG = RatingFragment.class.getName();
    private static final int LOGIN_FOR_RATING_FRAGMENT = 125;

    private final CanteenRatingAdapter ratingAdapter = new CanteenRatingAdapter();
    private CanteenManagerViewModel model;
    private TextView txtCountRatings;
    private TextView txtAvgRating;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Firebase  broadcastReceiver  new Message received.");
            //  Toast.makeText(getApplication(), "neues Rating!", Toast.LENGTH_SHORT);

            displayCanteenData();
        }
    };
    //  private SwipeRefreshLayout srlRatings;
    private RecyclerView rcvRatings;


    public RatingFragment() {
        // Required empty public constructor
    }

    public static RatingFragment newInstance(String param1, String param2) {
        RatingFragment fragment = new RatingFragment();
        Bundle args = new Bundle();

        // TODO add data to bundle
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO restore data from bundle
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rating, container, false);

        txtCountRatings = view.findViewById(R.id.txtCountRatings);
        txtAvgRating = view.findViewById(R.id.txtAvgRating);
        // srlRatings = view.findViewById(R.id.srlRatings);
        rcvRatings = view.findViewById(R.id.rcvRatings);

        rcvRatings.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvRatings.setAdapter(ratingAdapter);
        rcvRatings.setItemAnimator(new DefaultItemAnimator());

        //  srlRatings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        //    @Override
        //      public void onRefresh() {
        //  displayCanteenData();
        //      }
        //  });


        displayCanteenData();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, MyFirebaseMessagingService.updatedRatingsMessage());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadCanteenData() {
        model = ViewModelProviders.of(getActivity()).get(CanteenManagerViewModel.class);
        model.getCanteen().observe(this, canteen -> {
            if (canteen == null) {
                Log.e(TAG, "canteen is null - RatingFragment");
            } else {
                Log.i(TAG, "cantee canteen.getRatings().size()  " + canteen.getRatings().size());
                Log.i(TAG, "cantee txtAvgRating   " + canteen.getAverageRating());

                txtCountRatings.setText(String.valueOf(canteen.getRatings().size()));
                txtAvgRating.setText(String.format("%f", canteen.getAverageRating()));

                // srlRatings.setRefreshing(true);

                canteen.getRatings().stream().forEach(r -> {
                    Log.i(TAG, "cantee rating   " + r.getRatingId() + " " + r.getRemark() + " " + r.getUsername());
                });

                ratingAdapter.displayRatings(canteen.getRatings());
                // srlRatings.setRefreshing(false);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void updateView() {
        loadCanteenData();
    }

    @Override
    public void saveChanges() {
        // nothing to do here
    }

    @SuppressLint("StaticFieldLeak")
    private void displayCanteenData() {
        // srlRatings.setRefreshing(true);

        new AsyncTask<Void, Void, Canteen>() {
            @Override
            protected Canteen doInBackground(Void... voids) {
                try {
                    return new ServiceProxyManager().getCanteen();
                } catch (IOException e) {
                    return null;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onPostExecute(Canteen c) {
                if (c == null) {
                    Log.e(TAG, "got NO canteen   ");
                    return;
                }
                Log.e(TAG, "ratings   size " + c.getRatings().size());
                c.getRatings().stream().forEach(r -> {
                    Log.e(TAG, "rating id " + r.getRatingId() + " " + r.getUsername() + " " + r.getRemark());
                });

                Collection<CanteenRating> ratings = c.getRatings().stream().sorted((a, b) -> (a.getTimestamp() < b.getTimestamp() ? 1 : -1)).collect(Collectors.toList());
                // srlRatings.setRefreshing(false);
                ratingAdapter.displayRatings(ratings);
                txtCountRatings.setText(String.valueOf(c.getRatings().size()));
                txtAvgRating.setText(String.format("%4.1f", c.getAverageRating()));
            }
        }.execute();
    }

    class CanteenRatingAdapter extends RecyclerView.Adapter<CanteenRatingAdapter.ViewHolder> {
        private final String TAG = CanteenRatingAdapter.class.getName();

        private final List<CanteenRating> canteenList = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final CanteenRating c = canteenList.get(position);
            holder.txvUsername.setText(c.getUsername() + " | ");
            holder.txvRemark.setText(c.getRemark());
            holder.rtbRating.setRating(c.getRatingPoints());

            Date date = new Date(c.getTimestamp());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
            String format = formatter.format(date);
            holder.txvCreated.setText(format);

            holder.btnDelete.setOnClickListener(v -> {
                        Log.i(TAG, "button delete clicked deleting ratingId " + c.getRatingId() + " was successful   ");

                        new AsyncTask<Integer, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Integer... integers) {
                                try {
                                    return new ServiceProxyManager().deleteRating(c.getRatingId());
                                } catch (IOException e) {
                                    return null;
                                }
                            }

                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            protected void onPostExecute(Boolean res) {
                                if (res) {
                                    canteenList.removeIf(r -> r.getRatingId() == c.getRatingId());
                                    notifyDataSetChanged();
                                    Toast.makeText(getActivity(), "Bewertung wurde gelöscht (Id: " + c.getRatingId() + ")", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "deleting ratingId " + c.getRatingId() + " was successful   ");
                                } else {
                                    Toast.makeText(getActivity(), "FEHLER: Bewertung konnte nicht gelöscht  werden.(Id: " + c.getRatingId() + ")", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "deleting ratingId " + c.getRatingId() + " was NOT successful   ");
                                }
                            }
                        }.execute();
                    }
            );
        }

        @Override
        public int getItemCount() {
            return canteenList.size();
        }

        public void displayRatings(Collection<CanteenRating> canteens) {
            canteenList.clear();
            if (canteens != null) {
                canteenList.addAll(canteens);
            }
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView txvUsername = itemView.findViewById(R.id.txvUsername);
            private final TextView txvCreated = itemView.findViewById(R.id.txvCreated);
            private final TextView txvRemark = itemView.findViewById(R.id.txvRemark);
            private final RatingBar rtbRating = itemView.findViewById(R.id.rtbRating);
            private final Button btnDelete = itemView.findViewById(R.id.btnDelete);

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
