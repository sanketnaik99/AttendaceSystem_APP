package com.echo.attendacesystem;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.File;
import java.net.URI;
import java.util.List;


import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ScanningMode extends AppCompatActivity {

    private Button captureButton;
    ImageView barcodeImage;
    TextView barcodeValue;

    //Declaring Path of the image on External Storage
    private String path = "";
    private String appFolderPath = "";
    //Fixed Name for Image
    String barcode = "barcode-image";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_mode);

        //Defining Context of the Activity
        final ScanningMode temp = this;

        //Requesting ALL permissions With Rationale
        ScanningModePermissionsDispatcher.getCameraPermissionWithPermissionCheck(temp);


       //Firebase Barcode Initialize
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_CODE_39,
                        FirebaseVisionBarcode.FORMAT_CODE_93,
                        FirebaseVisionBarcode.FORMAT_CODE_128).build();

        //Defining Button and OnClick Action
        captureButton = (Button)findViewById(R.id.capture_image);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Assigning name to image
                String filename = barcode + ".jpg";

                //Create Storage folder
                File appFolder = Environment.getExternalStorageDirectory();
                appFolderPath = appFolder.getAbsolutePath() + "/" + "Attendance";
                File appDir = new File(appFolderPath);
                if(!appDir.exists())
                    appDir.mkdir();


                //Specifyinh the storage Location of the Image
                File storageDir = Environment.getExternalStorageDirectory();
                path = appFolderPath + "/" + filename;
                File file = new File(path);
                Uri outputFileUri = FileProvider.getUriForFile(ScanningMode.this, BuildConfig.APPLICATION_ID + ".provider",file);

                //Creating Intent for Camera Image Capture
               Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
               startActivityForResult(intent,1);
            }
        });
    }



    @NeedsPermission({Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    //Get permission and open Camera
    void getCameraPermission() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ScanningModePermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    void cameraRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this).setMessage("Please Enable Camera Permission").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.proceed();
            }
        }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.cancel();
            }
        }).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            File imageFile = new File(path);
            if(imageFile.exists()){
                Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                ImageView barcodeImage = (ImageView)findViewById(R.id.captured_image);
                barcodeImage.setImageBitmap(imageBitmap);
                processFirebaseImage(imageBitmap);
            }
        }
    }

    protected void processFirebaseImage(Bitmap image){

        //creating a FirebaseVisionImage object from a Bitmap object:
        FirebaseVisionImage fireImage = FirebaseVisionImage.fromBitmap(image);

        //Creating Instance Of Firebase Barcode Detector
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector();

        //Detecting Barcodes from Image
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(fireImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                Toast.makeText(ScanningMode.this,"Success!", Toast.LENGTH_SHORT).show();

                //Getting Information from Barcodes
                for(FirebaseVisionBarcode barcode : firebaseVisionBarcodes){
                    Rect bounds = barcode.getBoundingBox();
                    Point[] corners = barcode.getCornerPoints();

                    String rawValue = barcode.getRawValue();
                    TextView barcodeValue = (TextView)findViewById(R.id.barcode_value);
                    barcodeValue.setText(rawValue);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScanningMode.this,"Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
