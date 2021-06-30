package com.example.android.chatapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;

    FirestoreRecyclerAdapter<FirebaseModel,NoteViewHolder> chatAdapter;

    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.chat_fragment,container,false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mRecyclerView = v.findViewById(R.id.recyclerView);

        Query query = firebaseFirestore.collection("Users");
        FirestoreRecyclerOptions<FirebaseModel> allUsername = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query,FirebaseModel.class).build();
        chatAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allUsername) {
            @Override
            protected void onBindViewHolder(@NonNull ChatFragment.NoteViewHolder noteViewHolder, int i, @NonNull FirebaseModel firebaseModel) {
                noteViewHolder.particularUsername.setText(firebaseModel.getName());

                if(firebaseModel.getStatus().equals("Online")){
                    noteViewHolder.statusOfUser.setText(firebaseModel.getStatus());
                    noteViewHolder.statusOfUser.setTextColor(Color.GREEN);
                }
                else{
                    noteViewHolder.statusOfUser.setText(firebaseModel.getStatus());

                }

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(),"Item is clicked",Toast.LENGTH_SHORT).show();
                    }
                });

            }


            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_view_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(chatAdapter);
        return v;

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView particularUsername;
        private TextView statusOfUser;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            particularUsername = itemView.findViewById(R.id.nameOfUser);
            statusOfUser = itemView.findViewById(R.id.statusOfUser);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAdapter!=null)
        {
            chatAdapter.stopListening();
        }
    }
}