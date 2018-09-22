package com.echo.attendacesystem;

import android.util.Log;

public class AttendanceManagement {

    public static String lectureDate = "", lectureStartTime = "", lectureEndTime = "", lectureSubject = "", studentsList, currentStudent;
    public static String lectureMonth, lectureDay;
    public static int studentCount, lectureWeek;

    String TAG = "ATTENDANCE APP";

    public void logData(){
        Log.d(TAG, "logData: " + lectureDate + "  " + lectureStartTime + "   " + lectureEndTime + "  " + lectureSubject);
    }
}
