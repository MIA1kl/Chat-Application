package com.example.android.chatapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText mGetPhoneNumber;
    android.widget.Button mSendCode;
    CountryCodePicker mCountryCodePicker;
    String countryCode;
    String phoneNumber;

    FirebaseAuth firebaseAuth;
    ProgressBar mProgressBarOfMain;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codeSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        mSendCode = findViewById(R.id.sendCodeBtn);
        mGetPhoneNumber = findViewById(R.id.getPhoneNumber);
        mProgressBarOfMain = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        countryCode = mCountryCodePicker.getSelectedCountryCodeWithPlus();
        mCountryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = mCountryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        mSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number;
                number = mGetPhoneNumber.getText().toString();
                if(number.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please, enter your number", Toast.LENGTH_SHORT).show();
                }
                else if(number.length()<9){
                    Toast.makeText(getApplicationContext(), "Please, enter correct number", Toast.LENGTH_SHORT).show();
                }
                else{
                    mProgressBarOfMain.setVisibility(View.VISIBLE);
                    phoneNumber = countryCode+number;
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(mCallbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted( PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent( String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(),"Code was sent", Toast.LENGTH_SHORT).show();
                mProgressBarOfMain.setVisibility(View.INVISIBLE);
                codeSend = s;
                Intent intent = new Intent(MainActivity.this, codeAuthentication.class);
                intent.putExtra("code", codeSend);
                startActivity(intent);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}