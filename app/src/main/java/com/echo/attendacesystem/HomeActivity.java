package com.echo.attendacesystem;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    //Firebase Auth Object
    private FirebaseAuth mAuth;
    private TextView displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        getUserDetails();
    }

    //Initialize Views
    private void initViews(){
        displayName = findViewById(R.id.displayName);
    }


    //Add User Profile
    public void addUser(View v){
        startActivity(new Intent(HomeActivity.this, AddUser.class));
    }

    //Edit User Profile
    public void editProfile(View v){
        startActivityForResult(new Intent(HomeActivity.this,EditProfile.class), 0);
    }

    //Go to Lecture Analysis
    public void lectureAnalysis(View v){
        startActivity(new Intent(HomeActivity.this,LectureAnalysis.class));
    }

    //Get the Edit Profile Result and Update the UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == R.string.edit_profile_result){
            getUserDetails();
        }
    }

    private void getUserDetails(){
        //Getting the instance of Firebase Auth Object
        mAuth = FirebaseAuth.getInstance();

        //Checking if User is Signed In
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Try to Get the Display Name Of the User
        try {
            String Name = currentUser.getDisplayName();
            displayName.setText("Hello, " + Name);
        }catch (NullPointerException e){
            Log.d("ATTENDANCE APP", "onCreate: ERROR! NULL ");
        }
    }

    //Enter Scanner
    public void scan(View v){
        startActivity(new Intent(HomeActivity.this,LectureDetails.class));
    }
}
