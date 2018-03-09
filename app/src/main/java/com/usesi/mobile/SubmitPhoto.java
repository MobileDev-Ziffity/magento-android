package com.usesi.mobile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class SubmitPhoto extends Activity {

    private ImageView imgProfile;

    private String photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_a_photo);
        showPermissionDialog();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        imgProfile = (ImageView) findViewById(R.id.image);
        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoUri = takePhotoFromCamera();
            }
        });

        findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhotoFromGalllery();
            }
        });

        findViewById(R.id.buttonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("application/image");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"selvakumar.porccha@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test Subject");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
                Log.d("tag", "onClick: photo uri == " +photoUri);

                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(photoUri));
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

    }

    private String takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            photoUri = Environment.getExternalStorageDirectory() + "/IMG_" + new Random().nextInt(8000) + ".jpg";
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoUri)));
            Log.d("photo", "PATH= = = = = " + photoUri);
            startActivityForResult(intent, 3);
        }
        return photoUri;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == RESULT_OK) {
            Glide.with(this).load(new File(photoUri))
                    .into(imgProfile);
            imgProfile.setScaleType(ImageView.ScaleType.FIT_XY);

        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String image = cursor.getString(columnIndex);
            photoUri = image;
            cursor.close();

            imgProfile.setScaleType(ImageView.ScaleType.FIT_XY);
            imgProfile.setImageBitmap(BitmapFactory.decodeFile(image));
        }
    }

    public void uploadPhotoFromGalllery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            finish();
        }


    };


    private void showPermissionDialog() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

}

