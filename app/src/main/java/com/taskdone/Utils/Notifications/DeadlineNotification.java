package com.taskdone.Utils.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat.Builder;

import com.taskdone.MainActivity;
import com.taskdone.R;

public class DeadlineNotification extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(intent.getExtras().getInt("id"), new Builder(context, notificationManager.getNotificationChannel("canal_id").getId())
                        .setSmallIcon(R.drawable.ic_logo_notification)
                        .setSound(RingtoneManager.getDefaultUri(2))
                        .setContentTitle("Time is up!")
                        .setContentIntent(pendingIntent)
                        .setContentText(intent.getExtras().getString("text"))
                        .build());
    }
}
