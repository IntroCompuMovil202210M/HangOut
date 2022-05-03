package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

public class MostrarRestaurantesActivity extends AppCompatActivity {

    String [] mProjection;
    Cursor mCursor;
    RestaurantsAdapter mRestaurantsAdapter;
    ListView mlista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_restaurantes);

        mlista.setAdapter(mRestaurantsAdapter);

        /*mCursor = getContentResolver().query(, mProjection, null, null, null);
        mRestaurantsAdapter.changeCursor(mCursor);*/
    }
}