package com.example.mostafahussien.fkrny.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mostafahussien.fkrny.utils.AlarmUtil;
import com.example.mostafahussien.fkrny.utils.NotificationUtil;

public class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int ID = intent.getIntExtra("NOTIFICATION_ID", 0);
        Log.d("ff2", String.valueOf(ID));
        NotificationUtil.cancelNotification(context,ID);
    }
}
