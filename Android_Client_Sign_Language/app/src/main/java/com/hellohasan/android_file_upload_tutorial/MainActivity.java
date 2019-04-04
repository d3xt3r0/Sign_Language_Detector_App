package com.hellohasan.android_file_upload_tutorial;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellohasan.android_file_upload_tutorial.ModelClass.EventModel;
import com.hellohasan.android_file_upload_tutorial.ModelClass.ImageSenderInfo;
import com.hellohasan.android_file_upload_tutorial.NetworkRelatedClass.NetworkCall;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;
   // private EditText ageEditText;
    private ImageView imageView;
    private Button uploadButton;
    private TextView responseTextView;
    File photoFile = null;

    private static String ip = "Hello";

    private String filePath;
    private static final int PICK_PHOTO = 1958;
    private static final int CAPTURE_PHOTO = 1959;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventModel event) throws ClassNotFoundException {
        if (event.isTagMatchWith("response")) {
            String responseMessage = "Response from Server:\n" + event.getMessage();
            responseTextView.setText(responseMessage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
       // ageEditText = (EditText) findViewById(R.id.ageEditText);
        imageView = (ImageView) findViewById(R.id.imageView);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        responseTextView = (TextView) findViewById(R.id.responseTextView);

        verifyStoragePermissions(this);
    }

    public void addPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO);
    }

    public void cameraButtonClicked(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
           // File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_PHOTO);

                if(photoFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_PHOTO) {
            Uri imageUri = data.getData();
            filePath = getPath(imageUri);
            imageView.setImageURI(imageUri);
            uploadButton.setVisibility(View.VISIBLE);
        }else if (resultCode == RESULT_OK && requestCode == CAPTURE_PHOTO){
            //Uri imageUri = data.getData();
            //filePath = getPath(imageUri);

            if(photoFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                filePath  = photoFile.getAbsolutePath();
            }

            //imageView.setImageURI(imageUri);
            uploadButton.setVisibility(View.VISIBLE);
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }




    public void uploadButtonClicked(View view) {
        String name = nameEditText.getText().toString();


        ip = name;

        responseTextView.setText("");
       // int age = Integer.parseInt(ageEditText.getText().toString());
        NetworkCall.fileUpload(filePath, new ImageSenderInfo(name));
    }

    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

    }

    public static String getData(){
        return ip;
    }
}
