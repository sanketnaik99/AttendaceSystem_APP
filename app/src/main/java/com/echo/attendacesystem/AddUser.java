package com.echo.attendacesystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddUser extends AppCompatActivity {

    private TextInputEditText addUserEmail,addUserPassword,addUserDisplayName;
    private CheckBox isAdminCheckbox;
    private boolean isAdmin;

    public boolean success;
    public ProgressDialog progressDialog;

    public String displayName;

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
        displayName = addUserDisplayName.getText().toString();

        //Check if Admin
        if(isAdminCheckbox.isChecked())
            isAdmin = true;
        else
            isAdmin = false;


        if(!validate(email,password,displayName))
            return;
        else{

            progressDialog = new ProgressDialog(AddUser.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setMax(100);
            progressDialog.setMessage("Adding New User..");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            fireAuthNewUser(email,password,displayName);
        }

    }

    //Create User In Firebase Auth
    private void fireAuthNewUser(final String email, String password, final String displayName){

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
                                firestoreNewUser(email,isAdmin);
                            }else{
                                success = false;
                            }
                        }
                    });
                }else{
                    success = false;
                }
            }
        });
    }

    //Add User Data to Firestore Database
    private void firestoreNewUser(String email, boolean isAdmin){

        //Getting the Instance of Firebase Database
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        //Creating Object of the User
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("isAdmin", isAdmin);

        //Upload Data To Firestore Database
        database.collection("Users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                success = false;
            }
        });
    }


    //Method To Validate Credentials
    private boolean validate(String email, String password, String displayName){
        boolean valid = true;

        //Validate Email Address
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
            addUserEmail.setError("Enter a Valid Email Address!");
            valid = false;
        }else{
            addUserEmail.setError(null);
        }

        //Validate Password
        if(password.isEmpty() || password.length()<8){
            addUserPassword.setError("Please Enter a Valid Password!");
            valid = false;
        }else{
            addUserPassword.setError(null);
        }

        //Validate Display Name
        if(displayName.isEmpty() || displayName.length()<3){
            addUserDisplayName.setError("Please Enter A Valid Name");
        }else{
            addUserDisplayName.setError(null);
        }

        return valid;
    }
}
