package com.example.classcomplain;

import android.app.Service;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.support.v4.app.ShareCompat.getCallingActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String label="Feedback Notification";
    String d= "as126"; ///////notificATION ID
    Calendar c;
    private SimpleDateFormat dateFormatter,fortime;

    public void onMessageReceived(RemoteMessage remoteMessage) {


        c = Calendar.getInstance();
        //d=remoteMessage.getNotification().getTitle();
        //if (d.equals(""))
        //  d="as126";
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy ", Locale.US);
        fortime = new SimpleDateFormat("HH:mm:ss", Locale.US);

        DatabaseReference ref_notification = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Notifications/");
        DatabaseReference newref = ref_notification.push();

        ref_notification.child(newref.getKey()).child("Date").setValue(dateFormatter.format(c.getTime()));
        ref_notification.child(newref.getKey()).child("Time").setValue(fortime.format(c.getTime()));
        ref_notification.child(newref.getKey()).child("Message").setValue(remoteMessage.getNotification().getBody());
        ref_notification.child(newref.getKey()).child("Read").setValue("0");
        showNotification(remoteMessage.getNotification().getBody());
    }

    private void showNotification(String message) {

        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Intent activityIntent = new Intent(MyFirebaseMessagingService.this, notificationactivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this,0,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(label)
                .setContentText(message)
                .setSmallIcon(R.drawable.notificationlogo)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(sound);

        manager.notify(0,builder.build());
    }
}