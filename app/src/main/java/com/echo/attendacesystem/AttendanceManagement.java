package com.echo.attendacesystem;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class AttendanceManagement {

    public static String lectureDate = "", lectureStartTime = "", lectureEndTime = "", lectureSubject = "", studentsList, currentStudent;
    public static String lectureStartTime_RAW = "", lectureEndTime_RAW="";
    public static int studentCount, lectureWeek;
    public static boolean success;

    //Firebase Auth Object
    private static FirebaseAuth mAuth;

    static String TAG = "ATTENDANCE APP";

    public static void logData(){
        Log.d(TAG, "logData: " + lectureDate + "  " + lectureStartTime + "   " + lectureEndTime + "  " + lectureSubject);
    }

    public static boolean publishData(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
        db.setFirestoreSettings(settings);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String email = currentUser.getEmail();

        Map<String, Object> lecture = new HashMap<>();
        lecture.put("Subject", lectureSubject);
        lecture.put("Date", lectureDate);
        lecture.put("StartTime", lectureStartTime);
        lecture.put("EndTime",lectureEndTime);
        lecture.put("StudentsPresent",studentsList);
        lecture.put("StudentCount",studentCount);
        lecture.put("Week",lectureWeek);

        logData();
        db.collection(email).document(lectureDate)
                .collection(lectureDate).document(lectureStartTime_RAW + "-" + lectureEndTime_RAW).set(lecture).addOnSuccessListener(new OnSuccessListener<Void >() {
            @Override
            public void onSuccess(Void  avoid) {
                Log.d(TAG, "DocumentSnapshot written with ID: ");
                success = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                success = false;
            }
        });
        return success;
    }
}
