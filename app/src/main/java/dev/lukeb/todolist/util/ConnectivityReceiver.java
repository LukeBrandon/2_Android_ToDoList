package dev.lukeb.todolist.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

//import static dev.lukeb.todolist.ListActivity.dialog;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: change!!");

        try {
            if (isOnline(context)) {
               // dialog(true);
                Toast.makeText(context, "You are connected to the Internet!",Toast.LENGTH_LONG).show();
                Log.e("onReceive:", "Online Connect Intenet ");
            } else {
                //dialog(false);
                Toast.makeText(context, "OH NO, You have lost connection to the Internet...",Toast.LENGTH_LONG).show();
                Log.e("onReceive:", "Conectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}