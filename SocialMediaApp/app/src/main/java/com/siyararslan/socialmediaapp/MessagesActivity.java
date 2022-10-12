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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.siyararslan.socialmediaapp.adapter.MessagesAdapter;
import com.siyararslan.socialmediaapp.databinding.ActivityMessagesBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {

    private ActivityMessagesBinding binding;
    Intent intent;

    MessagesAdapter messagesAdapter;

    private FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;

    ArrayList<Message> messages;
    String messageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        intent=getIntent();




        auth=FirebaseAuth.getInstance();
        messageReceiver=intent.getStringExtra("messagereceiver");
        binding.emailChattingWith.setText(messageReceiver);
        firebaseFirestore=FirebaseFirestore.getInstance();


        messages=new ArrayList<>();
        getData();



        binding.recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        messagesAdapter=new MessagesAdapter(messages);
        binding.recyclerViewMessages.setAdapter(messagesAdapter);






    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain=new Intent(MessagesActivity.this,FeedActivity.class);
        startActivity(intentToMain);
        finish();
    }

    public void getData(){


        //.whereEqualTo("sender",auth.getCurrentUser().getEmail()).whereEqualTo("receiver",messageReceiver)
        firebaseFirestore.collection("Messages").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(MessagesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(error.getLocalizedMessage());
                }

                if(value!=null){
                    messages.clear();
                    for(DocumentSnapshot snapshot:value.getDocuments()){


                        Map<String,Object> data=snapshot.getData();
                        String message= (String) data.get("message");
                        String receiverImgUri= (String) data.get("receiverImgUri");
                        String senderImgUri= (String) data.get("senderImgUri");
                        if((auth.getCurrentUser().getEmail().equals(data.get("sender"))&&messageReceiver.equals(data.get("receiver")))){

                            Picasso.get().load(receiverImgUri).into(binding.imgToolBar);
                            messages.add(new Message(auth.getCurrentUser().getEmail(),messageReceiver,message,senderImgUri,receiverImgUri));//message
                        }else if((auth.getCurrentUser().getEmail().equals(data.get("receiver"))&&messageReceiver.equals(data.get("sender")))){

                            messages.add(new Message(messageReceiver,auth.getCurrentUser().getEmail(),message,senderImgUri,receiverImgUri));//message

                        }


                    }
                    messagesAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void sendButtonClicked(View view){

        String message=binding.editTextMessage.getText().toString();
        binding.editTextMessage.setText("");
        if (!message.equals("")){
            HashMap<String,Object> requestData=new HashMap<>();
            requestData.put("sender",auth.getCurrentUser().getEmail());
            requestData.put("receiver",messageReceiver);
            requestData.put("message",message);
            requestData.put("date", FieldValue.serverTimestamp());//güncel tarih

            FirebaseFirestore.getInstance().collection("ProfileImages").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(MessagesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (value != null) {

                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Map<String, Object> data = snapshot.getData();

                            String downloadUri = (String) data.get("downloaduri");

                            String emailFromDatabase = (String) data.get("email");
                            if (emailFromDatabase.equals(auth.getCurrentUser().getEmail())) {

                                requestData.put("senderImgUri",downloadUri);
                            } else if(emailFromDatabase.equals(messageReceiver)){
                                requestData.put("receiverImgUri",downloadUri);
                            }

                            messagesAdapter.notifyDataSetChanged();





                        }
                        firebaseFirestore.collection("Messages").add(requestData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MessagesActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                System.out.println(e.getLocalizedMessage());
                            }
                        });

                    }

                }
            });





        }
        else{
            Toast.makeText(MessagesActivity.this,"Enter a message!",Toast.LENGTH_LONG).show();
        }


    }




}