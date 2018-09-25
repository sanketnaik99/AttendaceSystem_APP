package com.echo.attendacesystem;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.util.Calendar;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class LectureDetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button dateButton, startTimeButton, endTimeButton;
    private TextView dateText, startTimeText, endTimeText;
    private String dateResult;
    private Spinner subjects;

    private String id;

    private String[] subjectList = new String[]{ "AM-3", "DS", "OOPM", "DM", "ECCF", "DLDA" };

    private AttendanceManagement attendanceManager = new AttendanceManagement();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_details);

        initviews();
    }

    //DatePicker Dialog Method which gets the Date after a user selects the date
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        dateResult = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());
        dateResult = dateResult.replace("/","-");
        dateText.setText(dateResult);
        AttendanceManagement.lectureWeek = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        //ADD Date to Attendance Manager Class
        AttendanceManagement.lectureDate = dateResult;
    }

    //TimePicker Dialog Which gets the Time after a user selects the time
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String AM_PM;
        if(hourOfDay > 12){
            hourOfDay=hourOfDay-12;
            AM_PM = "PM";
        }else{
            AM_PM = "AM";
        }
        if(hourOfDay == 0){
            hourOfDay = 12;
        }
        switch (id){
            case ("START_TIME_SELECT"):
                String startTime = hourOfDay + " : " + minute + " " + AM_PM;
                startTimeText.setText(startTime);
                id = "";

                //Add Start Time to Attendance Manager Class
                AttendanceManagement.lectureStartTime = startTime;
                AttendanceManagement.lectureStartTime_RAW = hourOfDay + "-" + minute;
                break;

            case ("END_TIME_SELECT"):
                String endTime = hourOfDay + " : " + minute + " " + AM_PM;
                endTimeText.setText(endTime);
                id = "";

                //Add Start Time to Attendance Manager Class
                AttendanceManagement.lectureEndTime = endTime;
                AttendanceManagement.lectureEndTime_RAW = hourOfDay + "-" + minute;
                break;
        }
    }


    //Initialize All Views
    private void initviews(){
        //DatePicker Contents
        dateButton = findViewById(R.id.dateSelectButton);
        dateText = findViewById(R.id.dateText);

        //Start Time TimePicker Contents
        startTimeText = findViewById(R.id.startTimeText);
        startTimeButton = findViewById(R.id.startTimeButton);

        //End Time TimePicker Contents
        endTimeText = findViewById(R.id.endTimeText);
        endTimeButton = findViewById(R.id.endTimeButton);

        //Spinner Content
        subjects = findViewById(R.id.subjectsSpinner);
        ArrayAdapter<String> subjectsSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectList);
        subjects.setAdapter(subjectsSpinnerAdapter);

    }

    //Button Click method for Date Select
    public void dateSelect(View v){
        DialogFragment datePicker = new datePickerFragment();
        datePicker.show(getSupportFragmentManager(), "Date Picker");
    }

    //Button Click method for Start Time select
    public void selectStartTime(View v){
        DialogFragment timePicker = new TimePickerFragment();
        id = "START_TIME_SELECT";
        timePicker.show(getSupportFragmentManager(), "Start Time Picker");
    }

    //Button Click method for End Time Select
    public void selectEndTime(View v){
        DialogFragment timePicker = new TimePickerFragment();
        id = "END_TIME_SELECT";
        timePicker.show(getSupportFragmentManager(), "Start Time Picker");
    }

    //Validate if All Fields have been marked
    private boolean validate(){
        boolean valid = true;
        if(AttendanceManagement.lectureDate.isEmpty()){
            dateButton.setBackgroundColor(getResources().getColor(R.color.errorColor));
            valid = false;
        }
        if(AttendanceManagement.lectureStartTime.isEmpty()){
            startTimeButton.setBackgroundColor(getResources().getColor(R.color.errorColor));
            valid = false;
        }
        if(AttendanceManagement.lectureEndTime.isEmpty()){
            endTimeButton.setBackgroundColor(getResources().getColor(R.color.errorColor));
            valid = false;
        }

        return valid;
    }


    //Method to Start barcode Scanner
    public void startScanning(View v){

        //GET data from Subject Dropdown
        AttendanceManagement.lectureSubject = subjects.getSelectedItem().toString();

        if(validate()) {
            LectureDetailsPermissionsDispatcher.openCameraWithPermissionCheck(this);
        }
    }

    //Get Barcode Scan Data and Update Data and UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null){
            if (result.getContents() == null){
                Toast.makeText(this,"Could Not Scan Barcode", Toast.LENGTH_LONG).show();
            }else{
                AttendanceManagement.currentStudent = result.getContents();
                AttendanceManagement.studentsList = result.getContents();
                AttendanceManagement.studentCount = 1;
                startActivity(new Intent(LectureDetails.this, ScanningMode.class));
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    //Get CAMERA PERMISSION
    @NeedsPermission(Manifest.permission.CAMERA)
    void openCamera() {
        IntentIntegrator scanner = new IntentIntegrator(this);
        scanner.setPrompt("please scan your ID card");
        scanner.setBeepEnabled(true);
        scanner.setOrientationLocked(true);
        scanner.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LectureDetailsPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.CAMERA)
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
}
