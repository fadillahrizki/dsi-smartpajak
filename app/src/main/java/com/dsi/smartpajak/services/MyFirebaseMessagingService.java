package com.dsi.smartpajak.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dsi.smartpajak.MainPADActivity;
import com.dsi.smartpajak.helpers.CacheManager;
import com.dsi.smartpajak.helpers.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String s) {
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {
        CacheManager cacheManager = new CacheManager(getApplicationContext());
        cacheManager.setFCMToken(token);
        cacheManager.setFCMTokenRemoved("no");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if ( remoteMessage == null )
            return;

        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e("FCM","" + e);
            }
        }
    }

    private void handleNotification(String message) {
        Intent pushNotification = new Intent("push_notification");
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();
    }

    private void handleDataMessage(JSONObject json) {
        try {
            CacheManager cacheManager = new CacheManager(getApplicationContext());

            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");
            String type = payload.getString("type");

            if (type.equals("skpd")) {
                if (cacheManager.isLoggedIn()) {
                    if ( ! NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        Intent pushNotification = new Intent("reload_skpd");
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    }

                    Intent resultIntent = new Intent(getApplicationContext(), MainPADActivity.class);
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                }
            }
        } catch (JSONException e) {
            Log.e("FCM","" + e);
        } catch (Exception e) {
            Log.e("FCM","" + e);
        }
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }
}