package com.example.mostafahussien.fkrny.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mostafahussien.fkrny.Activities.ViewTask;
import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.R;
import com.example.mostafahussien.fkrny.Receiver.DismissReceiver;
import com.example.mostafahussien.fkrny.Receiver.SnoozeActionReceiver;
import com.example.mostafahussien.fkrny.Receiver.SnoozeReceiver;


public class NotificationUtil {
    // Create intent for notification onClick behaviour
    public static void createNotification(Context context, Reminder reminder){
    Intent intent=new Intent(context,ViewTask.class);
        intent.putExtra("NOTIFICATION_ID", reminder.getId());
        intent.putExtra("NOTIFICATION_DISMISS", true);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,reminder.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Create intent for notification snooze click behaviour
    Intent snoozeIntent=new Intent(context, SnoozeActionReceiver.class);
    snoozeIntent.putExtra("NOTIFICATION_ID", reminder.getId());
    PendingIntent pendingSnooze=PendingIntent.getBroadcast(context,reminder.getId(),snoozeIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // create notification
    int taskIcon=context.getResources().getIdentifier(reminder.getIcon(),"drawable",context.getPackageName());
       NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context)
                .setColor(Color.parseColor(reminder.getColour()))
                .setContentText(reminder.getContent())
                .setContentTitle(reminder.getTitle())
                .setSmallIcon(taskIcon)
                .setTicker(reminder.getTitle())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(reminder.getContent()));
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        String taskRing=preferences.getString("NotificationSound", "content://settings/system/notification_sound");
        if(taskRing.length()!=0) {
            builder.setSound(Uri.parse(taskRing));
        }
        if(preferences.getBoolean("checkBoxLED", true)){
            builder.setLights(Color.RED,700,1500);
        }
        if (preferences.getBoolean("checkBoxOngoing", false)) {
            builder.setOngoing(true);
        }
        if (preferences.getBoolean("checkBoxVibrate", true)) {
            long[] v = {0, 300, 0};
            builder.setVibrate(v);
        } if (preferences.getBoolean("checkBoxMarkAsDone", false)) {
            Intent disIntent = new Intent(context, DismissReceiver.class);
            intent.putExtra("NOTIFICATION_ID", reminder.getId());
            PendingIntent disPending = PendingIntent.getBroadcast(context, reminder.getId(), disIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_done_white_24dp, "Task is Done", disPending);
        }
        if (preferences.getBoolean("checkBoxSnooze", false)) {
            builder.addAction(R.drawable.ic_snooze_white_24dp,"Snooze",pendingSnooze);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(reminder.getId(), builder.build());
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}
