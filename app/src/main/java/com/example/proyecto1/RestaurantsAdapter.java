package com.example.proyecto1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsAdapter extends ArrayAdapter<Restaurant> {

    private Context mContext;
    int mResource;

    public RestaurantsAdapter(Context context, int resource, ArrayList<Restaurant> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String address = getItem(position).getDir();
        String rating = getItem(position).getRating();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.restaurantName);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.restaurantAddress);
        TextView tvRating = (TextView) convertView.findViewById(R.id.restaurantRating);

        tvName.setText(name);
        tvAddress.setText(address);
        tvRating.setText(rating);

        return convertView;
    }
}
