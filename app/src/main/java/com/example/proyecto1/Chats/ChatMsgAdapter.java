package com.example.proyecto1.Chats;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proyecto1.ChatActivity;
import com.example.proyecto1.R;

import java.util.ArrayList;

public class ChatMsgAdapter extends ArrayAdapter<Mensaje> {

    private  Context mContext;
    int mResource;
    private String uuId;

    public ChatMsgAdapter(Context context, int resource, ArrayList<Mensaje> objects, String uuId) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        this.uuId = uuId;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView msgChat = (TextView) convertView.findViewById(R.id.msgChat);

        String msg = getItem(position).getMensaje();
        if(msg!=null)
        {
            if(getItem(position).getEmisor().equals(uuId)) {
                msgChat.setBackgroundColor(Color.BLUE);
                msgChat.setTextColor(Color.WHITE);
                //Log.i("Chat", "Chat with emisor "+uuId);
            } else {
                msgChat.setBackgroundColor(Color.GREEN);
                //Log.i("Chat", "Chat with receptor"+uuId);
                msgChat.setTextColor(Color.WHITE);
            }
            msgChat.setText(msg);
        }

        return convertView;
    }
    private String capitalizeString(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}
