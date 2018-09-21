package com.echo.attendacesystem;

import android.content.Intent;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;




public class ScanningMode extends AppCompatActivity {

    private Button captureButton;
    ImageView barcodeImage;
    TextView barcodeValue;

    //Disable Back Button
    @Override
    public void onBackPressed(){
        Log.d("", "onBackPressed: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_mode);

        initviews();
        barcodeValue.setText(AttendanceManagement.currentStudent);
    }

    private void initviews(){
        barcodeValue = findViewById(R.id.barcode_result);
    }

    public void scanCode(View v){
        IntentIntegrator scanner = new IntentIntegrator(this);
        scanner.setPrompt("please scan your ID card");
        scanner.setBeepEnabled(true);
        scanner.setOrientationLocked(true);
        scanner.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            if (result.getContents() == null){
                Toast.makeText(this,"Could Not Scan Barcode", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Success " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
