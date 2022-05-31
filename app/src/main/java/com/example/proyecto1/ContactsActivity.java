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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {


    public static final String PATH_USERS = "users/";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private String uuId;

    String[] mProjection;
    Cursor mCursor;
    OnlineUsersAdapter mContactsAdapter;
    ListView mlista;

    //List of users
    public static ArrayList<MyUser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //Inflate
        mlista = findViewById(R.id.list_contactos);


        users = new ArrayList<>();

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        uuId = mAuth.getCurrentUser().getUid().toString();
        //Database
        database = FirebaseDatabase.getInstance();
        //Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();
        getOnlineUsers();
    }

    private void getOnlineUsers() {

        myRef = database.getReference(PATH_USERS);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<MyUser> onlineUsers;
                onlineUsers = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    MyUser user = singleSnapshot.getValue(MyUser.class);
                    //if the user is online and is not the user that is logged in
                    if (user.getID() != uuId) {
                        onlineUsers.add(user);
                        users.add(user);
                        Log.i("Chat", "Usuario: " + user.getID());
                    }
                }
                users = onlineUsers;
                updateList(onlineUsers);
                startListenerListOnlineUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DATABASE", "error en la consulta", databaseError.toException());
            }
        });
    }

    private void updateList(ArrayList<MyUser> onlineUsers) {
        mlista.getEmptyView();
        mlista.setAdapter(mContactsAdapter);
        mContactsAdapter = new OnlineUsersAdapter(ContactsActivity.this, R.layout.item_lista_contactos, onlineUsers);
        mlista.setAdapter(mContactsAdapter);
        startListenerListOnlineUsers();
    }

    private void startListenerListOnlineUsers() {
        mlista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("LIST", "Clicked on item " + i);
                //Intent intent = new Intent(getBaseContext(), UserMap.class);
                //intent.putExtra("user", uuId);
                //intent.putExtra("userTouched", users.get(i).getUuid());
                //startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
    }
}