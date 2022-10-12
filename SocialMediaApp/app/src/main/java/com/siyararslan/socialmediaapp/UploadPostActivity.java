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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.siyararslan.socialmediaapp.adapter.PostAdapter;
import com.siyararslan.socialmediaapp.databinding.ActivityUploadPostBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadPostActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private ActivityUploadPostBinding binding;
    Uri imageData;
    //Bitmap selectedImage;


    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadPostBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        register();


        firebaseStorage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();

    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain=new Intent(UploadPostActivity.this,FeedActivity.class);
        startActivity(intentToMain);
        finish();
    }

    public void uploadButtonClicked(View view){

        //universal unique id
        //her seferinde daha önce oluşturulmamış bir isim olusturur

        UUID uuid=UUID.randomUUID();
        String imageName="images/"+uuid+".jpg";
        if (imageData!=null){//kullanıcı seçtiyse
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Download url
                    //bu islemleri yapmak icin önce verinin kaydedilmesi gerekir

                    StorageReference newReference=firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl=uri.toString();
                            String comment=binding.CommentText.getText().toString();
                            FirebaseUser user=auth.getCurrentUser();
                            String email=user.getEmail();

                            getProfImgAndSave(email,downloadUrl,comment);



                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadPostActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    public void getProfImgAndSave(String email,String postDownloadUrl,String comment){
        FirebaseFirestore.getInstance().collection("ProfileImages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(UploadPostActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                if (value != null) {

                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        String ppdownloadUri = (String) data.get("downloaduri");

                        String emailFromDatabase = (String) data.get("email");
                        if (emailFromDatabase.equals(email)) {

                            //allFriends.add(new User(userEmail,downloadUri));

                            HashMap<String,Object> postData=new HashMap<>();
                            postData.put("useremail",email);
                            postData.put("postdownloadurl",postDownloadUrl);
                            postData.put("comment",comment);
                            postData.put("profileimgdownloadurl",ppdownloadUri);
                            postData.put("date", FieldValue.serverTimestamp());//güncel tarih

                            firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Intent intent=new Intent(UploadPostActivity.this,FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//önceki activityleri kapatır finishe gerek kalmaz..
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadPostActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                                }
                            });
                        }




                    }
                    //PostAdapter.notifyDataSetChanged();

                }

            }
        });
    }

    public void selectImage(View view){


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ask permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }
            else{
                //ask permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else{
            //go to gallery
            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }

    }

    private void register(){


        activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentFromData=result.getData();
                    if (intentFromData!=null){
                        imageData=intentFromData.getData();
                        binding.imageView.setImageURI(imageData);//firebase için yeterli

                    }

                }
            }
        });


        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if (result){
                    Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);

                }else{
                    Toast.makeText(UploadPostActivity.this, "Permission needed", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}