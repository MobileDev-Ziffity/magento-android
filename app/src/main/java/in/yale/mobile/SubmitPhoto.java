package in.yale.mobile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class SubmitPhoto extends Activity {

    private int hasImg = 0;

    private ImageView imgProfile;

    private Uri mImageUri;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Bundle params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_a_photo);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        params = new Bundle();
        showPermissionDialog();
        hasImg = 0;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //final Bundle params = new Bundle();
        String dateStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
        params.putString("date",dateStr);
        imgProfile = findViewById(R.id.image);

        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                //imgProfile.setBackgroundResource(0);
                mFirebaseAnalytics.logEvent("submitPhoto_Camera", params);
            }
        });

        findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhotoFromGallery();
                //imgProfile.setBackgroundResource(0);
                mFirebaseAnalytics.logEvent("submitPhoto_Gallery", params);
            }
        });

        findViewById(R.id.buttonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent("submitPhoto_Send", params);
                EditText text = findViewById(R.id.scan_header);
                String body = text.getText().toString();
                if (body.length() < 1 && hasImg == 0) {
                    Toast.makeText(SubmitPhoto.this, "Please add an image and a message", Toast.LENGTH_LONG).show();
                } else if (body.length() < 1) {
                    Toast.makeText(SubmitPhoto.this, "Please add a message", Toast.LENGTH_LONG).show();
                } else if (hasImg == 0) {
                    Toast.makeText(SubmitPhoto.this, "Please add an image", Toast.LENGTH_LONG).show();
                }
                if (body.length() > 0 && hasImg == 1) {
                    composeEmail(new String[]{"customersupport@usesi.com"}, "Mobile App: Submit a Photo", body, mImageUri);
                }
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
                //Log.e("SubmitPhoto Err", "ERROR creating image file");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.FLAVOURED_AUTHORITIES,
                                                    photoFile);
                mImageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void grabImage(final ImageView imageView) {
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        //Bitmap bitmap;
        try {
            /* Without reducing large bitmap size
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            imageView.setImageBitmap(bitmap); */

            /* Reducing large bitmap size for Imageview */
            imageView.setImageBitmap(reduceLargeUri(cr));
            hasImg = 1;
        } catch (Exception e) {
            Toast.makeText(this, "Error Loading Image, TryAgain", Toast.LENGTH_SHORT).show();
            //Log.e("grabImage()", e.getMessage());
        }
    }

    private Bitmap reduceLargeUri(ContentResolver cr)
    {
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = cr.openInputStream(mImageUri);

            /* Decode image size */
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            //Log.d("debug", "scale = " + scale + ", orig-width: " + options.outWidth + ", orig-height: " + options.outHeight);

            Bitmap resultBitmap = null;
            in = cr.openInputStream(mImageUri);
            if (scale > 1) {
                scale--;
                /* scale to max possible inSampleSize that still yields an image
                   larger than target */
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                /* resize to desired dimensions */
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                //Log.d("debug", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
                resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            //Log.d("debug", "bitmap size - width: " +resultBitmap.getWidth() + ", height: " + resultBitmap.getHeight());

            return resultBitmap;
        } catch (IOException e) {
            //Log.e("debug", e.getMessage(),e);
            return null;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            ImageView imageView;
            imageView = findViewById(R.id.image);
            imgProfile.setBackgroundResource(0);
            this.grabImage(imageView);
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            mImageUri = selectedImage;
            try {
                /* Without reducing large bitmap size
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap); */
                ImageView imageView = findViewById(R.id.image);
                imgProfile.setBackgroundResource(0);
                /* Reducing large bitmap size for Imageview */
                imageView.setImageBitmap(reduceLargeUri(getContentResolver()));
                hasImg = 1;
            } catch (Exception e) {
                Toast.makeText(this, "Error Loading Image, TryAgain", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
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

