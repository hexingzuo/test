package com.example.ts.musicdemo;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.Utils.MusicConstant;

public class MainActivity extends AppCompatActivity {

    private List<Song> list;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MusicAdapter adapter;
    public static final int STARTPLAY = 1;
    public int s = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.activity_main);

        if (actionBar != null){
            actionBar.hide();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else {
            initView();
        }
        Intent in = new Intent(MainActivity.this, MyService.class);
        startService(in);
    }

    private void initView() {
        list = new ArrayList<>();
        //把扫描到的音乐赋值给list
        list = MusicMedium.getMusicList(this);
        recyclerView = (RecyclerView) findViewById(R.id.music_list_item1);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MusicAdapter(list);
        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                view.setBackgroundColor(Color.rgb(255,176,119));
                MusicAdapter.mSelect = position;

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list",(Serializable)list);
                intent.putExtras(bundle);
                intent.putExtra("path", list.get(position).getPath());
                intent.putExtra("position", position);
                intent.putExtra("MSG", STARTPLAY);
                intent.setClass(MainActivity.this, MusicService.class);
                startService(intent);

                intent.setClass(MainActivity.this, SongDetailActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initView();
                }
                else {
                    Toast.makeText(MainActivity.this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
