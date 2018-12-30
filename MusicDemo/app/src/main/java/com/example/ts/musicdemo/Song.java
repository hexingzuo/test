package com.example.ts.musicdemo;

import java.io.Serializable;


public class Song implements Serializable {
    private int id;
    private String song_name;
    private String singer;
    private String path;
    private String time_num;
    private long size;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSong_name(){
        return this.song_name;
    }

    public String getSinger(){
        return this.singer;
    }

    public String getPath(){
        return this.path;
    }

    public String getTime_num() {
        return time_num;
    }

    public long getSize() {
        return size;
    }

    public void setSong_name(String name){
        this.song_name = name;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setTime_num(String time_num) {
        this.time_num = time_num;
    }

    @Override
    public String toString() {
        return "name = " + this.song_name;
    }
}
