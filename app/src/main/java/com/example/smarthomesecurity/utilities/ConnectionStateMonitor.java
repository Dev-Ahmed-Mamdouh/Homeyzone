package com.example.smarthomesecurity.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Date;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {

    final NetworkRequest networkRequest;
    private onConnectionChangeListener delegate;
    private long loadTime = 0;

    public ConnectionStateMonitor() {
        networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
    }

    public void enable(Context context, onConnectionChangeListener listener) {
        delegate = listener;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    // Likewise, you can have a disable method that simply calls ConnectivityManager.unregisterNetworkCallback(NetworkCallback) too.

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        if (delegate != null && (loadTime == 0 || (new Date().getTime()) - loadTime > 3000)) {
            delegate.onConnectionAvailable();
            loadTime = new Date().getTime();
        }
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        if (delegate != null)
            delegate.onConnectionUnavailable();
    }

    public interface onConnectionChangeListener {

        void onConnectionAvailable();

        void onConnectionUnavailable();

    }
}