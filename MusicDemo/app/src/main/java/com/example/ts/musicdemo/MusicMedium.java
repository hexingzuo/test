package com.example.ts.musicdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class MusicMedium {

    public static List<Song> getMusicList(Context context){

        List<Song> music_list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);

        if (cursor != null){
            while (cursor.moveToNext()){
                Song song = new Song();
                song.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                song.setSong_name(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                String num = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                song.setTime_num(changeTimeNum(Integer.parseInt((num))));
                music_list.add(song);

                //歌曲文件格式不规范
                if (song.getSize() > 1000*800){
                    if (song.getSong_name().contains("-")){
                        String[] str = song.getSong_name().split("-");
                        song.setSinger(str[0]);
                        song.setSong_name(str[1]);
                    }
                }
            }
        }
        return music_list;
    }

    public static String changeTimeNum(int timenum){
        StringBuilder a = new StringBuilder();
        int fen = timenum / 60000;
        int miao = timenum % 60000 / 1000;
        if (fen < 10){
            a.append(0);
        }
        a.append(fen);
        a.append(":");
        if (miao < 10){
            a.append("0");

        }
        a.append(miao);
        return a.toString();
    }
}
