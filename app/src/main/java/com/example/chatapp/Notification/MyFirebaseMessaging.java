package com.example.chatapp.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.example.chatapp.Messages;
import com.example.chatapp.MessagesGroup;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert sented != null;

        if(firebaseUser != null && sented.equals(firebaseUser.getUid())){

            sendNotification(remoteMessage);

        }

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");

        String body = remoteMessage.getData().get("body");

        String icon = remoteMessage.getData().get("icon");

        String title = remoteMessage.getData().get("title");

        String group = remoteMessage.getData().get("group");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        int j = Integer.parseInt(user.replaceAll("[\\D]",""));


        Intent intent;

        Bundle bundle;

        if(group!=null){

           intent = new Intent(this, MessagesGroup.class);

            bundle = new Bundle();

            bundle.putString("groupid",group);

        }else{

            intent = new Intent(this, Messages.class);

            bundle = new Bundle();

            bundle.putString("userid",user);

        }




        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        int i =0;

        if(j>0){

            i=j;
        }

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        noti.notify(m,builder.build());



    }
}
