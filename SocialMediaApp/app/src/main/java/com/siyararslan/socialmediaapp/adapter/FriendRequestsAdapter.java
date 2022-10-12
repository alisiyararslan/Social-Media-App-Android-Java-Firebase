package com.siyararslan.socialmediaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.siyararslan.socialmediaapp.User;
import com.siyararslan.socialmediaapp.databinding.RecyclerRowFriendRequestsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.RequestHolder> {

    ArrayList<User> requests;//sender



    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    public FriendRequestsAdapter(ArrayList<User> requests) {
        this.requests = requests;
        auth=FirebaseAuth.getInstance();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        storageReference=firebaseStorage.getReference();
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerRowFriendRequestsBinding recyclerRowFriendRequestsBinding=RecyclerRowFriendRequestsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new RequestHolder(recyclerRowFriendRequestsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
        String currentSender=requests.get(position).getEmail();
        String currentSenderImgUri=requests.get(position).getProfilePicture();

        Picasso.get().load(currentSenderImgUri).into(holder.recyclerRowFriendRequestsBinding.friendRequestSmallProfileImage);
        holder.recyclerRowFriendRequestsBinding.senderEmail.setText(currentSender);



        holder.recyclerRowFriendRequestsBinding.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,String> friends=new HashMap<>();
                friends.put("friend1",auth.getCurrentUser().getEmail());
                friends.put("friend2",currentSender);

                firebaseFirestore.collection("Friends").add(friends).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });


                //delete requests
                deleteRequest(currentSender);


            }
        });

        holder.recyclerRowFriendRequestsBinding.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //delete requests
                deleteRequest(currentSender);

            }
        });

    }

    public void deleteRequest(String currentSender){
        firebaseFirestore.collection("Requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    //Toast.makeText(FriendRequestsActivity.class, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if(value!=null){

                    for(DocumentSnapshot snapshot:value.getDocuments()){

                        Map<String, Object> data=snapshot.getData();
                        String sender=(String) data.get("sender");
                        String reciever=(String) data.get("receiver");

                        if (currentSender.equals(sender)&&reciever.equals(auth.getCurrentUser().getEmail())){

                           // System.out.println(snapshot.getId());
                            firebaseFirestore.collection("Requests").document(snapshot.getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Log.w(TAG, "Error deleting document", e);
                                        }
                                    });
                        }
                    }
                }
            }
        });


        updateRequestList(requests);

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void updateRequestList(ArrayList<User> newlist) {// update recyclerView
        requests.clear();
        requests.addAll(newlist);
        this.notifyDataSetChanged();
    }

    class RequestHolder extends RecyclerView.ViewHolder{

        RecyclerRowFriendRequestsBinding recyclerRowFriendRequestsBinding;

        public RequestHolder(RecyclerRowFriendRequestsBinding recyclerRowFriendRequestsBinding) {
            super(recyclerRowFriendRequestsBinding.getRoot());
            this.recyclerRowFriendRequestsBinding=recyclerRowFriendRequestsBinding;
        }
    }



}
