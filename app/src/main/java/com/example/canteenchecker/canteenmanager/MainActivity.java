package com.example.canteenchecker.canteenmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.activity.LoginActivity;
import com.example.canteenchecker.canteenmanager.domainobjects.Canteen;
import com.example.canteenchecker.canteenmanager.fragments.AddressFragment;
import com.example.canteenchecker.canteenmanager.fragments.ContactFragment;
import com.example.canteenchecker.canteenmanager.fragments.FragmentChanges;
import com.example.canteenchecker.canteenmanager.fragments.HomeFragment;
import com.example.canteenchecker.canteenmanager.fragments.MenuFragment;
import com.example.canteenchecker.canteenmanager.fragments.RatingFragment;
import com.example.canteenchecker.canteenmanager.fragments.WaitingFragment;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxyManager;
import com.example.canteenchecker.canteenmanager.service.MyFirebaseMessagingService;
import com.example.canteenchecker.canteenmanager.viewmodel.CanteenManagerViewModel;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();
    private static final int LOGIN_FOR_CANTEEN_MANAGER = 123;
    FragmentChanges fragment = null;
    private CanteenManagerViewModel canteenModel;

    private TextView txvNavSubTitle;
    private TextView txvNavTitle;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Firebase  broadcastReceiver  new Message received.");
            //  Toast.makeText(getApplication(), "neues Rating!", Toast.LENGTH_SHORT);

            getCanteen();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        txvNavSubTitle = hView.findViewById(R.id.txvNavSubTitle);
        txvNavTitle = hView.findViewById(R.id.txvNavTitle);

        canteenModel = ViewModelProviders.of(this).get(CanteenManagerViewModel.class);

        if (!CanteenManagerApplication1.getInstance().isAuthenticated()) {
            Log.i(TAG, "not logged in -> log in ");
            startActivityForResult(LoginActivity.createIntent(this), LOGIN_FOR_CANTEEN_MANAGER);
        } else {
            getCanteen();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, MyFirebaseMessagingService.updatedRatingsMessage());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_FOR_CANTEEN_MANAGER && resultCode == Activity.RESULT_OK) {
            getCanteen();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Bundle bundle = new Bundle();

        switch (id) {
            case R.id.nav_address:
                fragment = new AddressFragment();
                break;
            case R.id.nav_contact:
                fragment = new ContactFragment();
                break;
            case R.id.nav_menu:
                fragment = new MenuFragment();
                break;
            case R.id.nav_ratings:
                fragment = new RatingFragment();
                break;
            case R.id.nav_waitingperiod:
                fragment = new WaitingFragment();
                break;

            //TODO: remove?

            case R.id.nav_logout:
                Toast.makeText(this, "logout success", Toast.LENGTH_SHORT).show();
                CanteenManagerApplication1.getInstance().setAuthenticationToken(null);
                txvNavSubTitle.setText(null);
                txvNavTitle.setText("kein user angemeldet");
                fragment = new HomeFragment();

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.replace_fragments, fragment);
                ft.commit();

                break;

            default:
                Log.e(TAG, "switch ended in default - should not happen!");
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.replace_fragments, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // MENU in top bar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        fragment.saveChanges();
        updateCanteen();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO: set SAVE visible if changes were made to a field in a fragment
        // menu.findItem((R.id.mniCall)).setVisible(canteen != null && canteen.getPhoneNumber() != null && !canteen.getPhoneNumber().isEmpty());
        // menu.findItem((R.id.mniShowWebSite)).setVisible(canteen != null && canteen.getWebsite() != null && !canteen.getWebsite().isEmpty());

        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("StaticFieldLeak")
    private void getCanteen() {

        new AsyncTask<Void, Void, Canteen>() {
            @Override
            protected Canteen doInBackground(Void... voids) {
                try {
                    return new ServiceProxyManager().getCanteen();
                } catch (IOException e) {
                    Log.e(TAG, "an error occured while catching the canteen from the server. " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Canteen c) {
                if (c == null) {
                    Log.e(TAG, "could not connect to server, returned canteen is null.");
                    Toast.makeText(getApplicationContext(), "FEHLER: Konnte Kantine nicht vom Server laden.", Toast.LENGTH_SHORT).show();
                    return;
                }
                canteenModel.setCanteen(c);
                txvNavSubTitle.setText(c.getAddress());
                txvNavTitle.setText(c.getName());
                Log.i(TAG, "got a canteen: " + c.getCanteenId() + " " + c.getName());
                Toast.makeText(getApplicationContext(), "Kantine erfolgreich geladen.", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void updateCanteen() {
        Canteen c = canteenModel.getCanteen().getValue();
        Log.i(TAG, "updateCanteen : " + c.getAddress() + " " + c.getName() + " " + c.getMeal());

        new AsyncTask<Canteen, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Canteen... canteens) {
                try {
                    return new ServiceProxyManager().updateCanteen(canteens[0]);
                } catch (IOException e) {
                    Log.e(TAG, "an error occured while sendign the updated canteen to the server . " + e.getMessage());
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean res) {
                if (!res) {
                    Log.e(TAG, "could not connect to server, could not update canteen.");
                    Toast.makeText(getApplicationContext(), "Kantine konnte nicht upgedated werden.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "got a canteen: " + c.getCanteenId() + " " + c.getName());
                Toast.makeText(getApplicationContext(), "Kantine erfolgreich upgedated.", Toast.LENGTH_SHORT).show();
            }

        }.execute(c);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
