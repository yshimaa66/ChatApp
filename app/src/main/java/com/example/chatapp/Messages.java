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
import com.example.chatapp.Adapter.MessagesAdapter;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Fragment.APIService;
import com.example.chatapp.Model.Blocklist;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Chatlist;
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
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Messages extends AppCompatActivity {



    APIService apiService;

    CircleImageView profileimage;

    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    boolean isFinalized,isFinalizedd;


    ValueEventListener isseen;

    Button sendbtn,sendphotobtn;
    EditText typemessage;


    MessagesAdapter messageAdapter;
    List<Chat> chat;

    RecyclerView recyclerView;

    StorageReference storageReference;
    private static final int image_request = 1;
    private Uri imageuri;
    private StorageTask<UploadTask.TaskSnapshot> uploadtask;

    String userid;

    Intent intent;

    boolean notify = false;

    TextView blockTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        sendbtn=(Button)findViewById(R.id.sendbtn);
        typemessage=(EditText)findViewById(R.id.typemessage);

        blockTV = findViewById(R.id.blockTV);


        blockTV.setVisibility(View.GONE);


        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        sendphotobtn=findViewById(R.id.sendphotobtn);

        storageReference= FirebaseStorage.getInstance().getReference("uploads");


        sendphotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                OpenImage();

                //typemessage.setText();


            }
        });




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
                startActivity(new Intent(Messages.this,Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });


        username = (TextView) findViewById(R.id.username);

        Intent intent = getIntent();

        userid = intent.getStringExtra("userid");


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        isFinalized= false;

        isFinalizedd=true;

        userblocklist(userid);



        sendbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                notify=true;

                String message=typemessage.getText().toString();

                if(!message.equals("")){

                    sendmessage(firebaseUser.getUid(),userid,message);

                    typemessage.setText("");




                }else{
                    Toast.makeText(Messages.this,"Enter a message",Toast.LENGTH_SHORT).show();


                }





            }


        });







        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    profileimage.setImageResource(R.drawable.ic_imgprofile);
                } else {

                    Glide.with(Messages.this).load(user.getImageURL()).into(profileimage);

                }
                readmessages(firebaseUser.getUid(), userid,user.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        isseenmessage(userid);


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
                public Task<Uri> then(@NonNull Task <UploadTask.TaskSnapshot>task) throws Exception {


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



                        sendmessage(firebaseUser.getUid(), userid, auri);

                        progressDialog.dismiss();


                    }else{

                        Toast.makeText(Messages.this,"Failed",Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(Messages.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();




                }
            });


        }else{


            Toast.makeText(Messages.this,"No image selected",Toast.LENGTH_SHORT).show();

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



                Toast.makeText(Messages.this,"Upload in progress",Toast.LENGTH_SHORT).show();



            }else{

                uploadImage();

            }





        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendmessage(String sender, final String receiver, String message){


        //String currenttime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        final String currentDateandTime = new SimpleDateFormat("EEE  MMM d, yyyy h:mm a").format(new Date());

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap= new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("time",currentDateandTime);

        hashMap.put("isseen",false);


        reference.child("Chats").push().setValue(hashMap);



        Chatlist chatlist = new Chatlist(receiver);
        FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(sender)
                .child(chatlist.getId()).setValue(chatlist);

        Chatlist chatlistt = new Chatlist(sender);
        FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(chatlistt.getId()).setValue(chatlistt);





        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if(notify) {

                    sendNotification(receiver, user.getUsername(), msg);

                }

                notify=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendNotification(String receiver, final String username, final String msg) {


        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");

        Query query = tokens.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){


                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher
                            ,username+ " : "+msg,"New Message",userid);


                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {


                                    if(response.code()==200){

                                        if(response.body().success!=1){

                                            Toast.makeText(Messages.this, "Failed !", Toast.LENGTH_SHORT).show();

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


    private void readmessages(final String myid, final String userid, final String imageurl) {


        chat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat cchat = snapshot.getValue(Chat.class);

                    if (cchat.getReceiver().equals(myid) && cchat.getSender().equals(userid)) {


                        chat.add(cchat);


                    } else if (cchat.getReceiver().equals(userid) && cchat.getSender().equals(myid)) {


                        chat.add(cchat);


                    }





                }
                messageAdapter = new MessagesAdapter(Messages.this, chat,imageurl);
                recyclerView.setAdapter(messageAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }



    private void isseenmessage(final String userid){




        reference = FirebaseDatabase.getInstance().getReference("Chats");


        isseen=  reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat cchat = snapshot.getValue(Chat.class);

                    if (cchat.getReceiver().equals(firebaseUser.getUid()) && cchat.getSender().equals(userid)) {


                        HashMap<String,Object> hashMap= new HashMap<>();

                        hashMap.put("isseen",true);

                        snapshot.getRef().updateChildren(hashMap);

                    }





                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });








    }


















    private void userblocklist(final String useridtocheck){


        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Blocklist").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Blocklist blockeduser = snapshot.getValue(Blocklist.class);


                    assert blockeduser != null;

                    if(blockeduser.getId().equals(useridtocheck)){

                        typemessage.setVisibility(View.GONE);
                        sendbtn.setVisibility(View.GONE);
                        blockTV.setVisibility(View.VISIBLE);

                        sendphotobtn.setVisibility(View.GONE);

                        isFinalized= true;

                        isFinalizedd= false;

                    }

                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        final FirebaseUser firebaseUserr= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referencee= FirebaseDatabase.getInstance().getReference("Blocklist").child(useridtocheck);

        referencee.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Blocklist blockeduser = snapshot.getValue(Blocklist.class);


                    assert blockeduser != null;

                    if(blockeduser.getId().equals(firebaseUserr.getUid())){

                        typemessage.setVisibility(View.GONE);
                        sendbtn.setVisibility(View.GONE);
                        blockTV.setVisibility(View.VISIBLE);
                        sendphotobtn.setVisibility(View.GONE);



                        isFinalized = false;

                        isFinalizedd = false;



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
        inflater.inflate(R.menu.message_menu, menu);

        //getMenuInflater().inflate(R.menu.message_menu, menu);


        return true;
    }



    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

        menu.findItem(R.id.unblock).setEnabled(isFinalized);

        menu.findItem(R.id.block).setEnabled(isFinalizedd);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.block) {




            Blocklist blocklist = new Blocklist(userid);
            FirebaseDatabase.getInstance().getReference("Blocklist")
                    .child(firebaseUser.getUid())
                    .child(blocklist.getId()).setValue(blocklist);


            typemessage.setVisibility(View.GONE);
            sendbtn.setVisibility(View.GONE);
            sendphotobtn.setVisibility(View.GONE);
            blockTV.setVisibility(View.VISIBLE);

            isFinalizedd=false;

        }


        if(id==R.id.unblock){


            Blocklist blocklist = new Blocklist(userid);
            FirebaseDatabase.getInstance().getReference("Blocklist")
                    .child(firebaseUser.getUid())
                    .child(blocklist.getId()).removeValue();

            typemessage.setVisibility(View.VISIBLE);
            sendbtn.setVisibility(View.VISIBLE);
            sendphotobtn.setVisibility(View.VISIBLE);
            blockTV.setVisibility(View.GONE);

            isFinalized=false;

            isFinalizedd=true;


        }



        return super.onOptionsItemSelected(item);
    }








    @Override
    protected void onPause() {

        super.onPause();


        reference.removeEventListener(isseen);

    }

}
