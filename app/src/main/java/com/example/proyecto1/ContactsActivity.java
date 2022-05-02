package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsActivity extends AppCompatActivity {

    public static final String CONTACTS_PERMISSION_NAME = Manifest.permission.READ_CONTACTS;
    public static final int CONTACTS_PERMISSION_ID = 5;

    String [] mProjection;
    Cursor mCursor;
    ContactsAdapter mContactsAdapter;
    ListView mlista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mlista = findViewById(R.id.list_contactos);
        mProjection = new String[]{
                ContactsContract.Profile._ID,
                ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
        };

        mContactsAdapter = new ContactsAdapter(this, null, 0);
        mlista.setAdapter(mContactsAdapter);
        requestPermission(this, CONTACTS_PERMISSION_NAME, "¿Permite tener acceso a sus contactos?", CONTACTS_PERMISSION_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView(){
        if(ContextCompat.checkSelfPermission(this, CONTACTS_PERMISSION_NAME) == PackageManager.PERMISSION_GRANTED){
            mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, mProjection, null, null, null);
            mContactsAdapter.changeCursor(mCursor);
        }
    }

    private void requestPermission(Activity context, String permission, String justification, int id){
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)){
                Toast.makeText(context, justification, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CONTACTS_PERMISSION_ID){
            initView();
        }
    }
}