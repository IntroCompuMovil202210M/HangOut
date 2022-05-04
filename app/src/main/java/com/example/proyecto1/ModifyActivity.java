package com.example.proyecto1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ModifyActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_PICKER_REQUEST = 4;
    private static final int CAMERA = 2;
    private static final int ALMACENAMIENTO_EXTERNO = 3;
    private static boolean accessCamera = false, accessAlm = false;
    private ImageView ivImage;
    private Button btnImage, btnCamera, btnGuardar;
    EditText txtUserName, txtPhone, txtEmail;
    TextView txtName;
    private FirebaseAuth mAuth;
    private boolean changedPhoto;
    private StorageReference mStorage;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("users");
    private MyUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_modify);

        ivImage = findViewById(R.id.imageView2);
        btnImage = findViewById(R.id.btnSelectImage);
        btnCamera = findViewById(R.id.btnSelectCamara2);
        txtUserName = findViewById(R.id.textView2);
        txtPhone = findViewById(R.id.editTextTextMultiLine2);
        txtEmail = findViewById(R.id.editTextTextMultiLine3);
        txtName = findViewById(R.id.editTextTextMultiLine);
        btnGuardar=findViewById(R.id.button);


        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessAlm = solicitPermission(ModifyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Permission to Access Gallery", ALMACENAMIENTO_EXTERNO);
                if(accessAlm){
                    usePermissionImage();
                }
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessCamera = solicitPermission(ModifyActivity.this, Manifest.permission.CAMERA, "Permission to Access Camera", CAMERA);
                if(accessCamera){
                    usePermissionCamera();
                }
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserToDatabase();
                Intent intent= new Intent(getBaseContext(), PerfilActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean solicitPermission(Activity context, String permit, String justification, int id){
        if(ContextCompat.checkSelfPermission(context, permit) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, permit)){
                Toast.makeText(this, justification, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permit}, id);
            return false;
        } else {
            return true;
        }
    }

    private void usePermissionCamera(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void usePermissionImage(){
        Intent pictureIntent = new Intent(Intent.ACTION_PICK);
        pictureIntent.setType("image/*");
        startActivityForResult(pictureIntent, IMAGE_PICKER_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    usePermissionCamera();
                } else {
                    Toast.makeText(getApplicationContext(), "Access denied to camera", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case ALMACENAMIENTO_EXTERNO: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    usePermissionImage();
                } else {
                    Toast.makeText(getApplicationContext(), "Access denied to image gallery", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_IMAGE_CAPTURE: {
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ivImage.setImageBitmap(imageBitmap);
                }
                break;
            }
            case IMAGE_PICKER_REQUEST: {
                if(resultCode == RESULT_OK){
                    try{
                        final Uri imageUri = data.getData();
                        final InputStream is = getContentResolver().openInputStream(imageUri);
                        final Bitmap selected = BitmapFactory.decodeStream(is);
                        ivImage.setImageBitmap(selected);
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
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
                }
                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }
}