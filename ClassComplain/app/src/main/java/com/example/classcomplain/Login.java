package com.example.classcomplain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText email,password;
    Button signin;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    CheckBox saveLoginCheckBox;
    String deviceid , token, campus;
    HashMap<String,String> camhash= new HashMap<String, String>();
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgress =new ProgressDialog(this);
        mProgress.setMessage("Loading...");
        mProgress.setCancelable(false);

        email =(EditText) findViewById(R.id.email);
        password =(EditText) findViewById(R.id.password);
        signin =(Button) findViewById(R.id.email_sign_in_button);
        mAuth = FirebaseAuth.getInstance();

        saveLoginCheckBox = (CheckBox)findViewById(R.id.saveLoginCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            email.setText(loginPreferences.getString("username", ""));
            password.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    finish();
                    //mProgress.dismiss();
                    Intent myintent= new Intent(Login.this, MainActivity.class);
                    //myintent.putExtra("campusid",campus);
                    startActivity(myintent);
                    // User is signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    signin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //String email = mEmailView.getText().toString();
                            //String password = mPasswordView.getText().toString();
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

                            if (saveLoginCheckBox.isChecked()) {
                                loginPrefsEditor.putBoolean("saveLogin", true);
                                loginPrefsEditor.putString("username", email.getText().toString());
                                loginPrefsEditor.putString("password", password.getText().toString());
                                loginPrefsEditor.commit();
                            } else {
                                loginPrefsEditor.clear();
                                loginPrefsEditor.commit();
                            }
                                mProgress.setMessage("Signing in...");
                                mProgress.show();
                                signin();

                   /*         finish();
                            //mProgress.dismiss();
                            Intent myintent= new Intent(Login.this, MainActivity.class);
                            //myintent.putExtra("msg",carnum);
                            startActivity(myintent);*/
                        }
                    });
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signin() {

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, "Sign in Failed.",Toast.LENGTH_SHORT).show();
                            password.setText("");
                            mProgress.dismiss();
                            //Log.w(TAG, "signInWithEmail:failed", task.getException());
                            /*Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();*/
                        }
                        else
                        {
                            /*UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName("Admin: Basit Khan").build();
                            //FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/accounttype").setValue("admin");
                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                            DatabaseReference usersref = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
                            usersref.child("name").setValue("Basit Khan");
                            usersref.child("designation").setValue("Principal");
                            usersref.child("campus").setValue("1");
                            usersref.child("canresolvecomplain").setValue("true");
                            usersref.child("canassign").setValue("true");
                            usersref.child("canopen").setValue("true");
                            usersref.child("emailid").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            usersref.child("role").setValue("Super Admin");*/

                            deviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
                            //deviceid = mngr.getDeviceId().toString();
                            token = FirebaseInstanceId.getInstance().getToken();

                            DatabaseReference usersref1 = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/devices/");
                            usersref1.child(deviceid).setValue(token);
                            finish();
                            Intent myintent= new Intent(Login.this, MainActivity.class);
                            //myintent.putExtra("campusid",campus);
                            startActivity(myintent);


                            /*DatabaseReference datasref = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/campus/");
                            datasref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                   campus =dataSnapshot.getValue().toString();
                                    Toast.makeText(getApplicationContext(),campus,Toast.LENGTH_SHORT).show();
                                    mProgress.dismiss();
                                    SharedPreferences.Editor editor = getSharedPreferences("campus", MODE_PRIVATE).edit();
                                    editor.putString("campus", campus);
                                    editor.commit();
                                    //SharedPreferences editors = getSharedPreferences("campus", MODE_PRIVATE);
                                    //Toast.makeText(getApplicationContext(),editors.getString("campus",""),Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent myintent= new Intent(Login.this, MainActivity.class);
                                    //myintent.putExtra("campusid",campus);
                                    startActivity(myintent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });*/

                        }
                        // ...
                    }
                });
    }


}
