package com.example.proyecto1;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;


public class Registrar extends AppCompatActivity{
    /*private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_PICKER_REQUEST = 4;
    private static final int CAMERA = 2;
    private static final int ALMACENAMIENTO_EXTERNO = 3;*/
    //private static boolean accessCamera = false, accessAlm = false;
    public static final String FB_USERS_PATH="users/";
    Button btnIngresar, btnImagen, btnCamara;
    EditText txtname, txtlastName,txtemail, txtusername,txtnumber,txtpassword, txtCpassword;
    //ImageView ivImage;
    FirebaseAuth mAuth;
   // public Uri imageUri;
    //private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("users");
    //DatabaseReference myRef;
    //private FirebaseStorage storage;
    //private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        btnIngresar=findViewById(R.id.btnIngresa);
        txtname=findViewById(R.id.name);
        txtlastName=findViewById(R.id.lastName);
        txtemail=findViewById(R.id.email);
        txtusername=findViewById(R.id.username);
        txtnumber=findViewById(R.id.phoneNumber);
        txtpassword=findViewById(R.id.password);
        txtCpassword=findViewById(R.id.confirmPassword);
        //btnCamara=findViewById(R.id.btnSelectCamara);
        //btnImagen=findViewById(R.id.btnSelectImage);
       // ivImage=findViewById(R.id.fotoPerfil);
        mAuth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        //storage= FirebaseStorage.getInstance();
        //storageReference=storage.getReference();
       /* btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessAlm = solicitPermission(Registrar.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Permission to Access Gallery", ALMACENAMIENTO_EXTERNO);
                if(accessAlm){
                    usePermissionImage();
                }
            }
        });

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessCamera = solicitPermission(Registrar.this, Manifest.permission.CAMERA, "Permission to Access Camera", CAMERA);
                if(accessCamera){
                    usePermissionCamera();
                }
            }
        });*/
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= txtname.getText().toString();
                String lastName= txtlastName.getText().toString();
                String userName= txtusername.getText().toString();
                String mail=txtemail.getText().toString();
                String phoneNumber=txtnumber.getText().toString();
                String password= txtpassword.getText().toString();
                String confPass= txtCpassword.getText().toString();
                Log.i("MENSAJE2", "ENTRA1");
                //mirar que todos los campos esten llenos
                if(name.isEmpty()||lastName.isEmpty()||userName.isEmpty()||mail.isEmpty()||phoneNumber.isEmpty()||password.isEmpty()){
                    Log.i("MENSAJE", "ENTRA2");
                    Toast.makeText(Registrar.this,"Porfavor llene todos los campos!!",Toast.LENGTH_SHORT).show();
                }else if (!password.equals(confPass)){ //mirar si las contraseñas son iguales
                    Toast.makeText(Registrar.this,"Las contraseñas no conciden!!",Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.i("MENSAJE3", "ENTRA3");
                                user = mAuth.getCurrentUser();
                                String userID = mAuth.getCurrentUser().getUid();
                                MyUser user= new MyUser(name, lastName, mail, userName, phoneNumber, password, userID);
                                userRef= database.getReference(FB_USERS_PATH+userID);
                                userRef.setValue(user);

                               // final ProgressDialog dialog=new ProgressDialog(Registrar.this);
                              //  final StorageReference uploader=storage.getReference("Image1"+new Random().nextInt(50));
                               // dialog.setTitle("File Uploader");
                                //dialog.show();
                               /* uploader.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri){
                                                dialog.dismiss();
                                                myRef= database.getReference(FB_USERS_PATH);
                                                String userID = mAuth.getCurrentUser().getUid();
                                                MyUser user= new MyUser(name, lastName, mail, userName, phoneNumber, password,imageUri.toString());
                                                myRef= database.getReference(FB_USERS_PATH+userID);
                                                myRef.setValue(user);
                                                Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    }
                                });*/
                                Intent intent  = new Intent(getBaseContext(), MenuActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(Registrar.this, "Usuario no registrado"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }//termina else
            }
        });

    }//End onCreate
   /* private boolean solicitPermission(Activity context, String permit, String justification, int id){
        if(ContextCompat.checkSelfPermission(context, permit) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, permit)){
                Toast.makeText(this, justification, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permit}, id);
            return false;
        } else {
            return true;
        }
    }*/
   /* private void usePermissionCamera(){
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
                    imageUri=getImageUri(Registrar.this,imageBitmap);
                    ivImage.setImageBitmap(imageBitmap);
                }
                break;
            }
            case IMAGE_PICKER_REQUEST: {
                if(resultCode == RESULT_OK){
                    try{
                        imageUri = data.getData();
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
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/
}
