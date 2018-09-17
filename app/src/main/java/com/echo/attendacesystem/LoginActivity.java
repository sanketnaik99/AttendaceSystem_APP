package com.echo.attendacesystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText useremail, userpassword;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    //Firebase Auth Object
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        //Getting the instance of Firebase Auth Object
        mAuth = FirebaseAuth.getInstance();

        //Checking if User is Signed In
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //if(currentUser != null)
          //  toNextActivity();
    }

    //Method to Initialize all the Views in the activity
    private void initViews(){
        useremail = (TextInputEditText)findViewById(R.id.user_email);
        userpassword = (TextInputEditText)findViewById(R.id.user_password);
        saveLoginCheckBox = (CheckBox)findViewById(R.id.saveLoginCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            useremail.setText(loginPreferences.getString("username", ""));
            userpassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }

    }

    //Primary Form Submit Method
    public void formSubmit(View v){

        //Get Credentials from From
        String email = useremail.getText().toString();
        String password = userpassword.getText().toString();

        //Check if the Form data is Valid
        if(!validate(email,password))
            return;
        else
            checkAndSaveCredentials(email,password);
            loginWithEmailAndPassword(email,password);
    }

    //Method To Validate Credentials
    private boolean validate(String email, String password){
        boolean valid = true;

        //Validate Email Address
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
            useremail.setError("Enter a Valid Email Address!");
            valid = false;
        }else{
            useremail.setError(null);
        }

        //Validate Password
        if(password.isEmpty() || password.length()<8){
            userpassword.setError("Please Enter a Valid Password!");
            valid = false;
        }else{
            userpassword.setError(null);
        }
        return valid;
    }

    //Method to check for REMEMBER ME toggle and Perform Actions Accordingly
    private void checkAndSaveCredentials(String email, String password){

        //Save Credentials Using Shared Preferences
        if (saveLoginCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", email);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
    }


    //Login with Email and Password On Firebase
    private void loginWithEmailAndPassword(String email, String password){

        //Creating Progress Dialog
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setMax(100);
        progressDialog.setMessage("Authenticating..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //Sign in Using Email And Password on Firebase
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("ATTENDACE APP", "signInWithEmail:success");
                    toNextActivity();
                    progressDialog.dismiss();
                }else{
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

    }

    //Method to Move to Next Activity
    private void toNextActivity(){
        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
    }
}
