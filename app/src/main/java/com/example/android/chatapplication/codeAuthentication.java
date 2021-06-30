package com.example.android.chatapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class codeAuthentication extends AppCompatActivity {

    TextView mChangeNumber;
    EditText mGetCode;
    android.widget.Button mVerifyCode;
    String enteredCode;

    FirebaseAuth firebaseAuth;
    ProgressBar mProgressbarForAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_authentication);

        mChangeNumber =findViewById(R.id.changeNumber);
        mVerifyCode =findViewById(R.id.verifyCode);
        mGetCode =findViewById(R.id.getPhoneCode);
        mProgressbarForAuth =findViewById(R.id.progressBarForAuth);

        firebaseAuth=FirebaseAuth.getInstance();

        mChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(codeAuthentication.this,MainActivity.class);

                startActivity(intent);
            }
        });

        mVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredCode = mGetCode.getText().toString();
                if(enteredCode.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Enter your Code First ",Toast.LENGTH_SHORT).show();
                }
                else

                {
                    mProgressbarForAuth.setVisibility(View.VISIBLE);
                    String coderecieved=getIntent().getStringExtra("code");
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(coderecieved, enteredCode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });



    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    mProgressbarForAuth.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"Login success",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(codeAuthentication.this,ProfileSet.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        mProgressbarForAuth.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}