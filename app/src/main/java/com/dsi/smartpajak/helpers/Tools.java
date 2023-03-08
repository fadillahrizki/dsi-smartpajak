package com.dsi.smartpajak.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.TypedValue;

public class Tools {
    public static boolean networkChecker(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            return (activeNetworkInfo != null && (activeNetworkInfo.isConnected() || activeNetworkInfo.isConnectedOrConnecting()));
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public static String getDeviceSerial() {
        return Build.SERIAL + "";
    }

    public static String getDeviceOS() {
        return Build.VERSION.RELEASE + "";
    }
}