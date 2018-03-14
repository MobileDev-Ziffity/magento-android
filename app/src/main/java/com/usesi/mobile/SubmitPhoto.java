package com.usesi.mobile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SubmitPhoto extends Activity {

    private int hasImg = 0;

    private ImageView imgProfile;

    private String photoUri;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_a_photo);
        showPermissionDialog();
        hasImg = 0;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        imgProfile = findViewById(R.id.image);
        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                photoUri = takePhotoFromCamera();
                dispatchTakePictureIntent();
            }
        });

        findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhotoFromGallery();
            }
        });

        findViewById(R.id.buttonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.scan_header);
                String body = text.getText().toString();
                if (body.toString().length() < 1 && hasImg == 0) {
                    Toast.makeText(SubmitPhoto.this, "Please add an image and a message", Toast.LENGTH_LONG).show();
                } else if (body.toString().length() < 1) {
                    Toast.makeText(SubmitPhoto.this, "Please add a message", Toast.LENGTH_LONG).show();
                } else if (hasImg == 0) {
                    Toast.makeText(SubmitPhoto.this, "Please add an image", Toast.LENGTH_LONG).show();
                }
                if (body.toString().length() > 0 && hasImg == 1) {
                    composeEmail(new String[]{"customersupport@usesi.com"}, "Mobile App: Submit a Photo", body, mImageUri);
                }

//                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//                emailIntent.setType("application/image");
//                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"selvakumar.porccha@gmail.com"});
//                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test Subject");
//                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
//                Log.d("tag", "onClick: photo uri == " +photoUri);
//
//                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(photoUri));
//                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("SubmitPhoto Err", "ERROR creating image file");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                                                    "com.usesi.android.fileprovider",
                                                    photoFile);
                mImageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void grabImage(ImageView imageView) {
        this.getContentResolver().notifyChange(mImageUri, null);
        Log.e("", mImageUri.toString());
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap);
            hasImg = 1;
        } catch (Exception e) {
            Log.d("","Failed to load", e);
        }
    }

    public void composeEmail(String[] addresses, String subject, String text, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


//    private String takePhotoFromCamera() {
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            photoUri = Environment.getExternalStorageDirectory() + "/IMG_" + new Random().nextInt(8000) + ".jpg";
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoUri)));
//            Log.d("photo", "PATH= = = = = " + photoUri);
//            startActivityForResult(intent, 3);
//        }
//        return photoUri;
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onAvtivityResult", requestCode + "");
        if (requestCode == 1 && resultCode == RESULT_OK) {
//            Glide.with(this).load(new File(photoUri))
//                    .into(imgProfile);
//            imgProfile.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageView imageView;
            imageView = findViewById(R.id.image);
            this.grabImage(imageView);

        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            assert selectedImage != null;
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//            if (cursor != null) {
                cursor.moveToFirst();
//            }

//            assert cursor != null;
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String image = cursor.getString(columnIndex);
//            Log.e("HENLO", image);
//            photoUri = image;
            cursor.close();

            Bitmap myImage = BitmapFactory.decodeFile(image);

            ImageView imageView = findViewById(R.id.image);
            imageView.setImageBitmap(myImage);
//            Log.e("galleryImageView", myImage.toString());
//            imgProfile.setImageBitmap(myImage);
//            imgProfile.setScaleType(ImageView.ScaleType.FIT_XY);
//            imgProfile.setImageBitmap(BitmapFactory.decodeFile(image));
        }
    }

    public void uploadPhotoFromGallery() {
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

