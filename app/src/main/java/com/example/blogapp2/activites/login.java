package com.example.blogapp2.activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blogapp2.Home;
import com.example.blogapp2.MainActivity;
import com.example.blogapp2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
EditText etMail,etPassword;
Button btnLogin,btnRegister;
ProgressBar progressBar;
FirebaseAuth firebaseAuth;
Intent homeActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        etMail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        btnLogin=findViewById(R.id.btnlogin);
        btnRegister=findViewById(R.id.btnsignup);
        progressBar=findViewById(R.id.progressBar);
        firebaseAuth=FirebaseAuth.getInstance();
        homeActivity=new Intent(this, Home.class);
        progressBar.setVisibility(View.INVISIBLE);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Intent in=new Intent(login.this, MainActivity.class);
                startActivity(in);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final  String email=etMail.getText().toString();
                final String password=etPassword.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    showMessage("please verify all fields");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    signIn(email,password);
                }
            }
        });
    }

    private void signIn(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    upDateUI();
                }
                else {
                    showMessage(task.getException().getMessage());
                }

            }
        });



    }

    private void upDateUI() {

        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String message) {

        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            upDateUI();
        }
    }
}
