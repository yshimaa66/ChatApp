package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.GroupAdapter;
import com.example.chatapp.Adapter.GroupMessagesAdapter;
import com.example.chatapp.Adapter.MessagesAdapter;
import com.example.chatapp.Fragment.Group;
import com.example.chatapp.Model.Blocklist;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.GroupChat;
import com.example.chatapp.Model.GroupsModel;
import com.example.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesGroup extends AppCompatActivity {



    CircleImageView profileimage;

    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    boolean isFinalized;



    Button sendbtn;
    EditText typemessage;


    GroupMessagesAdapter groupmessageAdapter;
    List<GroupChat> chat;

    RecyclerView recyclerView;


    String groupid;

    Intent intent;

    TextView leftTV;

    ValueEventListener isseen;

    int i,j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_group);


        sendbtn=(Button)findViewById(R.id.sendbtn);
        typemessage=(EditText)findViewById(R.id.typemessage);

        leftTV = findViewById(R.id.leftTV);


        leftTV.setVisibility(View.GONE);




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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });




    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendmessage(String sender, final String groupid, String message){

        //String currenttime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        final String currentDateandTime = new SimpleDateFormat("EEE  MMM d, yyyy h:mm a").format(new Date());

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        final HashMap<String,Object> hashMap= new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("groupid",groupid);
        hashMap.put("message",message);
        hashMap.put("time",currentDateandTime);







/*


        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referencee= FirebaseDatabase.getInstance().getReference("Groups");

        referencee.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    GroupsModel group = snapshot.getValue(GroupsModel.class);

                    if(group.getGroupid().equals(groupid)){

                    for(int i=0;i<group.getUser().size();i++){

                         hashMap.put(group.getUser().get(i).getId(),"false");



                        }




                    }




                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


*/








        reference.child("GroupChats").push().setValue(hashMap);


        /*DatabaseReference referencee=FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashhMap= new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("groupid",groupid);
        hashMap.put("message",message);
        hashMap.put("time",currenttime);

        hashMap.put("isseen",false);

        reference.child("GroupChats").push().setValue(hashMap);*/




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





}
