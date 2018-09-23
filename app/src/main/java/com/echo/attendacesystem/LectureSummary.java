package com.echo.attendacesystem;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LectureSummary extends AppCompatActivity {

    private static final String TAG = "LectureSummary";
    private TextView summaryTime, summarySubject, summaryDate, studentCount;
    private ListView studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_summary);

        initviews();
        updateData();

    }

    //Initialize All Views
    private void initviews() {
        summarySubject = findViewById(R.id.summary_subjectName);
        summaryDate = findViewById(R.id.summary_date);
        summaryTime = findViewById(R.id.summary_time);
        studentList = findViewById(R.id.summary_studentsList);
        studentCount = findViewById(R.id.summary_studentCount);
    }

    //Getting Data from Attendance Management Class and Updating UI
    private void updateData(){
        summarySubject.setText(AttendanceManagement.lectureSubject);
        summaryDate.setText(AttendanceManagement.lectureDate);
        summaryTime.setText(AttendanceManagement.lectureStartTime + " to " + AttendanceManagement.lectureEndTime);
        studentCount.setText(String.valueOf(AttendanceManagement.studentCount));

        //Update Student List
        ArrayList<String> studentData = new ArrayList<>();
        String data[] = AttendanceManagement.studentsList.split(",");
        for (String Student: data) {
            studentData.add(Student);
        }

        ArrayAdapter studentAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, studentData);
        studentList.setAdapter(studentAdapter);

    }

    public void addToDatabase(View v){
        //Creating Progress Dialog
        final ProgressDialog progressDialog = new ProgressDialog(LectureSummary.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setMax(100);
        progressDialog.setMessage("Adding to Database..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();



        if (!AttendanceManagement.publishData()){
            Toast.makeText(this,"Error", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }else{
            Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}
