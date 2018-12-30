package com.example.ts.musicdemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {

    public static MediaPlayer mediaPlayer;
    public static String oldpath = null;
    private static int index = -1;
    private boolean isplaying = false;
    private static int is_pause = -1;

    private List<Song> list;
    private LocalBroadcastManager localBroadcastManager;
    private ChangeStartBroadcastReceiver broadcastReceiver;
    private StateBroadcastReceiver stateBroadcastReceiver;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("changeDetail");
        broadcastReceiver = new ChangeStartBroadcastReceiver();
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("pause_start");
        stateBroadcastReceiver = new StateBroadcastReceiver();
        localBroadcastManager.registerReceiver(stateBroadcastReceiver, filter1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String path = intent.getStringExtra("path");
        list = (List<Song>) intent.getSerializableExtra("list");
        int position = intent.getIntExtra("position", -1);
        index = position;
        play(path, list);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        localBroadcastManager.unregisterReceiver(stateBroadcastReceiver);
        mediaPlayer.release();
    }

    public void play(String path, final List<Song> list) {

        if (!isplaying || (isplaying && !path.equals(oldpath))) {
            if ((isplaying && !path.equals(oldpath))) {
                    mediaPlayer.reset();
            }
            initMediaPlayer(path);
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (index != -1) {
                    if (index < list.size()-1) {
                        Song song = list.get(++index);
                        MusicAdapter.mSelect = index;
                        mediaPlayer.reset();
                        initMediaPlayer(song.getPath());
                    }
                    if (index == list.size()-1){
                        index = 0;
                        Song song = list.get(index);
                        MusicAdapter.mSelect = index;
                        mediaPlayer.reset();
                        initMediaPlayer(song.getPath());
                    }
                    Intent toAdapter = new Intent("changeItem");
                    toAdapter.putExtra("position", index);
                    Log.d("MusicService", "index = " + index);
                    localBroadcastManager.sendBroadcast(toAdapter);
                }
            }
        });
    }

    private void initMediaPlayer(String path) {

        try {
            oldpath = path;
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //在点击上一首或下一首之前点击了暂停，则只改变当前页面显示歌曲信息，
                    // mediaplayer加载不播放
                    if (is_pause == 1) {
                    }
                    else {
                        mp.start();
                        isplaying = true;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //changeDetail
    class ChangeStartBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Song song = (Song) intent.getSerializableExtra("song");
            int pos = intent.getIntExtra("playingPosition", -1);
            mediaPlayer.reset();
            index = pos;
            is_pause = intent.getIntExtra("is_pause", -1);
            play(song.getPath(), list);
            Intent toAdapter = new Intent("changeItem");
            toAdapter.putExtra("position", index);
            toAdapter.putExtra("is_pause", is_pause);
            Log.d("MusicService", "index = " + index);
            localBroadcastManager.sendBroadcast(toAdapter);
        }
    }

    class StateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String operate = intent.getStringExtra("operation");
            if (operate.equals("暂停")) {
                mediaPlayer.pause();
                is_pause = 1;
            }
            else if (operate.equals("播放")) {
                mediaPlayer.start();
                is_pause = 0;
            }
        }
    }
}
