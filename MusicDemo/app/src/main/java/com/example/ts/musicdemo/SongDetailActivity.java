package com.example.ts.musicdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SongDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Song> list;
    private int position;
    private TextView song_name;
    private TextView singer;

    private static final int PREVIOUS_NEXT = -1;
    private static final int PAUSE_START = 0;
    private static final int INIT = 1;
    private static final int change = 2;
    private static int is_pause = 0;

    private SongDetailBroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private SongChangeBroadcastReceiver changeBroadcastReceiver;
    private ChangeStateBroadcastReceiver stateBroadcastReceiver;

    private Button btn_pre;
    private Button btn_next;
    private Button pause_start;
    private TextView back;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Song song = null;
            switch (msg.what) {
                case change:
                    int pos = msg.arg1;
                    if (is_pause == 0){
                        pause_start.setText("暂停");
                    }
                    song = list.get(pos);
                    song_name.setText(song.getSong_name());
                    singer.setText(song.getSinger());
                    break;
                case PREVIOUS_NEXT:
                    if (is_pause == 0){
                        pause_start.setText("暂停");
                    }
                    song = (Song) msg.obj;
                    song_name.setText(song.getSong_name());
                    singer.setText(song.getSinger());
                    break;
                case PAUSE_START:
                    String text = pause_start.getText().toString().equals("暂停")? "播放":"暂停";
                    pause_start.setText(text);
                    break;
                case INIT:
                    pause_start.setText("播放");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_detail_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        song_name = (TextView) findViewById(R.id.detail_song_name);
        singer = (TextView) findViewById(R.id.detail_singer);

        Intent intent = getIntent();

        list = (List<Song>) intent.getSerializableExtra("list");
        position = intent.getIntExtra("position", -1);
        Song song = list.get(position);


        Log.d("Songdetail", "is_pause = " + intent.getIntExtra("is_pause", -1));
        Log.d("SongDetail", "asfsgerf");
        if (is_pause == 1){
            Log.d("Songdetail", is_pause+"");
            Message msg = new Message();
            msg.what = INIT;
            handler.sendMessage(msg);
        }

        song_name.setText(song.getSong_name());
        singer.setText(song.getSinger());

        btn_pre = (Button) findViewById(R.id.detail_previous);
        btn_next = (Button) findViewById(R.id.detail_next);
        pause_start = (Button) findViewById(R.id.pause_start);
        back = (TextView) findViewById(R.id.back_to_main);

        back.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        pause_start.setOnClickListener(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("changeDetail");
        broadcastReceiver = new SongDetailBroadcastReceiver();
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("changeItem");
        changeBroadcastReceiver = new SongChangeBroadcastReceiver();
        localBroadcastManager.registerReceiver(changeBroadcastReceiver,filter2);

        IntentFilter filter3 = new IntentFilter();
        filter3.addAction("pause_start");
        stateBroadcastReceiver = new ChangeStateBroadcastReceiver();
        localBroadcastManager.registerReceiver(stateBroadcastReceiver,filter3);
    }

    @Override
    public void onClick(View v) {
        Song song = null;
        Intent intent = null;
        Bundle bundle = null;
        switch (v.getId()) {
            case R.id.detail_previous:
                if (position < 1){
                    Toast.makeText(SongDetailActivity.this, "已经是第一首了", Toast.LENGTH_SHORT).show();
                }
                else{
                    song = list.get(--position);
                    intent = new Intent("changeDetail");
                    bundle = new Bundle();
                    bundle.putSerializable("song",song);
                    intent.putExtras(bundle);
                    intent.putExtra("playingPosition", position);
                    intent.putExtra("is_pause", is_pause);
                    localBroadcastManager.sendBroadcast(intent);
                }
                break;
            case R.id.detail_next:
                if (position == list.size()-1){
                    Toast.makeText(SongDetailActivity.this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
                }
                else{
                    song = list.get(++position);
                    intent = new Intent("changeDetail");
                    bundle = new Bundle();
                    bundle.putSerializable("song",song);
                    intent.putExtras(bundle);
                    intent.putExtra("playingPosition", position);
                    intent.putExtra("is_pause", is_pause);
                    localBroadcastManager.sendBroadcast(intent);
                }
                break;
            case R.id.pause_start:
                is_pause = is_pause == 0?1:0 ;
                intent = new Intent("pause_start");
                if (is_pause == 1){
                    intent.putExtra("operation", "暂停");
                }else {
                    intent.putExtra("operation", "播放");
                }
                localBroadcastManager.sendBroadcast(intent);
                break;
            case R.id.back_to_main:
                Log.d("SongDetailActivity", "clicked back to main");
                /*Intent tomain = new Intent(SongDetailActivity.this, MainActivity.class);
                startActivity(tomain);*/
                this.finish();
                break;
            default:
                break;
        }
    }

    //changeDetail
    class SongDetailBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Song song = (Song) intent.getSerializableExtra("song");
            Message message = new Message();
            message.what = PREVIOUS_NEXT;
            message.obj = song;
            handler.sendMessage(message);
        }
    }

    //接收：一首播放完后，mediaplayer继续播下一首时发送的广播
    //changeItem
    class SongChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int pos = intent.getIntExtra("position", -1);
            Message message = new Message();
            message.what = change;
            message.arg1 = pos;
            handler.sendMessage(message);
        }
    }

    //pause_start
    class ChangeStateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Message message = new Message();
            message.what = PAUSE_START;
            handler.sendMessage(message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
