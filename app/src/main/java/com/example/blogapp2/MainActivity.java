package com.example.blogapp2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blogapp2.activites.homeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
     ImageView userPhoto;
    static  int PReqCode=1;
    static  int REQUESCODE=1;
    Uri pickedImageUri;
    EditText userEmail,userPassword,userPassword2,userName;
    ProgressBar progressBar;
    Button btnRegister;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        //img view

        userPhoto = findViewById(R.id.img_user);
        userEmail = findViewById(R.id.etEmail);
        userName = findViewById(R.id.etName);
        userPassword = findViewById(R.id.etPassword);
        userPassword2 = findViewById(R.id.confPassword);
        btnRegister = findViewById(R.id.button4);

        mAuth=FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String email = userEmail.getText().toString();
                final String name = userName.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();

                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || !password.equals(password2)) {
                    showMessage("please verfify all fields");
                } else {
                    createUserAccount(email, name, password);
                }
            }
        });


        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT>=22){
                    checkAndRequestPermission();
                }
                else{
                    openGallery();
                }

            }
        });



    }

    private void createUserAccount(String email, final String name, String password) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                       // progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                           // startActivity(new Intent(getApplicationContext(),setUpActivity.class));
                            //Toast.makeText(MainActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();

                             showMessage("account created");
                             updateUserInfo(name,pickedImageUri,mAuth.getCurrentUser());


                        } else {
                            //Toast.makeText(MainActivity.this, "Authecation failed", Toast.LENGTH_SHORT).show();
                       showMessage("account creation failed "+task.getException().getMessage());
                        }
                    }
                });

    }
//update user photo and name
    private void updateUserInfo(final String name, final Uri pickedImageUri, final FirebaseUser currentUser) {

        StorageReference mStorage= FirebaseStorage.getInstance().getReference().child("user  photos");
        final StorageReference imageFilePath=mStorage.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

               imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                        UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(userProfileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful()){
                                               showMessage("Register Complete");
                                               updateUI();
                                           }

                                    }
                                });
                   }
               });
            }
        });

    }

    private void updateUI() {
        Intent in=new Intent(getApplicationContext(), Home.class);
        startActivity(in);
        finish();
    }


    private void showMessage(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {

        //TODO: open gallery intent and wait for user on pick an image!

        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestPermission() {

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(this, "Pleasr accept for required permmisssion", Toast.LENGTH_SHORT).show();

            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==REQUESCODE && data !=null){
            //the user has successfully picked an image
            //we need to save its reference uri code

            pickedImageUri=data.getData();
            userPhoto.setImageURI(pickedImageUri);

        }
    }
}
