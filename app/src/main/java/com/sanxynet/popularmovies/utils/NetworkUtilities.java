package com.sanxynet.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.sanxynet.popularmovies.R;
import java.util.Objects;

public class NetworkUtilities {
    private static final String TAG = NetworkUtilities.class.getSimpleName();

    /**
     * Checks if the device is connected to the internet
     *
     * @param context
     * @return true if connected, false otherwise
     */
    @SuppressWarnings("JavaDoc")
    public static boolean isDeviceConnectedToInternet(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();

        boolean isDeviceConnected = networkInfo != null &&
                networkInfo.isConnectedOrConnecting();

        if (isDeviceConnected) {
            Log.d(TAG, context.getString(R.string.device_connected));
        } else {
            Log.d(TAG, context.getString(R.string.device_not_connected));
        }

        return isDeviceConnected;
    }
}
