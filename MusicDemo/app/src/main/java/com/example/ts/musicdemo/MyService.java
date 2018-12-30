package com.example.ts.musicdemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class MyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("MyService","ahdfkakshf");
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("MusicDemo")
                .setContentText("正在运行")
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.music))
                .setContentIntent(pi)
                .build();
        startForeground(1,notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
