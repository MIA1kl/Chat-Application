package com.example.android.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileSet extends AppCompatActivity {

    private EditText mGetUsername;
    private android.widget.Button mSaveProfile;
    private FirebaseAuth firebaseAuth;
    private String name;
    ProgressBar mProgressBarForSetProfile;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_set);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        mGetUsername=findViewById(R.id.getUserName);
        mSaveProfile=findViewById(R.id.saveProfileBtn);
        mProgressBarForSetProfile=findViewById(R.id.progressBarForSetProfile);

        mSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=mGetUsername.getText().toString();
                if(name.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Name is Empty",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    mProgressBarForSetProfile.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    mProgressBarForSetProfile.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(ProfileSet.this,ChatActivity.class);
                    startActivity(intent);
                    finish();


                }
            }
        });
    }

    private void sendDataForNewUser()
    {
        sendDataToRealTimeDatabase();
    }

    private void sendDataToRealTimeDatabase()
    {


        name=mGetUsername.getText().toString().trim();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        UserProfile mUserProfile=new UserProfile(name,firebaseAuth.getUid());
        databaseReference.setValue(mUserProfile);
        Toast.makeText(getApplicationContext(),"User Profile Added Successfully",Toast.LENGTH_SHORT).show();
        sendDataToCloudFirestore();
    }

    private void sendDataToCloudFirestore() {


        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String , Object> userdata=new HashMap<>();
        userdata.put("name",name);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Data on Cloud Firestore send success",Toast.LENGTH_SHORT).show();

            }
        });



    }


}