package com.echo.attendacesystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AddUser extends AppCompatActivity {

    private TextInputEditText addUserEmail,addUserPassword,addUserDisplayName;
    private CheckBox isAdminCheckbox;
    private boolean isAdmin;

    //Firebase Auth Object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        initViews();
    }

    //Initialize All Views
    private void initViews(){
        addUserEmail = findViewById(R.id.addUser_email);
        addUserDisplayName = findViewById(R.id.addUser_displayName);
        addUserPassword =  findViewById(R.id.addUser_password);
        isAdminCheckbox = findViewById(R.id.isAdminCheckbox);
    }


    //Add User to Firebase Auth and Firebase Database
    public void addUser(View v){

        //Get All Values From the form
        String email = addUserEmail.getText().toString();
        String password = addUserPassword.getText().toString();
        final String displayName = addUserDisplayName.getText().toString();

        //Check if Admin
        if(isAdminCheckbox.isChecked())
            isAdmin = true;
        else
            isAdmin = false;

        //Getting the instance of Firebase Auth Object
        mAuth = FirebaseAuth.getInstance();

        //Create User Using Firebase Auth
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //Updating User Details if User Successfully created
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("ATTENDANCE APP", "onComplete: User Details Updated" + user.getEmail() + "  " + user.getDisplayName());
                                //startActivity(new Intent(EditProfile.this,HomeActivity.class));
                            }else{
                                Log.d("ATTENDANCE APP", "onComplete: ERROR");
                            }
                        }
                    });
                }else{
                    Log.d("ATTENDANCE APP", "onComplete: ERROR");
                }
            }
        });


    }
}
