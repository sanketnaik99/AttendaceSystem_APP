package com.echo.attendacesystem;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;


public class AttendanceManagement {

    public static String lectureDate = "", lectureStartTime = "", lectureEndTime = "", lectureSubject = "", studentsList, currentStudent, lectureWeek = "";
    public static String lectureStartTime_RAW = "", lectureEndTime_RAW="";
    public static int studentCount;
    public static boolean success;

    public static String weeksOnDatabase = "";

    //Firebase Auth Object
    private static FirebaseAuth mAuth;

    static String TAG = "ATTENDANCE APP";

    public static void logData(){
        Log.d(TAG, "logData: " + lectureDate + "  " + lectureStartTime + "   " + lectureEndTime + "  " + lectureSubject);
    }

}
