package com.taskdone.Utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat.Builder;

import com.taskdone.R;

public class ReminderNotification extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ((NotificationManager) context.getSystemService("notification")).notify(55, new Builder(context).setSmallIcon(R.drawable.ic_logo_notification).setSound(RingtoneManager.getDefaultUri(2)).setContentTitle("Reminder").setContentText(intent.getExtras().getString("text")).build());
    }
}
