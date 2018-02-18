package com.example.mostafahussien.fkrny.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mostafahussien.fkrny.Model.Reminder;
import com.example.mostafahussien.fkrny.TaskDatabase;
import com.example.mostafahussien.fkrny.utils.AlarmUtil;
import com.example.mostafahussien.fkrny.utils.DateTimeUtil;

import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver{    // to restore tasks alarm if user restart his phone
    @Override
    public void onReceive(Context context, Intent intent) {
        TaskDatabase database=TaskDatabase.getInstance(context);
        List<Reminder>reminders=database.getTasks(Reminder.ACTIVE);
        database.close();
        Intent alarmIntent=new Intent(context,AlarmReceiver.class);
        for(Reminder r:reminders){
            Calendar calendar= DateTimeUtil.parseDateAndTime(r.getDateAndTime());
            calendar.set(Calendar.SECOND,0);
            AlarmUtil.setAlarm(context,alarmIntent,r.getId(),calendar);
        }
    }
}
