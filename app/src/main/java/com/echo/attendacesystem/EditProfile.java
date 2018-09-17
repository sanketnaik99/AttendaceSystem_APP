package com.echo.attendacesystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfile extends AppCompatActivity {

    private TextInputEditText editModeEmail, editModeDisplayName;

    //Firebase Auth Object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Getting the instance of Firebase Auth Object
        mAuth = FirebaseAuth.getInstance();

        //Getting Current User
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Initialize Views
        initViews();

        //Get User Data and Update Fields
        getUserData(currentUser);
    }

    private void initViews(){
        editModeEmail = findViewById(R.id.editMode_email);
        editModeDisplayName = findViewById(R.id.editMode_displayName);
    }

    private void getUserData(FirebaseUser current){
        String email = current.getEmail();
        String displayName = current.getDisplayName();

        editModeEmail.setText(email);
        editModeDisplayName.setText(displayName);
    }

    public void updateProfile(View v){
        String displayName = editModeDisplayName.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("ATTENDANCE APP", "onComplete: User Details Updated");
                    startActivity(new Intent(EditProfile.this,HomeActivity.class));
                }
            }
        });
    }
}
