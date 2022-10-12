package com.siyararslan.socialmediaapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.siyararslan.socialmediaapp.Message;
import com.siyararslan.socialmediaapp.databinding.RecyclerRowMessagesBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageHolder> {

    ArrayList<Message> messages;
    private FirebaseAuth auth;

    public MessagesAdapter(ArrayList<Message> messages) {
        this.messages = messages;
        auth=FirebaseAuth.getInstance();
        System.out.println(messages.size());

    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowMessagesBinding recyclerRowMessagesBinding=RecyclerRowMessagesBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MessageHolder(recyclerRowMessagesBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        String message=messages.get(position).getContext();
        holder.recyclerRowMessagesBinding.message.setText(message);//?


        ConstraintLayout constraintLayout=holder.recyclerRowMessagesBinding.ccLayout;
        if(messages.get(position).getSender().equals(auth.getCurrentUser().getEmail())){//user send message
            //UPLOAD PHOTO
            Picasso.get().load(messages.get(position).getSenderImgUri()).into(holder.recyclerRowMessagesBinding.smallProfileImage);
            ConstraintSet constraintSet=new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(holder.recyclerRowMessagesBinding.profileCardview.getId(),ConstraintSet.LEFT);
            constraintSet.clear(holder.recyclerRowMessagesBinding.message.getId(),ConstraintSet.LEFT);
            constraintSet.connect(holder.recyclerRowMessagesBinding.profileCardview.getId(),ConstraintSet.RIGHT,
                    holder.recyclerRowMessagesBinding.ccLayout.getId(),ConstraintSet.RIGHT,0);
            constraintSet.connect(holder.recyclerRowMessagesBinding.message.getId(),ConstraintSet.RIGHT,
                    holder.recyclerRowMessagesBinding.profileCardview.getId(),ConstraintSet.LEFT,0);
            constraintSet.applyTo(constraintLayout);
        }else{
            //UPLOAD PHOTO
            Picasso.get().load(messages.get(position).getSenderImgUri()).into(holder.recyclerRowMessagesBinding.smallProfileImage);
            ConstraintSet constraintSet=new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(holder.recyclerRowMessagesBinding.profileCardview.getId(),ConstraintSet.RIGHT);
            constraintSet.clear(holder.recyclerRowMessagesBinding.message.getId(),ConstraintSet.RIGHT);
            constraintSet.connect(holder.recyclerRowMessagesBinding.profileCardview.getId(),ConstraintSet.LEFT,
                    holder.recyclerRowMessagesBinding.ccLayout.getId(),ConstraintSet.LEFT,0);
            constraintSet.connect(holder.recyclerRowMessagesBinding.message.getId(),ConstraintSet.LEFT,
                    holder.recyclerRowMessagesBinding.profileCardview.getId(),ConstraintSet.RIGHT,0);
            constraintSet.applyTo(constraintLayout);

        }


        //updateRequestList(messages);
    }

    public void updateRequestList(ArrayList<Message> newlist) {// update recyclerView
        messages.clear();
        messages.addAll(newlist);
        this.notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder{

        RecyclerRowMessagesBinding recyclerRowMessagesBinding;

        public MessageHolder(RecyclerRowMessagesBinding recyclerRowMessagesBinding) {
            super(recyclerRowMessagesBinding.getRoot());
            this.recyclerRowMessagesBinding=recyclerRowMessagesBinding;

        }
    }
}
