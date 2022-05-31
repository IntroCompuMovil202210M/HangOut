package com.example.proyecto1.Chats;

import android.content.Context;
import android.content.Intent;
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

    public ChatMsgAdapter(Context context, int resource, ArrayList<Mensaje> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String msg = capitalizeString(getItem(position).getMensaje());

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView msgChat = (TextView) convertView.findViewById(R.id.msgChat);

        msgChat.setText(msg);

        return convertView;
    }
    private String capitalizeString(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}
