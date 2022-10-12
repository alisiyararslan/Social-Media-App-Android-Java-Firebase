package com.siyararslan.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.siyararslan.socialmediaapp.adapter.FriendRequestsAdapter;
import com.siyararslan.socialmediaapp.databinding.ActivityFriendRequestsBinding;

import java.util.ArrayList;
import java.util.Map;

public class FriendRequestsActivity extends AppCompatActivity {

    FriendRequestsAdapter friendRequestsAdapter;
    ActivityFriendRequestsBinding binding;
    private FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;

    ArrayList<User> requestSenders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendRequestsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth=FirebaseAuth.getInstance();

        requestSenders=new ArrayList<>();
        firebaseFirestore=FirebaseFirestore.getInstance();

        getData();

        binding.recyclerViewFriendRequest.setLayoutManager(new LinearLayoutManager(this));
        friendRequestsAdapter=new FriendRequestsAdapter(requestSenders);//sender
        binding.recyclerViewFriendRequest.setAdapter(friendRequestsAdapter);

    }


    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain=new Intent(FriendRequestsActivity.this,FeedActivity.class);
        startActivity(intentToMain);
        finish();
    }

    public void getData(){

        firebaseFirestore.collection("Requests").whereEqualTo("receiver",auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    Toast.makeText(FriendRequestsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if(value!=null){

                    for(DocumentSnapshot snapshot:value.getDocuments()){

                        Map<String, Object> data=snapshot.getData();
                        String sender=(String) data.get("sender");
                        String senderImgUri=(String) data.get("senderImgUri");
                        requestSenders.add(new User(sender,senderImgUri));




                    }
                    friendRequestsAdapter.notifyDataSetChanged();//important

                }

            }
        });


    }
}