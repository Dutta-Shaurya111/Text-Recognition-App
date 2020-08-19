package com.example.textrecognitionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
ImageView img;
Button upload_image;
    public String dataonticket;
Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
private static final int PICK_IMAGE=2;
private int STORAGEPERMISSIONCODE=3;
Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img=findViewById(R.id.image_view);
        upload_image=findViewById(R.id.upload_image);

        upload_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       /* if (view==img_btn)
        {
            permission();
dispatchTakePictureIntent();
        }*/


        if (view==upload_image)
        {
            galleryIntent();
        }

    }

    void permission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();

        }else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
new AlertDialog.Builder(this)
        .setTitle("Permission Needed")
        .setMessage("This permission is required because it will allow the image access")
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.CAMERA},STORAGEPERMISSIONCODE);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},STORAGEPERMISSIONCODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==STORAGEPERMISSIONCODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Uploading Image from Gallery
    private void galleryIntent()
    {
Intent gallery=new Intent();
gallery.setType("image/*");
gallery.setAction(Intent.ACTION_GET_CONTENT);

startActivityForResult(Intent.createChooser(gallery,"Select Picture "),PICK_IMAGE );
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageUri = data.getData();
                img.setImageBitmap(imageBitmap);
                getTextRecognised(imageUri);
            }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Some Error Occured",Toast.LENGTH_SHORT).show();
                Log.d("Exception Occured: " ,e.getMessage());
        }
        }

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            imageUri=data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                img.setImageBitmap(bitmap);
                getTextRecognised(imageUri);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"Some Error Occured",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void getTextRecognised(Uri imageUri) {
        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(getApplicationContext(), imageUri);
            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            textRecognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText result) {
                            dataonticket = result.getText();
                            Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                            intent.putExtra("data",dataonticket);
                            startActivity(intent);
                            Log.e("TEXT",dataonticket);
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Task Failed",Toast.LENGTH_LONG).show();
                                }
                            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Exit App")
                .setMessage("Do You Really Want To Exit")

       .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
             dialogInterface.dismiss();
            }
        }).create().show();
    }
}
