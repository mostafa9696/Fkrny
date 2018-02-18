package com.example.mostafahussien.fkrny.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.TaskDatabase;
import com.example.mostafahussien.fkrny.utils.NotificationUtil;

/**
 * Created by Mostafa Hussien on 13/09/2017.
 */

public class SnoozeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TaskDatabase database=TaskDatabase.getInstance(context);
        int ID= intent.getIntExtra("NOTIFICATION_ID", 0);
        if (ID != 0 && database.isTaskPresent(ID)) {
            Reminder reminder = database.getNotification(ID);
            NotificationUtil.createNotification(context, reminder);
        }
        database.close();
    }
}
