package com.example.chatapp.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class Profile extends Fragment {



    CircleImageView imageprofile;
    TextView username,friends;

    DatabaseReference reference;

    FirebaseUser firebaseUser;


    StorageReference storageReference;
    private static final int image_request = 1;
    private Uri imageuri;
    private StorageTask uploadtask;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        imageprofile=view.findViewById(R.id.profile_imagefragmentprofile);
        username=view.findViewById(R.id.usernamefragmentprofile);
        friends=view.findViewById(R.id.friends);






        friends.setText("Friends : "+" "+Chats.userList.size()+" ");




        storageReference= FirebaseStorage.getInstance().getReference("uploads");


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);
                assert user != null;

                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {

                    imageprofile.setImageResource(R.drawable.ic_imgprofile);

                } else {

                    Glide.with(getContext()).load(user.getImageURL()).into(imageprofile);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImage();
            }
        });


        return view;
    }

    private void OpenImage() {


        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,image_request);


    }


    private String getFileExtension(Uri uri){


        ContentResolver contentResolver= getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));



    }


    private void uploadImage(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();



        if(imageuri !=null){

            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageuri));


            uploadtask=fileReference.putFile(imageuri);

            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {

                @Override
                public Task <Uri> then(@NonNull Task <UploadTask.TaskSnapshot>task) throws Exception {


                    if(!task.isSuccessful()){

                        throw task.getException();

                    }


                    return fileReference.getDownloadUrl();


                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();

                        String auri= downloadUri.toString();


                        
                        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        

                        HashMap<String,Object> map = new HashMap<>();

                        map.put("imageURL",auri);

                        reference.updateChildren(map);

                        progressDialog.dismiss();


                    }else{

                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();




                }
            });


        }else{


            Toast.makeText(getContext(),"No image selected",Toast.LENGTH_SHORT).show();

        }





    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode==image_request && resultCode==RESULT_OK

                && data != null && data.getData() != null
        ) {

            imageuri = data.getData();

            if(uploadtask!=null &&  uploadtask.isInProgress()){



                Toast.makeText(getContext(),"Upload in progress",Toast.LENGTH_SHORT).show();



            }else{

                uploadImage();

            }





        }
    }
}
