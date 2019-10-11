package dev.lukeb.todolist;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

/*
    * This class creates the notification channel on startup of the Application
    * Also serves as the place to
 */
public class App extends Application {

    private NotificationManager notificationManager;
    public static final String TODO_LIST_NOTIFICATION_CHANNEL_ID = "todolist_channel_main";

    @Override
    public void onCreate(){
        super.onCreate();

        this.notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannels();
    }

    public void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(TODO_LIST_NOTIFICATION_CHANNEL_ID, "Channel Main", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("This is channel 1");

            this.notificationManager.createNotificationChannel(channel1);
        } else {
            throw new RuntimeException("Update your phone please");
        }
    }
}
