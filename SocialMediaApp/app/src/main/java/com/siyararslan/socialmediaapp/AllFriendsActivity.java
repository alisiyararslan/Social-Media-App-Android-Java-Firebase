package com.siyararslan.socialmediaapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.siyararslan.socialmediaapp.adapter.FriendsAdapter;
import com.siyararslan.socialmediaapp.databinding.ActivityAllFriendsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class AllFriendsActivity extends AppCompatActivity {

    ActivityAllFriendsBinding binding;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    ArrayList<User> allFriends;
    FriendsAdapter friendsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllFriendsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        allFriends=new ArrayList<>();

        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();

        getData();

        binding.recyclerViewAllFriends.setLayoutManager(new LinearLayoutManager(this));
        friendsAdapter=new FriendsAdapter(allFriends,this.getApplicationContext());
        binding.recyclerViewAllFriends.setAdapter(friendsAdapter);

    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain=new Intent(AllFriendsActivity.this,FeedActivity.class);
        startActivity(intentToMain);
        finish();
    }



    public void getData(){
        firebaseFirestore.collection("Friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    Toast.makeText(AllFriendsActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                if(value!=null){
                    for (DocumentSnapshot snapshot:value.getDocuments()){
                        Map<String, Object> data=snapshot.getData();
                        String friend1=(String) data.get("friend1");
                        String friend2=(String)data.get("friend2");



                        if(friend1.equals(auth.getCurrentUser().getEmail())){
                            profileUri(friend2);

                        }
                        else if(friend2.equals(auth.getCurrentUser().getEmail())){

                            profileUri(friend1);

                        }

                    }
                    friendsAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    public void profileUri(String userEmail) {


        FirebaseFirestore.getInstance().collection("ProfileImages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(AllFriendsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if (value != null) {

                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        String downloadUri = (String) data.get("downloaduri");

                        String emailFromDatabase = (String) data.get("email");
                        if (emailFromDatabase.equals(userEmail)) {

                            allFriends.add(new User(userEmail,downloadUri));
                        }

                        friendsAdapter.notifyDataSetChanged();


                    }

                }

            }
        });



    }
}