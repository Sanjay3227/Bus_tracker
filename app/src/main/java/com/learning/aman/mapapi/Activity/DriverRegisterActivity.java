package com.learning.aman.mapapi.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learning.aman.mapapi.MapsActivity;
import com.learning.aman.mapapi.R;
import java.util.HashMap;

public class DriverRegisterActivity extends AppCompatActivity {

    private EditText mName, mEmail, mPassword,mContact,mbus;
    private Button mSubmit;
    private ProgressDialog mProgressBar;

    //Firebase Auth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Database Reference
    private DatabaseReference mUserDetails = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        init();
        onClick();

    }

    private void init() {
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mContact = findViewById(R.id.contact);
        mbus = findViewById(R.id.typeofbus);
        mPassword =  findViewById(R.id.password);
        mSubmit = findViewById(R.id.confirm);
    }

    private void onClick() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String contact = mContact.getText().toString();
                String bus = mbus.getText().toString();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)&& !TextUtils.isEmpty(bus)&& !TextUtils.isEmpty(contact)){
                    mProgressBar = new ProgressDialog(DriverRegisterActivity.this);
                    mProgressBar.setMessage("Registering....");
                    mProgressBar.setCanceledOnTouchOutside(false);
                    mProgressBar.show();
                    register(name, email, password, bus, contact);

                }else {
                    Toast.makeText(DriverRegisterActivity.this, "Enter Vaild email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register(final String name, final String email, final String password,final String bus,final String contact) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();

                            HashMap<String,String> userDetails = new HashMap<>();
                            userDetails.put("name",name);
                            userDetails.put("email",email);
                            userDetails.put("password",password);
                            userDetails.put("contact",contact);
                            userDetails.put("bus",bus);

                            mUserDetails.child("Users").child("Driver").child(uid).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(DriverRegisterActivity.this, "Creating Account Successfull", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(DriverRegisterActivity.this, MapsActivity.class));
                                        finish();
                                        mProgressBar.dismiss();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(DriverRegisterActivity.this,"Creating Account Failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
