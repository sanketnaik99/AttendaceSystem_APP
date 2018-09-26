package com.echo.attendacesystem;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class LectureAnalysis extends AppCompatActivity {

    ProgressBar progressBar2;
    Spinner weekSpinner, subjectSpinner;
    Button go;
    private static final String TAG = "LectureAnalysis";
    private List<String> weeks = new ArrayList<String>();
    private String[] subjectList = new String[]{ "ALL", "AM-3", "DS", "OOPM", "DM", "ECCF", "DLDA" };

    private List<Float> dayCount = new ArrayList<>();
    private List<Float> studentCount = new ArrayList<>();
    private static String[] dateLabel;

    private static int counter;
    private float count;

    private LineGraphSeries dataSeries;

    GraphView graph;

    Query query;

    //Firebase Auth Object
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_analysis);

        initViews();
        GetWeekFirestoreData task = new GetWeekFirestoreData();
        task.execute();
    }

    private void initViews(){
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.VISIBLE);

        go = findViewById(R.id.lecture_analysis_go);
        go.setActivated(false);

        weekSpinner = findViewById(R.id.week_spinner);

        dataSeries = new LineGraphSeries<>();

        graph = findViewById(R.id.graph);
        graph.setVisibility(View.INVISIBLE);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Number of Students Present");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Lecture Date and Time");
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(9);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(20);




        //Spinner Content
        subjectSpinner = findViewById(R.id.subject_spinner);
        ArrayAdapter<String> subjectsSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectList);
        subjectSpinner.setAdapter(subjectsSpinnerAdapter);
    }

    public void GetLectureData(View v){
        String week = weekSpinner.getSelectedItem().toString();
        String subject = subjectSpinner.getSelectedItem().toString();
        Log.d(TAG, "GetLectureData: " + week + subject);
        String[] input = new String[]{week, subject};
        getLectureFirestoreData task = new getLectureFirestoreData();
        task.execute(input);

    }



    //ASYNC TASKS
    //Getting Initial Data from Firebase Async Task
    private class GetWeekFirestoreData extends AsyncTask<Void,Integer, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar2.setProgress(0);
            progressBar2.setVisibility(View.VISIBLE);
            go.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
            db.setFirestoreSettings(settings);
            publishProgress(30);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            publishProgress(60);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            publishProgress(80);
            try {
                Thread.sleep(200);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            db.collection("Lectures").document("Weeks").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        String weeks_RAW = documentSnapshot.getData().get("Weeks").toString();
                        String[] weeks_ARR = weeks_RAW.split(",");
                        for(int i =0;i<weeks_ARR.length;i++){
                            weeks.add(weeks_ARR[i]);
                        }
                        ArrayAdapter<String> weekSpinnerAdapter = new ArrayAdapter<>(LectureAnalysis.this,android.R.layout.simple_spinner_dropdown_item, weeks_ARR);
                        weekSpinner.setAdapter(weekSpinnerAdapter);
                        publishProgress(90);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        publishProgress(100);
                    }else{
                        Log.d(TAG, "onSuccess: SnapShot Does not Exist");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e);
                }
            });

            return "Success";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar2.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressBar2.setProgress(0);
            progressBar2.setVisibility(View.INVISIBLE);
            go.setVisibility(View.VISIBLE);
        }
    }


    //Getting Lecture Data from Firestore for Plotting
    private class getLectureFirestoreData extends AsyncTask<String,Integer,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar2.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
            db.setFirestoreSettings(settings);
            publishProgress(25);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            String email = user.getEmail();
            publishProgress(50);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CollectionReference lectureCollection = db.collection("Lectures");

            if(strings[1].equals("ALL")){
                query = lectureCollection.whereEqualTo("Week", strings[0]).orderBy("Date",Query.Direction.ASCENDING).orderBy("StartTime", Query.Direction.ASCENDING);
            }else{
                query = lectureCollection.whereEqualTo("Week",strings[0]).whereEqualTo("Subject", strings[1]).orderBy("Date",Query.Direction.ASCENDING).orderBy("StartTime",Query.Direction.ASCENDING);
            }
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    count = 1;
                    for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                        Log.d(TAG, "onSuccess: " + document.getData());
                        int student_Count_INT = document.getData().get("StudentCount").hashCode();
                        float student_Count_FLOAT = student_Count_INT;
                        studentCount.add(student_Count_FLOAT);
                        dayCount.add(count);
                        Log.d(TAG, "onSuccess: " + studentCount + dayCount);
                        count++;
                    }
                    publishProgress(75);
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "doInBackground: RUNNING LOOP");
                    counter=0;
                    while (counter<studentCount.size()){
                        dataSeries.appendData(new DataPoint(dayCount.get(counter),studentCount.get(counter)),false,40);
                        Log.d(TAG, "doInBackground: " + dataSeries);
                        counter +=1;
                    }
                    publishProgress(100);
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: TASK FAILED" + e) ;
                }
            });

            return "SUCCESS";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar2.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar2.setVisibility(View.INVISIBLE);
            graph.addSeries(dataSeries);
            graph.setVisibility(View.VISIBLE);
            go.setVisibility(View.INVISIBLE);
        }
    }
}
