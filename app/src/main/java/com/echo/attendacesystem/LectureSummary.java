package com.echo.attendacesystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LectureSummary extends AppCompatActivity {

    private static final String TAG = "LectureSummary";
    private TextView summaryTime, summarySubject, summaryDate;
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
    }

    //Getting Data from Attendance Management Class and Updating UI
    private void updateData(){
        summarySubject.setText(AttendanceManagement.lectureSubject);
        summaryDate.setText(AttendanceManagement.lectureDate);
        summaryTime.setText(AttendanceManagement.lectureStartTime + " to " + AttendanceManagement.lectureEndTime);

        //Update Student List
        ArrayList<String> studentData = new ArrayList<>();
        String data[] = AttendanceManagement.studentsList.split(",");
        for (String Student: data) {
            studentData.add(Student);
        }

        ArrayAdapter studentAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, studentData);
        studentList.setAdapter(studentAdapter);

    }
}
