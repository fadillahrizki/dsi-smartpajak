package com.dsi.smartpajak.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "SMARTPAJAK";

    public CacheManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
    }

    public void setLogin(boolean login) {
        sharedPreferences.edit().putBoolean("logged_in", login).apply();
    }

    public void setUser(String user) {
        sharedPreferences.edit().putString("user", user).apply();
    }

    public void setFCMToken(String token) {
        sharedPreferences.edit().putString("fcm_token", token).apply();
    }

    public void setFCMTokenRemoved(String removed) {
        sharedPreferences.edit().putString("fcm_token_removed", removed).apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("logged_in", false);
    }

    public String getUser() {
        return sharedPreferences.getString("user", null);
    }

    public String getFCMToken() {
        return sharedPreferences.getString("fcm_token", null);
    }

    public String getFCMTokenRemoved() {
        return sharedPreferences.getString("fcm_token_removed", null);
    }
}
