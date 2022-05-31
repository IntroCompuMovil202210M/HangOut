package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Log.i("Chat", "from: " + uuId + " to: " + userChatId);
        //Database
        database = FirebaseDatabase.getInstance();
        //Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();

        getUser(uuId, userChatId);
        enviarMensaje();


    }

    private void enviarMensaje() {
        btnEnviarMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txtMsg.getText().toString();
                if (!msg.isEmpty()) {
                    crearMensaje(msg, uuId, userChatId, keyChat, uuId);
                    crearMensaje(msg, uuId, userChatId, keyChat, userChatId);
                    txtMsg.setText("");
                }
            }
        });
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
                    if (userFound.getID().equals(uuid)) {
                        users.add(userFound);
                        Log.i("Chat", "primer usuario: " + userFound.getName());

                    }
                    if (userFound.getID().equals(userChatId)) {
                        users.add(userFound);
                        Log.i("Chat", "segundo usuario: " + userFound.getName());
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
        boolean encontrado = false;
        for (MyUser user : users) {
            if (user.getID().equals(uuId)) {
                //primerUsuario = user.getID();
                Log.i("Chat", "primer usuario: " + uuId);
                //Si no tiene chats, se crean y se guardan en la BD

                if (encontrado == false) {
                    keyChat = tienenChats(user);
                    encontrado = true;
                    if (keyChat == null) {
                        String key = myRef.push().getKey();
                        keyChat = key;
                        crearChat(uuId, userChatId, key, uuId);
                        crearChat(userChatId, uuId, key, userChatId);
                        Log.i("Chat", "chats creados");
                        //getMessages();
                    } else {
                        Log.i("Chat", "chats existentes");
                        //Get messages
                        getMessages();
                    }
                }


            } else if (user.getID().equals(userChatId)) {
                segundoUsuario = user.getID();
                txtPersonaChat.setText(CapitalizeString.capitalize(user.getName()) + " " + CapitalizeString.capitalize(user.getLastName()));
                Log.i("Chat", "segundo usuario: " + segundoUsuario);
            }
        }
    }

    private void getMessages() {
        DatabaseReference myRefMessages = database.getReference(PATH_USERS + uuId + "/chats/" + keyChat).child("mensajes");
        myRefMessages.addValueEventListener(new ValueEventListener() {
            ArrayList<Mensaje> mensajes;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mensajes = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> msg = (Map<String, Object>) singleSnapshot.getValue();
                    String receptor = (String) msg.get("receptor");
                    String emisor = (String) msg.get("emisor");
                    String mensaje = (String) msg.get("mensaje");
                    Mensaje mensajeFound = new Mensaje(mensaje, emisor, receptor);
                    mensajes.add(mensajeFound);
                    Log.i("Chat", "mensaje: " + mensajeFound.getMensaje());
                }
                updateUI(mensajes, uuId);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DATABASE", "error en la consulta", databaseError.toException());
            }
        });
    }

    private void updateUI(ArrayList<Mensaje> mensajes, String uuid) {
        //listaChat.getEmptyView();
        Log.i("Chat", "mensajes: " + mensajes.size());
        Log.i("Chat", "uuid: " + uuid);
        if (uuid != null) {
            mChatsAdapter = new ChatMsgAdapter(ChatActivity.this, R.layout.item_lista_chat, mensajes, uuid);
            listaChat.setAdapter(mChatsAdapter);
            listaChat.setSelection(mChatsAdapter.getCount() - 1);
        }

    }

    private void crearChat(String primerUsuarioId, String segundoUsuarioId, String key, String uuId) {
        try {
            //Crear chat de persona 1 con persona 2
            DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference("users/").child(primerUsuarioId);
            firebaseDB.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            MyUser user = task.getResult().getValue(MyUser.class);
                            Chat chat = new Chat(primerUsuarioId, segundoUsuarioId, key);
                            if (uuId.equals(primerUsuarioId)) {
                                FirebaseDatabase.getInstance().getReference("users/" + primerUsuarioId + "/chats/" + key).setValue(chat);
                            } else {
                                FirebaseDatabase.getInstance().getReference("users/" + segundoUsuarioId + "/chats/" + key).setValue(chat);
                            }


                            //FirebaseDatabase.getInstance().getReference("users/" + segundoUsuario.getID() + "/chats").push().setValue(chat);
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.i("Chat", "Error al crear chat");
            Log.i("Chat", e.getMessage());
        }
    }

    private void crearMensaje(String mensaje, String primerUsuarioId, String segundoUsuarioId, String key, String uuId) {
        try {
            //Crear chat de persona 1 con persona 2
            DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference("users/" + primerUsuarioId + "/chats/").child(key);
            firebaseDB.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            Chat chat = task.getResult().getValue(Chat.class);
                            Mensaje mensajeNuevo = new Mensaje(mensaje, primerUsuarioId, segundoUsuarioId);
                            if (chat.getMensajes() == null) {
                                ArrayList<Mensaje> mensajes = new ArrayList<>();
                                mensajes.add(mensajeNuevo);
                                chat.setMensajes(mensajes);
                            } else {
                                chat.getMensajes().add(mensajeNuevo);
                            }
                            if (uuId.equals(primerUsuarioId)) {
                                FirebaseDatabase.getInstance().getReference("users/" + primerUsuarioId + "/chats/" + key).setValue(chat);
                            } else {
                                FirebaseDatabase.getInstance().getReference("users/" + segundoUsuarioId + "/chats/" + key).setValue(chat);
                            }


                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.i("Chat", "Error al crear chat");
            Log.i("Chat", e.getMessage());
        }

    }


    private String tienenChats(MyUser user) {
        if (user.getChats() != null) {
            for (Chat chat : user.getChats().values()) {
                if (chat != null) {
                    if (chat.getPrimerUsuario() != null && chat.getSegundoUsuario() != null) {
                        //Si el primer usuario y el segundo coinciden
                        if (chat.getPrimerUsuario().equals(uuId)) {
                            if (chat.getSegundoUsuario().equals(userChatId)) {
                                return chat.getKey();
                            }
                        }
                        //Si los usuarios están cambiados
                        if (chat.getSegundoUsuario().equals(uuId)) {
                            if (chat.getPrimerUsuario().equals(userChatId)) {
                                return chat.getKey();
                            }
                        }

                    }
                }

            }
        }
        return null;
    }


}