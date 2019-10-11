package dev.lukeb.todolist.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

//import static dev.lukeb.todolist.view.ListActivity.dialog;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: change!!");

        // Notify user of the status of network connection
        Toast toast;
        if (isOnline(context)) {
            toast = Toast.makeText(context, "You are connected to the Internet!",Toast.LENGTH_LONG);

        } else {
            toast = Toast.makeText(context, "OH NO, You have lost connection to the Internet...",Toast.LENGTH_LONG);
        }

        // Display in center of screen
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    // Method that returns if the device is connected or not
    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        // Check null because in airplane mode it will be null
        return (networkInfo != null && networkInfo.isConnected());

    }
}