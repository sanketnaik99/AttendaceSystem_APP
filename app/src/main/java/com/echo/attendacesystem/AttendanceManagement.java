package com.echo.attendacesystem;

import android.util.Log;

public class AttendanceManagement {

    public String lectureDate, lectureStartTime, lectureEndTime, lectureSubject;

    String TAG = "ATTENDANCE APP";

    public void logData(){
        Log.d(TAG, "logData: " + lectureDate + "  " + lectureStartTime + "   " + lectureEndTime + "  " + lectureSubject);
    }
}
