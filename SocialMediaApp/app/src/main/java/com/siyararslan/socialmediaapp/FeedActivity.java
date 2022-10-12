package com.siyararslan.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.siyararslan.socialmediaapp.adapter.PostAdapter;
import com.siyararslan.socialmediaapp.databinding.ActivityFeedBinding;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {



    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    ArrayList<Post> postArrayList;
    ArrayList<String> allFriends;



    PostAdapter postAdapter;

    ActivityFeedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();

        allFriends=new ArrayList<>();
        postArrayList=new ArrayList<>();


        getAllFriends();
        getData();


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter=new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);
    }

    public void getAllFriends(){

        firebaseFirestore.collection("Friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if(value!=null){

                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String,Object> data=snapshot.getData();

                        String friend1= (String) data.get("friend1");
                        String friend2 =(String) data.get("friend2");


                        if(friend1.equals(auth.getCurrentUser().getEmail())){
                            allFriends.add(friend2);
                        }
                        else if(friend2.equals(auth.getCurrentUser().getEmail())){
                            allFriends.add(friend1);
                        }



                    }


                }

            }
        });

    }

    public void getData(){

        //firebaseFirestore.collection("Posts").whereEqualTo("useremail","james@gmail.com").addSna...//useremail'i james@gmail.com olanları getir
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if(value!=null){

                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String,Object> data=snapshot.getData();

                        String userEmail= (String) data.get("useremail");
                        String comment =(String) data.get("comment");
                        String postdownloadurl=(String) data.get("postdownloadurl");
                        String profileimgdownloadurl=(String) data.get("profileimgdownloadurl");

                        for(String user:allFriends){
                            if (user.equals(userEmail)||userEmail.equals(auth.getCurrentUser().getEmail())){
                                Post post=new Post(userEmail,comment,postdownloadurl,profileimgdownloadurl);
                                postArrayList.add(post);
                            }
                        }

//                        Post post=new Post(userEmail,comment,postdownloadurl,profileimgdownloadurl);
//                        postArrayList.add(post);


                    }

                    postAdapter.notifyDataSetChanged();
                }

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.addPost){
            Intent intent=new Intent(FeedActivity.this,UploadPostActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.Profile){
            Intent intent=new Intent(FeedActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.addFriend){
            Intent intent=new Intent(FeedActivity.this,AddFriendActivity.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId()==R.id.friendRequest){
            Intent intent=new Intent(FeedActivity.this,FriendRequestsActivity.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId()==R.id.friends){
            Intent intent=new Intent(FeedActivity.this,AllFriendsActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.signOut){
            auth.signOut();
            Intent intent=new Intent(FeedActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}