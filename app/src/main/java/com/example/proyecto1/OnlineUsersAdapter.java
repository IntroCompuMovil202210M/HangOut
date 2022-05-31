package com.example.proyecto1;

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

import java.util.ArrayList;

public class OnlineUsersAdapter extends ArrayAdapter<MyUser> {

    private  Context mContext;
    int mResource;

    public OnlineUsersAdapter(Context context, int resource, ArrayList<MyUser> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = capitalizeString(getItem(position).getName());
        String lastName = capitalizeString(getItem(position).getLastName());

        String uuId = getItem(position).getID();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameOnlineUser = (TextView) convertView.findViewById(R.id.msgChat);
        ImageButton btnChat = (ImageButton) convertView.findViewById(R.id.chat_btn);

        nameOnlineUser.setText(name+" "+lastName);

        //Start chat activity
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Chat", "Chat with "+uuId);
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("user", uuId);
                view.getContext().startActivity(intent);
            }
        });

        return convertView;
    }
    private String capitalizeString(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}
