package com.echo.attendacesystem;

import android.content.Intent;
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

        //Getting the instance of Firebase Auth Object
        mAuth = FirebaseAuth.getInstance();

        //Checking if User is Signed In
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Try to Get the Display Name Of the User
        try {
            String Name = currentUser.getDisplayName();
            Log.d("ATTENDANCE APP", "onCreate: " + Name);
            displayName.setText("Hello, " + Name);
        }catch (NullPointerException e){
            Log.d("ATTENDANCE APP", "onCreate: ERROR! NULL ");
        }
    }

    //Initialize Views
    private void initViews(){
        displayName = findViewById(R.id.displayName);
    }

    //Edit User Profile
    public void editProfile(View v){
        startActivity(new Intent(HomeActivity.this,EditProfile.class));
    }

    //Add User Profile
    public void addUser(View v){
        startActivity(new Intent(HomeActivity.this, AddUser.class));
    }

    //Enter Scanner
    public void scan(View v){
        startActivity(new Intent(HomeActivity.this,ScanningMode.class));
    }
}
