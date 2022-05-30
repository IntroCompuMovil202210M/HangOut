package com.example.proyecto1;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
        byte[] byteArray = getItem(position).getByteArray();
        String id = getItem(position).getId();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.restaurantName);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.restaurantAddress);
        TextView tvRating = (TextView) convertView.findViewById(R.id.restaurantRating);

        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.restaurantIcon);

        tvName.setText(name);
        tvAddress.setText(address);
        tvRating.setText(rating);


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("restaurant/"+id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(ivIcon);
            }
        });

        return convertView;
    }
}
