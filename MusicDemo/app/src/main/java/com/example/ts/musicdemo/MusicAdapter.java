package com.example.ts.musicdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> implements View.OnClickListener {

    public static int mSelect = -1;
    private List<Song> musiclist;
    private LocalBroadcastManager localBroadcastManager;
    private changeItemBroadcastReceiver broadcastReceiver;
    public static int is_pause;

    static class ViewHolder extends RecyclerView.ViewHolder{

        View musicView;
        TextView song_name;
        TextView singer;
        TextView time_num;
        ImageView playin;

        public ViewHolder(View view){
            super(view);
            song_name = (TextView) view.findViewById(R.id.song_name);
            singer = (TextView) view.findViewById(R.id.singer);
            time_num = (TextView) view.findViewById(R.id.song_time);
            playin = (ImageView) view.findViewById(R.id.playing);
            playin.setVisibility(View.GONE);

            musicView = view;
        }
    }

    private OnItemClickListener mOnItemClickListener = null;

    //define interface RecyclerView添加onItemClick
    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }


    public MusicAdapter(List<Song> list){
        musiclist = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        //RecyclerView添加onItemClick
        view.setOnClickListener(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(parent.getContext());
        broadcastReceiver = new changeItemBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("changeItem");
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);
        return holder;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int)v.getTag());
        }
    }

    //RecyclerView添加onItemClick
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Song song = musiclist.get(position);
        holder.musicView.setTag(position);
        holder.song_name.setText(song.getSong_name());
        holder.singer.setText(song.getSinger());
        holder.time_num.setText(song.getTime_num());

        if (mSelect == position) {
            holder.playin.setVisibility(View.VISIBLE);
            holder.song_name.setTextSize(20);
            holder.song_name.setTextColor(Color.rgb(0,0,200));
        }
        else {
            holder.playin.setVisibility(View.GONE);
            holder.song_name.setTextSize(16);
            holder.song_name.setTextColor(Color.rgb(0,0,0));
        }

        //RecyclerView添加onItemClick
        holder.musicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.musicView, position);
                    notifyItemRangeChanged(0, musiclist.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == musiclist ? 0 : musiclist.size();
    }

    class changeItemBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int pos = intent.getIntExtra("position", -1);
            int start = mSelect;
            notifyItemChanged(start-1);
            mSelect = pos;
            notifyItemChanged(mSelect);
            //notifyItemRangeChanged(start-1, (mSelect-start+2));
        }
    }
}
