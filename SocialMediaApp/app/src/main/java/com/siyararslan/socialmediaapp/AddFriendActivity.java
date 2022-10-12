package com.siyararslan.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.siyararslan.socialmediaapp.databinding.ActivityAddFriendBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity {

    private ActivityAddFriendBinding binding;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFriendBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();

    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain=new Intent(AddFriendActivity.this,FeedActivity.class);
        startActivity(intentToMain);
        finish();
    }


    public void addFriend(View view){
        String receiverEmail=binding.emailAddFriendText.getText().toString();
        FirebaseUser user= auth.getCurrentUser();
        String senderEmail=user.getEmail();



        HashMap<String,String> requestData=new HashMap<>();
        requestData.put("sender",senderEmail);
        requestData.put("receiver",receiverEmail);

        FirebaseFirestore.getInstance().collection("ProfileImages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(AddFriendActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if (value != null) {

                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        String downloadUri = (String) data.get("downloaduri");

                        String emailFromDatabase = (String) data.get("email");
                        if (emailFromDatabase.equals(senderEmail)) {

                            requestData.put("senderImgUri",downloadUri);
                        }

                        //friendRequestsAdapter.notifyDataSetChanged();
                    }

                    firebaseFirestore.collection("Requests").add(requestData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Intent intent=new Intent(AddFriendActivity.this,FeedActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddFriendActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });


    }


}