package com.siyararslan.socialmediaapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.siyararslan.socialmediaapp.AllFriendsActivity;
import com.siyararslan.socialmediaapp.MessagesActivity;
import com.siyararslan.socialmediaapp.ProfileActivity;
import com.siyararslan.socialmediaapp.User;
import com.siyararslan.socialmediaapp.databinding.RecyclerRowAllFriendsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsHolder> {

    ArrayList<User> allFriends;
    private Context context;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    public FriendsAdapter(ArrayList<User> allFriends,Context context) {

        this.allFriends = allFriends;
        this.context=context;


        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();
    }

    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowAllFriendsBinding recyclerRowAllFriendsBinding=RecyclerRowAllFriendsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new FriendsHolder(recyclerRowAllFriendsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsHolder holder, int position) {

        String friendName=allFriends.get(position).getEmail();
        String profilUri=allFriends.get(position).getProfilePicture();

        holder.recyclerRowAllFriendsBinding.friendEmail.setText(friendName);
        if(!profilUri.equals(""))
            Picasso.get().load(profilUri).into(holder.recyclerRowAllFriendsBinding.imgPro);


//        if(allFriends.get(position).getProfilePicture()==null){
//            //holder.recyclerRowAllFriendsBinding.imgPro.setImageURI();
//        }else{
//            Picasso.get().load(getProfileUri(friendName)).into(holder.recyclerRowAllFriendsBinding.imgPro);
//        }


        holder.recyclerRowAllFriendsBinding.friendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, MessagesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//önceki activityleri kapatır finishe gerek kalmaz..
                intent.putExtra("messagereceiver",friendName);
                intent.putExtra("imagereceiver",profilUri);//do not work
                intent.putExtra("imageuser","");
                context.startActivity(intent);
                //finish();
            }
        });

    }



    @Override
    public int getItemCount() {
        return allFriends.size();
    }

    class FriendsHolder extends RecyclerView.ViewHolder{

        RecyclerRowAllFriendsBinding recyclerRowAllFriendsBinding;

        public FriendsHolder(RecyclerRowAllFriendsBinding recyclerRowAllFriendsBinding) {
            super(recyclerRowAllFriendsBinding.getRoot());
            this.recyclerRowAllFriendsBinding=recyclerRowAllFriendsBinding;
        }
    }
}
