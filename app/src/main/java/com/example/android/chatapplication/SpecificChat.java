package com.example.android.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SpecificChat extends AppCompatActivity {
    EditText mGetMessage;
    ImageButton mSendMessageButton;

    CardView mSendMessageCardView;
    androidx.appcompat.widget.Toolbar mToolbarOfSpecificChat;
    TextView mNameOfSpecificUser;
    FirebaseFirestore firebaseFirestore;

    private String enteredMessage;
    Intent intent;
    String mReceiverName, senderName, mRecieverUid, mSenderUid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String senderRoom, receiverRoom;

    ImageButton mBackButtonOfSpecificChat;

    RecyclerView mMessageRecyclerView;

    String currentTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);

        mGetMessage =findViewById(R.id.getMessage);
        mSendMessageCardView =findViewById(R.id.cardViewOfSendMessage);
        mSendMessageButton =findViewById(R.id.imageViewSendMessage);
        mToolbarOfSpecificChat =findViewById(R.id.toolbarOfSpecificChat);
        mNameOfSpecificUser =findViewById(R.id.nameOfSpecificUser);
        mBackButtonOfSpecificChat =findViewById(R.id.backButtonOfSpecificChat);

        messagesArrayList=new ArrayList<>();
        mMessageRecyclerView =findViewById(R.id.recyclerViewOfSpecific);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter=new MessagesAdapter(SpecificChat.this,messagesArrayList);
        mMessageRecyclerView.setAdapter(messagesAdapter);
        firebaseFirestore=FirebaseFirestore.getInstance();



        intent=getIntent();

        setSupportActionBar(mToolbarOfSpecificChat);
        mToolbarOfSpecificChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Toolbar is Clicked",Toast.LENGTH_SHORT).show();


            }
        });

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh:mm a");


        mSenderUid =firebaseAuth.getUid();
        mRecieverUid =getIntent().getStringExtra("receiveruid");
        mReceiverName =getIntent().getStringExtra("name");



        senderRoom = mSenderUid + mRecieverUid;
        receiverRoom = mRecieverUid + mSenderUid;



        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");
        messagesAdapter=new MessagesAdapter(SpecificChat.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Messages messages=snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        mBackButtonOfSpecificChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mNameOfSpecificUser.setText(mReceiverName);



        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredMessage = mGetMessage.getText().toString();
                if(enteredMessage.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Enter message first",Toast.LENGTH_SHORT).show();
                }

                else

                {
                    Date date=new Date();
                    currentTime =simpleDateFormat.format(calendar.getTime());
                    Messages messages=new Messages(enteredMessage,firebaseAuth.getUid(),date.getTime(), currentTime);
                    firebaseDatabase=FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    });

                    mGetMessage.setText(null);




                }




            }
        });




    }


    @Override
    protected void onStop() {
        super.onStop();
        if(messagesAdapter!=null)
        {
            messagesAdapter.notifyDataSetChanged();
        }

        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

}