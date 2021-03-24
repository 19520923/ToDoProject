package com.example.todoproject.Utility;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.util.Log;

import com.example.todoproject.R;
import com.example.todoproject.Reminder.ReminderActivity;

import java.util.UUID;

public class TodoNotidicationService extends IntentService {
    public static final String TODOTEXT = "com.todoproject.todonotificationservicetext";
    public static final String TODOUUID = "com.todoproject.todonotificationserviceuuid";
    private String mToDoText;
    private UUID mToDoUUID;
    private Context context;

    public TodoNotidicationService(){super("TodoNotificationService");}

    @Override
    public void onHandleIntent(Intent intent){
        mToDoText = intent.getStringExtra(TODOTEXT);
        mToDoUUID = (UUID) intent.getSerializableExtra(TODOUUID);

        Log.d("Group11","onHandleIntent called");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent i = new Intent(this, ReminderActivity.class);
        i.putExtra(TodoNotidicationService.TODOUUID,mToDoUUID);
        Intent deleteIntent = new Intent(this, DeleteNotificationService.class);
        deleteIntent.putExtra(TODOUUID,mToDoUUID);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(mToDoText)
                .setSmallIcon(R.drawable.ic_done_white_24dp).setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND).setDeleteIntent(PendingIntent.getService(this,mToDoText.hashCode(),deleteIntent,PendingIntent.FLAG_UPDATE_CURRENT))
                .setContentIntent(PendingIntent.getActivity(this, mToDoText.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        manager.notify(100,notification);
    }
}
