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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class HardwareCameraActivity extends AppCompatActivity {

    private final int CAMERA_PERMISSION_ID = 101;
    private final int GALLERY_PERMISSION_ID = 102;
    String cameraPerm = Manifest.permission.CAMERA;
    String galPerm = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static boolean accessCamera = false, accessGal = false;
    private ImageView ivImagen;
    private Button bGaleria, bCamara, bAceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_camera);
       // getSupportActionBar().hide();

        ivImagen = findViewById(R.id.imagenTomada);
        bGaleria = findViewById(R.id.buttonGaleria);
        bCamara = findViewById(R.id.buttonCamera);
        bAceptar = findViewById(R.id.aceptarFoto);

        bGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessGal = requestPermission(HardwareCameraActivity.this, galPerm, "Permiso para utilizar la galeria", GALLERY_PERMISSION_ID);
                if(accessGal){
                    startGallery();
                }
            }
        });

        bCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessCamera = requestPermission(HardwareCameraActivity.this, cameraPerm, "Permiso para utilizar la camara", CAMERA_PERMISSION_ID);
                if(accessCamera){
                    startCamera();
                }
            }
        });

        bAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream previewStream = new ByteArrayOutputStream();
                Bitmap previewBmp = ((BitmapDrawable) ivImagen.getDrawable()).getBitmap();
                previewBmp.compress(Bitmap.CompressFormat.JPEG, 100, previewStream);
                byte[] previewByteArray = previewStream.toByteArray();
                Intent intent = new Intent(getBaseContext(), ModifyActivity.class);
                intent.putExtra("picture", previewByteArray);
                startActivity(intent);
            }
        });
    }

    private boolean requestPermission(Activity context, String permission, String justification, int id){
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
            return false;
        }
        return true;
    }

    private void startCamera(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(pictureIntent, CAMERA_PERMISSION_ID);
        }
    }

    public void startGallery(){
        Intent pickGalleryImage = new Intent(Intent.ACTION_PICK);
        pickGalleryImage.setType("image/*");
        startActivityForResult(pickGalleryImage, GALLERY_PERMISSION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_PERMISSION_ID: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startCamera();
                } else {
                    Toast.makeText(getApplicationContext(), "Access denied to camera", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case GALLERY_PERMISSION_ID: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startGallery();
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
            case CAMERA_PERMISSION_ID: {
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ivImagen.setImageBitmap(imageBitmap);
                }
                break;
            }
            case GALLERY_PERMISSION_ID: {
                if(resultCode == RESULT_OK){
                    try{
                        final Uri imageUri = data.getData();
                        final InputStream is = getContentResolver().openInputStream(imageUri);
                        final Bitmap selected = BitmapFactory.decodeStream(is);
                        ivImagen.setImageBitmap(selected);

                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}