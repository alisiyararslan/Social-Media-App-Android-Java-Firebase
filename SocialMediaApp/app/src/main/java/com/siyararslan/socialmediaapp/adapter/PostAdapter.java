package com.siyararslan.socialmediaapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siyararslan.socialmediaapp.Post;
import com.siyararslan.socialmediaapp.databinding.RecyclerRowPostBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{
    ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowPostBinding recyclerRowPostBinding=RecyclerRowPostBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recyclerRowPostBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.recyclerRowPostBinding.postEmail.setText(postArrayList.get(position).getEmail());
        holder.recyclerRowPostBinding.recyclerCommentText.setText(postArrayList.get(position).getComment());
        //görselin url'sini download etme
        Picasso.get().load(postArrayList.get(position).getPostDownloadUrl()).into(holder.recyclerRowPostBinding.recyclerViewImageView);
        Picasso.get().load(postArrayList.get(position).getProfileImgDownloadUrl()).into(holder.recyclerRowPostBinding.postSmallProfileImage);

    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerRowPostBinding recyclerRowPostBinding;

        public PostHolder(RecyclerRowPostBinding recyclerRowPostBinding) {
            super(recyclerRowPostBinding.getRoot());
            this.recyclerRowPostBinding=recyclerRowPostBinding;
        }
    }
}
