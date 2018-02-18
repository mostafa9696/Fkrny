package com.example.mostafahussien.fkrny.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.example.mostafahussien.fkrny.Activities.SnoozeDialogActivity;
import com.example.mostafahussien.fkrny.utils.AlarmUtil;

public class SnoozeActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra("NOTIFICATION_ID", 0);

        // Close notification tray
        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeIntent);
        Intent snoozeIntent = new Intent(context, SnoozeDialogActivity.class);
        snoozeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        snoozeIntent.putExtra("NOTIFICATION_ID", reminderId);
        context.startActivity(snoozeIntent);
    }
}