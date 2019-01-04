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

    private static final int LOGIN_FOR_CANTEEN_MANAGER = 23;

    final FragmentChanges fragmentAddress = new AddressFragment();
    final FragmentChanges fragmentContact = new ContactFragment();
    final FragmentChanges fragmentHome = new HomeFragment();
    final FragmentChanges fragmentMenu = new MenuFragment();
    final FragmentChanges fragmentRating = new RatingFragment();
    final FragmentChanges fragmentWaiting = new WaitingFragment();

    FragmentChanges currentFragment;

    private CanteenManagerViewModel canteenModel;

    private TextView txvNavSubTitle;
    private TextView txvNavTitle;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Firebase  broadcastReceiver  new Message received.");
            //  Toast.makeText(getApplication(), "neues Rating!", Toast.LENGTH_SHORT);

            getCanteen();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        txvNavSubTitle = hView.findViewById(R.id.txvNavSubTitle);
        txvNavTitle = hView.findViewById(R.id.txvNavTitle);

        canteenModel = ViewModelProviders.of(this).get(CanteenManagerViewModel.class);

        if (!CanteenManagerApplication.getInstance().isAuthenticated()) {
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

        Log.i(TAG, "MainActivity.onActivityResult   logged in -start getCanteen() ");
        Log.i(TAG, "MainActivity.onActivityResult  requestCode:  " + requestCode + "  resultCode  " + resultCode);

        if (requestCode == LOGIN_FOR_CANTEEN_MANAGER && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "  now i am logged in -> getCanteen");
            getCanteen();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        switch (id) {
            case R.id.nav_address:
                loadFragment(fragmentAddress, true);
                break;
            case R.id.nav_contact:
                loadFragment(fragmentContact, true);
                break;
            case R.id.nav_menu:
                loadFragment(fragmentMenu, true);
                break;
            case R.id.nav_ratings:
                loadFragment(fragmentRating, true);
                break;
            case R.id.nav_waitingperiod:
                loadFragment(fragmentWaiting, true);
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "logout success", Toast.LENGTH_SHORT).show();
                CanteenManagerApplication.getInstance().setAuthenticationToken(null);
                txvNavSubTitle.setText(null);
                txvNavTitle.setText(getString(R.string.NoUserLoggedIn));
                loadFragment(fragmentHome, false);
                break;
            default:
                Log.e(TAG, "switch ended in default - should not happen!");
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
        if (item.getItemId() == R.id.mniSaveChanges) {
            currentFragment.saveChanges();
            updateCanteen();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem((R.id.mniSaveChanges)).setVisible(
                CanteenManagerApplication.getInstance().isAuthenticated() &&
                        ((currentFragment != fragmentRating) || (currentFragment != fragmentHome)));

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
                    Toast.makeText(getApplicationContext(), "FEHLER: Konnte Kantine nicht vom Server laden.", Toast.LENGTH_LONG).show();
                    return;
                }
                canteenModel.setCanteen(c);
                txvNavSubTitle.setText(c.getAddress());
                txvNavTitle.setText(c.getName());
                Log.e(TAG, "got a canteen: " + c.getCanteenId() + " " + c.getName());
                Toast.makeText(getApplicationContext(), "Kantine erfolgreich geladen.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Kantine konnte nicht upgedated werden.", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i(TAG, "got a canteen: " + c.getCanteenId() + " " + c.getName());
                Toast.makeText(getApplicationContext(), "Kantine erfolgreich upgedated.", Toast.LENGTH_LONG).show();
            }

        }.execute(c);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void loadFragment(FragmentChanges fragment, boolean checkLoggedIn) {
        currentFragment = fragment;

        if (checkLoggedIn && !CanteenManagerApplication.getInstance().isAuthenticated()) {
            Log.e(TAG, "not logged in -> log in ");
            startActivityForResult(LoginActivity.createIntent(this), LOGIN_FOR_CANTEEN_MANAGER);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.replace_fragments, currentFragment);
        ft.addToBackStack(null);
        ft.commit();

        invalidateOptionsMenu();
    }
}
