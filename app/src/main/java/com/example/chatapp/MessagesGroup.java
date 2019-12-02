package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.GroupAdapter;
import com.example.chatapp.Adapter.GroupMessagesAdapter;
import com.example.chatapp.Adapter.MessagesAdapter;
import com.example.chatapp.Fragment.APIService;
import com.example.chatapp.Fragment.Group;
import com.example.chatapp.Model.Blocklist;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.GroupChat;
import com.example.chatapp.Model.GroupsModel;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notification.Client;
import com.example.chatapp.Notification.Data;
import com.example.chatapp.Notification.MyResponse;
import com.example.chatapp.Notification.Sender;
import com.example.chatapp.Notification.Token;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesGroup extends AppCompatActivity {



    CircleImageView profileimage;

    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    boolean isFinalized;

    boolean notify = false;

    APIService apiService;

    Button sendbtn,sendphotobtn;
    EditText typemessage;

    StorageReference storageReference;
    private static final int image_request = 1;
    private Uri imageuri;
    private StorageTask<UploadTask.TaskSnapshot> uploadtask;


    GroupMessagesAdapter groupmessageAdapter;
    List<GroupChat> chat;

    RecyclerView recyclerView;


    List<String>usersids;

    String groupid;

    Intent intent;

    TextView leftTV;

    ValueEventListener isseen;

    int i,j;

    Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_group);


        sendbtn=(Button)findViewById(R.id.sendbtn);
        typemessage=(EditText)findViewById(R.id.typemessage);

        leftTV = findViewById(R.id.leftTV);


        leftTV.setVisibility(View.GONE);





        sendphotobtn=findViewById(R.id.sendphotobtn);

        storageReference= FirebaseStorage.getInstance().getReference("uploads");


        sendphotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                OpenImage();

                //typemessage.setText();


            }
        });


        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        profileimage = (CircleImageView) findViewById(R.id.profile_imagemessage);


        recyclerView=findViewById(R.id.recyclerviewmessages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessagesGroup.this,Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });


        username = (TextView) findViewById(R.id.username);

        Intent intent = getIntent();

        groupid = intent.getStringExtra("groupid");

        //Toast.makeText(this, groupid, Toast.LENGTH_LONG).show();




        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


       // userblocklist(userid);

        isFinalized= false;




        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final GroupsModel groupsModel = dataSnapshot.getValue(GroupsModel.class);

                // Toast.makeText(MessagesGroup.this, groupsModel.getGroupid()+"", Toast.LENGTH_SHORT).show();



                assert groupsModel != null;

                username.setText(groupsModel.getGroupname());

                if (groupsModel.getImageURL().equals("default")) {
                    profileimage.setImageResource(R.drawable.ic_imgprofile);
                } else {

                    Glide.with(MessagesGroup.this).load(groupsModel.getImageURL()).into(profileimage);

                }
                sendbtn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {

                        notify=true;

                        String message=typemessage.getText().toString();

                        if(!message.equals("")){

                            sendmessage(firebaseUser.getUid(),groupsModel.getGroupid(),message);

                            typemessage.setText("");




                        }else{
                            Toast.makeText(MessagesGroup.this,"Enter a message",Toast.LENGTH_SHORT).show();


                        }





                    }


                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });







        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                GroupsModel groupsModel = dataSnapshot.getValue(GroupsModel.class);

               // Toast.makeText(MessagesGroup.this, groupsModel.getGroupid()+"", Toast.LENGTH_SHORT).show();



                assert groupsModel != null;

                username.setText(groupsModel.getGroupname());

                if (groupsModel.getImageURL().equals("default")) {
                    profileimage.setImageResource(R.drawable.ic_imgprofile);
                } else {

                    Glide.with(MessagesGroup.this).load(groupsModel.getImageURL()).into(profileimage);

                }


                readmessages(firebaseUser.getUid(), groupsModel.getGroupid(),groupsModel.getImageURL());

                isseenmessageall(groupsModel.getUser().size());




            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


        isseenmessage();



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

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();



        if(imageuri !=null){

            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageuri));


            uploadtask=fileReference.putFile(imageuri);

            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task <Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                    if(!task.isSuccessful()){

                        throw task.getException();

                    }


                    return fileReference.getDownloadUrl();


                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();

                        String auri= downloadUri.toString();



                        sendmessage(firebaseUser.getUid(), groupid, auri);

                        progressDialog.dismiss();


                    }else{

                        Toast.makeText(MessagesGroup.this,"Failed",Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MessagesGroup.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();




                }
            });


        }else{


            Toast.makeText(MessagesGroup.this,"No image selected",Toast.LENGTH_SHORT).show();

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



                Toast.makeText(MessagesGroup.this,"Upload in progress",Toast.LENGTH_SHORT).show();



            }else{

                uploadImage();

            }





        }
    }








    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendmessage(String sender, final String groupid, String message){

        //String currenttime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        final String currentDateandTime = new SimpleDateFormat("EEE  MMM d, yyyy h:mm a").format(new Date());

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();




        HashMap<String,Object> hashMap= new HashMap<>();

        usersids=new ArrayList<>();

        hashMap.put("sender",sender);
        hashMap.put("groupid",groupid);
        hashMap.put("message",message);
        hashMap.put("time",currentDateandTime);

        usersids.add(firebaseUser.getUid());

        hashMap.put("isseen",usersids);

        hashMap.put("isseenall",false);

        reference.child("GroupChats").push().setValue(hashMap);


        usersids.clear();

        /*DatabaseReference referencee=FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashhMap= new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("groupid",groupid);
        hashMap.put("message",message);
        hashMap.put("time",currenttime);

        hashMap.put("isseen",false);

        reference.child("GroupChats").push().setValue(hashMap);*/



        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                GroupsModel groupsModel = dataSnapshot.getValue(GroupsModel.class);

                if(notify) {


String sendername = null;

                    for (int i=0;i<groupsModel.getUser().size();i++) {

                        if(groupsModel.getUser().get(i)!= null) {

                            if(groupsModel.getUser().get(i).getId().equals(firebaseUser.getUid())) {

                                sendername=groupsModel.getUser().get(i).getUsername();



                            }

                        }
                    }





                            for (int i=0;i<groupsModel.getUser().size();i++) {

                                if(groupsModel.getUser().get(i)!= null) {

                                    if(!groupsModel.getUser().get(i).getId().equals(firebaseUser.getUid())) {
                                    sendNotification(groupsModel.getUser().get(i).getId()
                                            , groupsModel.getGroupname(), msg,sendername);



                                }

                                }
                            }





                }

                notify=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }





    private void sendNotification(final String receiver, final String groupname, final String msg, final String sendername) {


        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");

        Query query = tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){


                    Token token = snapshot.getValue(Token.class);



                                      data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher
                                              , groupname +"    " + sendername + " : " + msg, "New Message"
                                              ,receiver,groupid);





                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {


                                    if(response.code()==200){

                                        if(response.body().success!=1){

                                            Toast.makeText(MessagesGroup.this, "Failed !", Toast.LENGTH_SHORT).show();

                                        }


                                    }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });


                }

            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    private void readmessages(final String myid, final String groupid, final String imageurl) {


        chat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("GroupChats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    GroupChat cchat = snapshot.getValue(GroupChat.class);

                    if (cchat.getGroupid().equals(groupid)) {


                        chat.add(cchat);


                    }





                }
                groupmessageAdapter = new GroupMessagesAdapter(MessagesGroup.this, chat,imageurl);
                recyclerView.setAdapter(groupmessageAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }




    private void isseenmessageall(final int h){


        reference = FirebaseDatabase.getInstance().getReference("GroupChats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    GroupChat ccchat = snapshot.getValue(GroupChat.class);

                    if (ccchat.getGroupid().equals(groupid)) {


                        if(ccchat.getIsseen().size()==h) {

                            // holder.seen.setVisibility(View.VISIBLE);
                            // holder.unseen.setVisibility(View.GONE);

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("isseenall", true);

                            snapshot.getRef().updateChildren(hashMap);


                        }



                    }





                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }





    private void isseenmessage(){




        reference = FirebaseDatabase.getInstance().getReference("GroupChats");


        isseen=  reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    GroupChat cchat = snapshot.getValue(GroupChat.class);

                    int j=0;

                    if (cchat.getGroupid().equals(groupid)) {

                        for(int i=0;i<cchat.getIsseen().size();i++){

                            if(!cchat.getIsseen().get(i).equals(firebaseUser.getUid())){

                                j=1;

                            }else{
                                j=0;
                                break;
                            }

                        }

                        if(j==1){

                            List<String>usersids = cchat.getIsseen();

                            usersids.add(firebaseUser.getUid());

                            HashMap<String,Object> hashMap= new HashMap<>();

                            hashMap.put("isseen",usersids);

                            snapshot.getRef().updateChildren(hashMap);

                        }


                    }





                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });








    }




















    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);

        //getMenuInflater().inflate(R.menu.message_menu, menu);


        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement



        if(id==R.id.groupinfo){


            Intent intent = new Intent(this, GroupInfo.class);
            intent.putExtra("groupid",groupid);
            startActivity(intent);





        }












        if (id == R.id.leave) {




            final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        GroupsModel group = snapshot.getValue(GroupsModel.class);


                        assert group != null;

                        if(group.getGroupid().equals(groupid)) {

                            for (int i = 0; i < group.getUser().size(); i++) {

                                if (group.getUser().get(i).getId().equals(firebaseUser.getUid())) {





                                    FirebaseDatabase.getInstance().getReference("Groups")
                                            .child(groupid).child("user").child(i+"").removeValue();



                                    Intent intent = new Intent(MessagesGroup.this,Home.class);
                                    startActivity(intent);



                                   //group.getUser().get(i).getUsername();

                                }


                            }

                        }


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });








            Toast.makeText(this, "You leaved the group", Toast.LENGTH_SHORT).show();

        }









        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onPause() {

        super.onPause();


        reference.removeEventListener(isseen);

    }



}
