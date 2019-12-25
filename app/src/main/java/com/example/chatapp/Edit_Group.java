package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.Adapter.GroupEditAdapter;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.GroupsModel;
import com.example.chatapp.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_Group extends AppCompatActivity {

    CircleImageView imageprofile;
    EditText username,friends;

    DatabaseReference reference;

    FirebaseUser firebaseUser;

    String groupid;

    int i;

    public RecyclerView recyclerView;
    public GroupEditAdapter groupEditAdapter;

    StorageReference storageReference;
    private static final int image_request = 1;
    private Uri imageuri;
    private StorageTask uploadtask;


    public List<User> users;

    public static List<User> userstoremove;

    public List<User> usersingroup;

    List<String>usersingroupid;

    protected FloatingActionButton addbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_edit__group);

        imageprofile=findViewById(R.id.profile_imagefragmentprofile);
        username=findViewById(R.id.usernamefragmentprofile);


        addbtn = findViewById(R.id.addbtn);
        recyclerView=findViewById(R.id.recyclerviewusers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        users = new ArrayList<>();

        usersingroupid=new ArrayList<>();


        usersingroup=new ArrayList<>();
        userstoremove=new ArrayList<>();

        Intent intent = getIntent();

        groupid = intent.getStringExtra("groupid");






        storageReference= FirebaseStorage.getInstance().getReference("uploads");


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Groups").child(groupid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                final GroupsModel group = dataSnapshot.getValue(GroupsModel.class);





                username.setText(group.getGroupname());





                if (group.getImageURL().equals("default")) {

                    imageprofile.setImageResource(R.drawable.ic_imgprofile);

                } else {

                    Glide.with(Edit_Group.this).load(group.getImageURL()).into(imageprofile);

                }



                for(i=0;i<group.getUser().size();i++){

                    if(group.getUser().get(i)!=null){

                        usersingroupid.add(group.getUser().get(i).getId());

                    }

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });














        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                users.clear();



                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);

                    assert user != null;

                    for (int i = 0; i < usersingroupid.size(); i++) {


                        if (user.getId().equals(usersingroupid.get(i))) {

                            if (!user.getId().equals(firebaseUser.getUid())) {

                                users.add(user);

                            }

                        }

                    }
                }


                groupEditAdapter = new GroupEditAdapter(getApplicationContext(),users);
                recyclerView.setAdapter(groupEditAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






   addbtn.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {


           final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups").child(groupid);

           reference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                   final GroupsModel group = dataSnapshot.getValue(GroupsModel.class);



                   group.setGroupname( username.getText().toString());


                   usersingroup=group.getUser();



                   for(int i=0;i<userstoremove.size();i++){

                       User user = userstoremove.get(i);

                       for(int j=0;j<usersingroup.size();j++) {

                           if(usersingroup.get(j).getId().equals(user.getId())){

                             usersingroup.remove(j);

                           }
                       }
                   }

                   //Toast.makeText(Edit_Group.this, usersingroup.size()+"", Toast.LENGTH_SHORT).show();

                   group.setUser(usersingroup);

                   FirebaseDatabase.getInstance().getReference("Groups")
                           .child(groupid)
                           .setValue(group);



               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }


           });


           Toast.makeText(Edit_Group.this, "Group updated successfully", Toast.LENGTH_SHORT).show();


           Intent intent = new Intent(Edit_Group.this, Home.class);

           startActivity(intent);

           finish();


       }
   });





        imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImage();
            }
        });


    }

    private void OpenImage() {


        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,image_request);


    }


    private String getFileExtension(Uri uri){


        ContentResolver contentResolver= this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));



    }


    private void uploadImage(){

        final ProgressDialog progressDialog = new ProgressDialog(Edit_Group.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();



        if(imageuri !=null){

            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageuri));


            uploadtask=fileReference.putFile(imageuri);

            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

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



                        reference=FirebaseDatabase.getInstance().getReference("Groups").child(groupid);



                        HashMap<String,Object> map = new HashMap<>();

                        map.put("imageURL",auri);

                        reference.updateChildren(map);

                        progressDialog.dismiss();


                    }else{

                        Toast.makeText(Edit_Group.this,"Failed",Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(Edit_Group.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();




                }
            });


        }else{


            Toast.makeText(Edit_Group.this,"No image selected",Toast.LENGTH_SHORT).show();

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



                Toast.makeText(Edit_Group.this,"Upload in progress",Toast.LENGTH_SHORT).show();



            }else{

                uploadImage();

            }





        }
    }
}
