package com.echo.attendacesystem;

import android.util.Log;

public class AttendanceManagement {

    public static String lectureDate, lectureStartTime, lectureEndTime, lectureSubject, studentsList, currentStudent;
    public int studentCount;

    String TAG = "ATTENDANCE APP";

    public void logData(){
        Log.d(TAG, "logData: " + lectureDate + "  " + lectureStartTime + "   " + lectureEndTime + "  " + lectureSubject);
    }
}
