package com.siyararslan.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.siyararslan.socialmediaapp.databinding.ActivityMainBinding;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    private boolean isSignedUp=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth=FirebaseAuth.getInstance();

        FirebaseUser user=auth.getCurrentUser();



        if(user!=null){
            Intent intent=new Intent(MainActivity.this,FeedActivity.class);
            startActivity(intent);
            finish();
        }





    }


    public void signInClicked(View view){
        String email=binding.emailMainText.getText().toString();
        String password=binding.passwordText.getText().toString();

        if(email.equals("")&&password.equals("")){
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
        }
        else if(email.equals("")){
            Toast.makeText(this, "Enter email ", Toast.LENGTH_SHORT).show();
        }
        else if(password.equals("")){
            Toast.makeText(this, "Enter password ", Toast.LENGTH_SHORT).show();
        }
        else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }

    }

    public void signUpClicked(View view){
        String email=binding.emailMainText.getText().toString();
        String password=binding.passwordText.getText().toString();

        if(email.equals("")&&password.equals("")){
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
        }
        else if(email.equals("")){
            Toast.makeText(this, "Enter email ", Toast.LENGTH_SHORT).show();
        }
        else if(password.equals("")){
            Toast.makeText(this, "Enter password ", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Entery trying", Toast.LENGTH_SHORT).show();



            //bu bir asenkron işlemdir(bu istek islenirken arka planda calışır)
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {//başarılı olursa
                @Override
                public void onSuccess(AuthResult authResult) {







                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();//activiteyi kapatma bir kere giris yaptıktan sonra bir daha giris ekranına ihtiyacımız yok


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }

    }
}