package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.proyecto1.Chats.Chat;
import com.example.proyecto1.Chats.ChatMsgAdapter;
import com.example.proyecto1.Chats.Mensaje;
import com.example.proyecto1.Utilities.CapitalizeString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public static final String PATH_USERS = "users/";
    public static final String PATH_USERSCHATS = "users/chats";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myRefChats;
    private StorageReference mStorageRef;

    //Chat
    private String uuId;
    private String userChatId;
    private String primerUsuario;
    private String segundoUsuario;
    private String keyChat;


    private TextView txtPersonaChat;
    private ListView listaChat;
    private EditText txtMsg;
    private ImageButton btnEnviarMsg;

    ChatMsgAdapter mChatsAdapter;

    //List of messages
    public static ArrayList<Mensaje> mensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Inflate
        txtPersonaChat = findViewById(R.id.txtPersonaChat);
        listaChat = findViewById(R.id.listaChat);
        txtMsg = findViewById(R.id.txtMsg);
        btnEnviarMsg = findViewById(R.id.btnEnviarMsg);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        uuId = mAuth.getCurrentUser().getUid();
        Intent intent = getIntent();
        userChatId = intent.getStringExtra("user");

        Log.i("Chat","from: "+uuId+" to: "+userChatId);
        //Database
        database = FirebaseDatabase.getInstance();
        //Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();

       getUser(uuId,userChatId);



    }

    //Get user from database
    private void getUser(String uuid, String userChatId) {

        myRef = database.getReference(PATH_USERS);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<MyUser> users;
                users = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    MyUser userFound = singleSnapshot.getValue(MyUser.class);
                    if(userFound.getID().equals(uuid)){
                            users.add(userFound);
                        Log.i("Chat","primer usuario: "+userFound.getName());

                    }
                    if(userFound.getID().equals(userChatId)){
                        users.add(userFound);
                        Log.i("Chat","segundo usuario: "+userFound.getName());
                    }
                }
                actualizarUsuarios(users);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DATABASE", "error en la consulta", databaseError.toException());
            }
        });
    }

    private void actualizarUsuarios(ArrayList<MyUser> users) {
        for (MyUser user : users) {
             if(user.getID().equals(uuId)){
                primerUsuario = user.getID();
                Log.i("Chat","primer usuario: "+primerUsuario);
                //Si no tiene chats, se crean y se guardan en la BD
                 if(!tienenChats(user)){
                     String key = myRef.push().getKey();
                     keyChat = key;
                     crearChat(uuId,userChatId,key);
                     crearChat(userChatId,uuId,key);
                     Log.i("Chat","chats creados");
                 }

            }
            else if(user.getID().equals(userChatId)){
                segundoUsuario = user.getID();
                txtPersonaChat.setText(CapitalizeString.capitalize(user.getName())+" "+CapitalizeString.capitalize(user.getLastName()));
                Log.i("Chat","segundo usuario: "+segundoUsuario);
            }
        }



    }

    private void crearChat(String primerUsuarioId, String segundoUsuarioId, String key)
    {
        try{
            //Crear chat de persona 1 con persona 2
            DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference("users/").child(primerUsuarioId);
            firebaseDB.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            MyUser user = task.getResult().getValue(MyUser.class);
                            Chat chat = new Chat(primerUsuarioId,segundoUsuarioId,key);

                            FirebaseDatabase.getInstance().getReference("users/" + primerUsuarioId + "/chats/"+key).setValue(chat);

                            //FirebaseDatabase.getInstance().getReference("users/" + segundoUsuario.getID() + "/chats").push().setValue(chat);
                        }
                    }
                }
            });

        }
        catch (Exception e){
            Log.i("Chat","Error al crear chat");
            Log.i("Chat",e.getMessage());
        }

    }

    private boolean tienenChats(MyUser user)
    {
        if(user.getChats()!=null) {
            for (Chat chat : user.getChats().values()) {
                if(chat!=null)
                {
                    if (chat.getSegundoUsuario().equals(userChatId)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private void getChat(String uuId) {
        myRef = database.getReference(PATH_USERS+uuId+"/chats");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Chat> chats;
                chats = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Chat chatfound = singleSnapshot.getValue(Chat.class);
                    for(Chat chat : chats) {

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DATABASE", "error en la consulta", databaseError.toException());
            }
        });
    }
}