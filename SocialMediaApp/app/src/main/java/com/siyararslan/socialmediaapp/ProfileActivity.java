package com.siyararslan.socialmediaapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.siyararslan.socialmediaapp.databinding.ActivityMainBinding;
import com.siyararslan.socialmediaapp.databinding.ActivityProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData;


    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        
        register();

        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();

        binding.userEmailProfile.setText(auth.getCurrentUser().getEmail());
        getData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain=new Intent(ProfileActivity.this,FeedActivity.class);
        startActivity(intentToMain);
        finish();
    }

    public void getData(){

        //firebaseFirestore.collection("Posts").whereEqualTo("useremail","james@gmail.com").addSna...//useremail'i james@gmail.com olanları getir
        firebaseFirestore.collection("ProfileImages").whereEqualTo("email",auth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(ProfileActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if(value!=null){

                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String,Object> data=snapshot.getData();


                        String downloadUri=(String) data.get("downloaduri");
                        Picasso.get().load(downloadUri).into(binding.selectProfileImg);

                        FirebaseFirestore.getInstance().collection("Users").add(new User(auth.getCurrentUser().getEmail(),downloadUri)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
//                                Intent intent=new Intent(ProfileActivity.this,FeedActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });




                    }


                }

            }
        });



    }

    public void UploadImageClicked(View view){
        UUID uuid=UUID.randomUUID();
        String imageName="profileimages/"+uuid+".jpg";
        if (imageData!=null){
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference newReference=firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUri=uri.toString();
                            String email=auth.getCurrentUser().getEmail();
                            HashMap<String,Object> map=new HashMap<>();
                            map.put("email",email);
                            map.put("downloaduri",downloadUri);
                            map.put("date", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("ProfileImages").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(ProfileActivity.this,"done",Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    });

                }
            });

        }

    }

    public void selectedProfileImg(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for go to gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ask permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                //ask permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }else{
            //go to gallery
            Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        }

    }

    private void register() {
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentFromData=result.getData();
                    if (intentFromData!=null){
                        imageData=intentFromData.getData();
                        binding.selectProfileImg.setImageURI(imageData);
                    }
                }
            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                }else{
                    Toast.makeText(ProfileActivity.this, "Permission needed", Toast.LENGTH_LONG).show();
                }
            }
        });


    }




}