package com.example.canteenchecker.canteenmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication1;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxyManager;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getName();
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnLogIn;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogIn = findViewById(R.id.btnLogIn);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
    }

    private void logIn() {
        Log.i(TAG, String.format("grg: starting  logIn"));

        setUIEnabled(false);

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    return new ServiceProxyManager().authenticate(strings[0], strings[1]);
                } catch (Exception e) {
                    Log.e(TAG, String.format("Login for username '%s'", strings[0]), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != null) {
                    CanteenManagerApplication1.getInstance().setAuthenticationToken(s);

                    // der aufrufer kann das auswerten
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    edtPassword.setText(null);
                    setUIEnabled(true);
                    Toast.makeText(LoginActivity.this, getString(R.string.msg_LoginNotSuccessful), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(edtUserName.getText().toString(), edtPassword.getText().toString());
    }

    private void setUIEnabled(boolean enabled) {
        edtPassword.setEnabled(enabled);
        edtUserName.setEnabled(enabled);
        btnLogIn.setEnabled(enabled);
    }
}

