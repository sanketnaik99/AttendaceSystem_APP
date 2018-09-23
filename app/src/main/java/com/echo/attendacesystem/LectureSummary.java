package com.echo.attendacesystem;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.echo.attendacesystem.AttendanceManagement.lectureDate;
import static com.echo.attendacesystem.AttendanceManagement.lectureEndTime_RAW;
import static com.echo.attendacesystem.AttendanceManagement.lectureStartTime_RAW;
import static com.echo.attendacesystem.AttendanceManagement.lectureWeek;
import static com.echo.attendacesystem.AttendanceManagement.weeksOnDatabase;

public class LectureSummary extends AppCompatActivity {

    private static final String TAG = "LectureSummary";
    private ProgressBar progressBar;
    private TextView summaryTime, summarySubject, summaryDate, studentCount;
    private ListView studentList;
    private Button addToDatabase;
    private Map<String, Object> lecture = new HashMap<>();

    private boolean error;

    //Firebase Auth Object
    private FirebaseAuth mAuth;

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
        progressBar = findViewById(R.id.progressBar);
        addToDatabase = findViewById(R.id.summary_submit);
    }

    //Getting Data from Attendance Management Class and Updating UI
    private void updateData(){
        progressBar.setVisibility(View.INVISIBLE);
        summarySubject.setText(AttendanceManagement.lectureSubject);
        summaryDate.setText(lectureDate);
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

    //Button OnClick Method
    public void addToDatabase(View v) {
        addToDatabase.setActivated(false);
        FirestoreAsync task = new FirestoreAsync();
        task.execute();
    }


    //ASYNC CLASS
    private class FirestoreAsync extends AsyncTask<Void, Integer, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Make the Progressbar visible
            progressBar.setVisibility(View.VISIBLE);

            //Create The Lecture's MAP Object
            lecture.put("Subject", AttendanceManagement.lectureSubject);
            lecture.put("Date", lectureDate);
            lecture.put("StartTime", AttendanceManagement.lectureStartTime);
            lecture.put("EndTime",AttendanceManagement.lectureEndTime);
            lecture.put("StudentsPresent",AttendanceManagement.studentsList);
            lecture.put("StudentCount",AttendanceManagement.studentCount);
            lecture.put("Week", lectureWeek);

        }

        @Override
        protected String doInBackground(Void... voids) {


            //Initialize Firestore Database Object and set the settings
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
            db.setFirestoreSettings(settings);
            publishProgress(20);

            //Initialize Firebase Auth Object and get Current User's Mail
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            final String email = currentUser.getEmail();
            publishProgress(40);

            //Get the Weeks Data From Firebase
            db.collection(email).document("Weeks").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot weekList = task.getResult();
                        weeksOnDatabase = weekList.getString("Weeks");
                        publishProgress(60);
                        if(weeksOnDatabase == null){
                            weeksOnDatabase = lectureWeek;
                            updateWeeks(db, email);
                            publishProgress(80);
                        }
                        if(!weeksOnDatabase.contains(lectureWeek)){
                            weeksOnDatabase += "," + lectureWeek;
                            updateWeeks(db, email);
                            publishProgress(80);
                        }
                    }else{
                        Log.d(TAG, "onComplete: ERROR!");
                        error = true;
                    }
                }
            });

            //Publish Lecture Data to Firebase
            db.collection(email).document(lectureDate)
                    .collection(lectureDate).document(lectureStartTime_RAW + "-" + lectureEndTime_RAW).set(lecture).addOnSuccessListener(new OnSuccessListener<Void >() {
                @Override
                public void onSuccess(Void  avoid) {
                    Log.d(TAG, "DocumentSnapshot written with ID: ");
                    publishProgress(100);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                    error = true;
                }
            });


            if(!error){
                return "SUCCESS!";
            }else {
                return "ERROR";
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(LectureSummary.this, s, Toast.LENGTH_SHORT).show();
            progressBar.setProgress(0);
            progressBar.setVisibility(View.INVISIBLE);
        }

        private void updateWeeks(FirebaseFirestore db, String email){
            //Create Weeks Data Object
            Map<String, Object> weeksData = new HashMap<>();
            weeksData.put("Weeks", weeksOnDatabase);

            //Publish weeks data to firebase
            db.collection(email).document("Weeks").set(weeksData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: Success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: ERROR " + e);
                    error = true;
                }
            });
        }


    }

}

