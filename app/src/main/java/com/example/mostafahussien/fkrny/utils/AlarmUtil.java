package com.example.mostafahussien.fkrny.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.Receiver.AlarmReceiver;
import com.example.mostafahussien.fkrny.TaskDatabase;

import java.util.Calendar;

public class AlarmUtil {

    public static void setAlarm(Context context, Intent alarmReciever, int notificationId, Calendar calendar) {
           alarmReciever.putExtra("NOTIFICATION_ID",notificationId);
        Log.d("ff2", String.valueOf(notificationId));
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,notificationId,alarmReciever,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        Log.d("ff3", String.valueOf(notificationId));
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public static void setNextAlarm(Context context, Reminder reminder, TaskDatabase database) {
        Calendar calendar = DateTimeUtil.parseDateAndTime(reminder.getDateAndTime());   // current time
        calendar.set(Calendar.SECOND, 0);
        int type=reminder.getRepeatType();
        // Interval always be = 1  ,, in original app interval can be 2,3,... in advanced mode which mean after 2,3,.... hours or month or ...
        if(type== Reminder.HOURLY)
                calendar.add(Calendar.HOUR, reminder.getInterval());        // after 1 hour(interval) from now
        if(type== Reminder.DAILY)
                calendar.add(Calendar.DATE, reminder.getInterval());    // after 1 day from current day
        if(type== Reminder.WEEKLY)
            calendar.add(Calendar.WEEK_OF_YEAR, reminder.getInterval());
        if(type== Reminder.MONTHLY)
                calendar.add(Calendar.MONTH, reminder.getInterval());
        if(type== Reminder.YEARLY)
            calendar.add(Calendar.YEAR, reminder.getInterval());
        reminder.setDateAndTime(DateTimeUtil.toStringDateAndTime(calendar));     // set new alarm for next repeat
        database.addNewTask(reminder);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        setAlarm(context, alarmIntent, reminder.getId(), calendar);
    }
}
