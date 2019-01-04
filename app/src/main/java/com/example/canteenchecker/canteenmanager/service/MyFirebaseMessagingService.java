package com.example.canteenchecker.canteenmanager.service;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getName();

    private static final String REMOTE_MESSAGE_TYPE_KEY = "type";
    private static final String REMOTE_MESSAGE_TYPE_VALUE = "canteenDataChanged";

    private static final String RATINGS_CHANGED_INTENT_ACTION = "CanteenChangedAction";

    public static IntentFilter updatedRatingsMessage() {
        return new IntentFilter(RATINGS_CHANGED_INTENT_ACTION);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "Firebase Manger  new Message received.   remoteMessage 111  " + remoteMessage.getData().get(REMOTE_MESSAGE_TYPE_KEY));
        Log.i(TAG, "Firebase Manger  new Message received.   remoteMessage.getMessageType  " + remoteMessage.getMessageType());

        Map<String, String> data = remoteMessage.getData();
        data.forEach((k, v) -> {
            Log.i(TAG, "Firebase   data         k = " + k + " + value = " + v);
        });

        if (REMOTE_MESSAGE_TYPE_VALUE.equals(data.get(REMOTE_MESSAGE_TYPE_KEY))) {
            Intent intent = new Intent(RATINGS_CHANGED_INTENT_ACTION);
            Log.i(TAG, "Firebase Manger  sending Intent ");

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}

