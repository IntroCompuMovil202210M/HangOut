package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ModifyActivity extends AppCompatActivity {

    private ImageView fotoPerfil;
    private Button btnImage, btnCamera, btnGuardar;
    EditText txtUserName, txtPhone, txtEmail;
    TextView txtName;
    private FirebaseAuth mAuth;
    private MyUser user;
    private Bitmap bmImage;
    private boolean changedPhoto;
    private StorageReference mStorage;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        fotoPerfil = findViewById(R.id.fotoTomada);
        txtUserName = findViewById(R.id.textView2);
        txtPhone = findViewById(R.id.editTextTextMultiLine2);
        txtEmail = findViewById(R.id.editTextTextMultiLine3);
        txtName = findViewById(R.id.txtName);
        btnGuardar=findViewById(R.id.button);
        changedPhoto = false;
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserToDatabase();
                uploadPhotoToStorage();
                Intent intent= new Intent(getBaseContext(), PerfilActivity.class);
                startActivity(intent);
            }
        });
    }


    public void updateUserToDatabase(){
        if(!txtUserName.getText().toString().equals("") || !txtPhone.getText().toString().equals("") || !txtEmail.getText().toString().equals("")){
            if(!txtUserName.getText().toString().equals("")) user.setUserName(txtUserName.getText().toString());
            if(!txtPhone.getText().toString().equals("")) user.setPhoneNumber(txtPhone.getText().toString());
            if(!txtEmail.getText().toString().equals("")) user.setMail(txtEmail.getText().toString());
            userRef.child(user.getID()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ModifyActivity.this, "Se actualizo el perfil.",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ModifyActivity.this, "Error actualizando el perfil.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            userRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(MyUser.class);
                    user.setID(dataSnapshot.getKey());
                    txtName.setText(user.getName() + " " +user.getLastName());
                    try {
                        downloadFile(user.getID(), fotoPerfil);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }
    public void uploadPhotoToStorage() {
        if(changedPhoto) {
            StorageReference imgRef = mStorage.child("images/" + user.getID() + "/" + "profile.png");
            imgRef.putBytes(uploadImage()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ModifyActivity.this, "Se guardó la foto!", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ModifyActivity.this, "Error guardando la foto!", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void downloadFile(String id, ImageView ivPhoto) throws IOException {
        final File localFile = File.createTempFile("images", "png");
        StorageReference imageRef = mStorage.child("images/" + id + "/profile.png");
        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        fotoPerfil.setImageURI(Uri.fromFile(localFile));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }
    public void onClickAceptarModificacionInfo(View view){
       Intent intent = new Intent(this, PerfilActivity.class);
       startActivity(intent);
    }

    public void onClickSubirFotoUsuario(View view){
        Intent intent = new Intent(this, HardwareCameraActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        recuperarFoto();
    }

    private byte[] uploadImage(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public void recuperarFoto(){
        if(getIntent().getByteArrayExtra("picture") != null){
            changedPhoto = true;
            byte[] byteArray = getIntent().getByteArrayExtra("picture");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            bmImage = bmp;
            fotoPerfil.setImageBitmap(bmp);
        }
    }
}