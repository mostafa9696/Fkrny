package com.example.mostafahussien.fkrny.Receiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.TaskDatabase;
import com.example.mostafahussien.fkrny.utils.AlarmUtil;
import com.example.mostafahussien.fkrny.utils.NotificationUtil;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id=intent.getIntExtra("NOTIFICATION_ID", 0);
        TaskDatabase database=TaskDatabase.getInstance(context);
        Reminder reminder=database.getNotification(id);
        reminder.setNumberShown(reminder.getNumberShown() + 1);     // make this hnotification as InActive and replace with new type
        database.addNewTask(reminder);
        NotificationUtil.createNotification(context,reminder);
        if (reminder.getNumberToShow() > reminder.getNumberShown() ) {
            AlarmUtil.setNextAlarm(context, reminder, database);
        }
        Intent updateIntent = new Intent("BROADCAST_REFRESH");          // BROADCAST_REFRESH is an action to define broadcast to make operation on activities when receive the alarm
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent);
        database.close();
    }
}
