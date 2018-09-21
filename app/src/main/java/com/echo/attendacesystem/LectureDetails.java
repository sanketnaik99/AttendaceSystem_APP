package com.echo.attendacesystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

public class LectureDetails extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button dateButton, startTimeButton, endTimeButton;
    private TextView dateText, startTimeText, endTimeText;
    private String dateResult;
    private Spinner subjects;

    private String id;

    private String[] subjectList = new String[]{ "AM-3", "DS", "OOPM", "DM", "ECCF" };

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

        //ADD Date to Attendance Manager Class
        attendanceManager.lectureDate = dateResult;
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
                attendanceManager.lectureStartTime = startTime;
                break;

            case ("END_TIME_SELECT"):
                String endTime = hourOfDay + " : " + minute + " " + AM_PM;
                endTimeText.setText(endTime);
                id = "";

                //Add Start Time to Attendance Manager Class
                attendanceManager.lectureEndTime = endTime;
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

    public void pushData(View v){

        //GET data from Subject Dropdown
        attendanceManager.lectureSubject = subjects.getSelectedItem().toString();

        attendanceManager.logData();
    }
}
