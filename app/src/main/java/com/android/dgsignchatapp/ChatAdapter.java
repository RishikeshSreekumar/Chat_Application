package com.android.dgsignchatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    private ArrayList<Chat> listdata;
    private Context context;
    private SharedPreferences sharedPreferences;

    public ChatAdapter(ArrayList<Chat> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("digisign.data", Context.MODE_PRIVATE);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_chat, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_name.setText(listdata.get(position).getName());
        holder.tv_message.setText(listdata.get(position).getMessage());
        holder.tv_demo.setText(listdata.get(position).getSignedMessage());
        if(sharedPreferences.getBoolean("demo", false))
            holder.tv_demo.setVisibility(View.VISIBLE);
        else
            holder.tv_demo.setVisibility(View.GONE);
        if(listdata.get(position).isAlert()){
            holder.iv_alert.setVisibility(View.VISIBLE);
        }else{
            holder.iv_alert.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_message, tv_demo;
        public ImageView iv_alert;
        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_message = (TextView) itemView.findViewById(R.id.tv_message);
            this.tv_demo = (TextView) itemView.findViewById(R.id.tv_demo);
            this.iv_alert = itemView.findViewById(R.id.iv_alert);
        }
    }
}
