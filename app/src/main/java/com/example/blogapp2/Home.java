package com.example.blogapp2;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blogapp2.activites.login;
import com.example.blogapp2.fragnment.homeFragnment;
import com.example.blogapp2.fragnment.profileFragnment;
import com.example.blogapp2.fragnment.settingFragnment;
import com.example.blogapp2.models.post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth firebaseAuth;
    private  static final int PReqCode=2;
    static  int REQUESCODE=2;
    FirebaseUser currentUser;
    Dialog popAddPost;
    ImageView popoupUserImage,popupPostImage,popAaBtn;
    TextView popupTitle,popupdescription;
    ProgressBar popupProgressBar;
    private Uri pickedImageUri=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();

        //in1 popup

        inipopup();
        setupPopupImageClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();


            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        //set the home fragnment as the default one

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new homeFragnment()).commit();
    }




    private void setupPopupImageClick() {

        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here when image clicked we need to open gallery
                //before we open the gallery we  need to check if our aap have the access to user fies
                //we did this before in register activity I M just copy code to save time

                checkAndRequestPermission();

            }
        });
    }

    private void openGallery() {

        //TODO: open gallery intent and wait for user on pick an image!

        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    //when user picked an image



    private void checkAndRequestPermission() {

        if(ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Home.this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(this, "Pleasr accept for required permmisssion", Toast.LENGTH_SHORT).show();

            }
            else{
                ActivityCompat.requestPermissions(Home.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        }
        else {
            //everything goes will: we have permmission to use gallery
            openGallery();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==REQUESCODE && data !=null){
            //the user has successfully picked an image
            //we need to save its reference uri code

           pickedImageUri=data.getData();
           popupPostImage.setImageURI(pickedImageUri);


        }
    }

    private void inipopup() {
        popAddPost=new Dialog(this);

        popAddPost.setContentView(R.layout.poppup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity= Gravity.TOP;

        //ini popup widgets

        popoupUserImage=popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage=popAddPost.findViewById(R.id.popup_img);
        popupTitle=popAddPost.findViewById(R.id.popup_title);
        popupdescription=popAddPost.findViewById(R.id.popup_description);
        popAaBtn=popAddPost.findViewById(R.id.popup_add);
        popupProgressBar=popAddPost.findViewById(R.id.popup_progressBar);

        //load currentuser profile photo
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popoupUserImage);

        //Add Post click listner

        popAaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAaBtn.setVisibility(View.INVISIBLE);
                popupProgressBar.setVisibility(View.VISIBLE);

                //we need to check all input fields (title and descripton ) and postimage
                if(!popupTitle.getText().toString().isEmpty()
                    && !popupdescription.getText().toString().isEmpty()
                    && pickedImageUri!=null){
                    //everything is ok no empty or null value
                    //TODO create post object and add it to firebase database

                    //first we need to upload post image
                    //access firebase storage
                    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath=storageReference.child(pickedImageUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                               String imageDownloadLink=uri.toString();
                               //create post object
                                    post post=new post(popupTitle.getText().toString(),
                                            popupdescription.getText().toString(),
                                            imageDownloadLink,
                                            currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString()
                                            );

                                    //add post to firebase database
                                    addPost(post);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //something goes wrong to post
                                    showMessager(e.getMessage());
                                    popupProgressBar.setVisibility(View.INVISIBLE);
                                    popAaBtn.setVisibility(View.VISIBLE);


                                }
                            });

                        }
                    });
                }
                else {
                    showMessager("Please verify all input fields and choice post  Image");
                    popAaBtn.setVisibility(View.VISIBLE);
                    popupProgressBar.setVisibility(View.INVISIBLE);
                }


            }
        });

    }

    private void addPost(post post) {


        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myRef=firebaseDatabase.getReference("posts").push();
        //get post unique ID and update post key
        String key =myRef.getKey();
        post.setKey(key);


        //add post data to firebase databae
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessager("post Added successfuly");
                popupProgressBar.setVisibility(View.INVISIBLE);
                popAaBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });
    }

    private void showMessager(String message) {

        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            // Handle the camera action
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new homeFragnment()).commit();

        } else if (id == R.id.profile) {

            // Handle the camera action
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new profileFragnment()).commit();

        }  else if (id == R.id.settings) {

            // Handle the camera action
            getSupportActionBar().setTitle("Setting");
            getSupportFragmentManager().beginTransaction().replace(R.id.container,new settingFragnment()).commit();

        } else if (id == R.id.signOut) {

            FirebaseAuth.getInstance().signOut();
            Intent in=new Intent(Home.this, login.class);
            startActivity(in);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(){

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView=navigationView.getHeaderView(0);
        TextView navUserName=headerView.findViewById(R.id.user_name);
        TextView navEmail=headerView.findViewById(R.id.user_mail);
        ImageView navPhoto=headerView.findViewById(R.id.user_photo);

        navEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        //now we will use glid to load user image
        //first we need to import the libary

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navPhoto);


    }
}
