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
import com.example.chatapp.Adapter.MessagesAdapter;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.Blocklist;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Chatlist;
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
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messages extends AppCompatActivity {



    CircleImageView profileimage;

    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    boolean isFinalized;


    ValueEventListener isseen;

    Button sendbtn;
    EditText typemessage;


    MessagesAdapter messageAdapter;
    List<Chat> chat;

    RecyclerView recyclerView;


    String userid;

    Intent intent;

    TextView blockTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        sendbtn=(Button)findViewById(R.id.sendbtn);
        typemessage=(EditText)findViewById(R.id.typemessage);

        blockTV = findViewById(R.id.blockTV);


        blockTV.setVisibility(View.GONE);





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

        userblocklist(userid);



        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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





    private void sendmessage(String sender, String receiver, String message){


        String currenttime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap= new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("time",currenttime);

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

                        isFinalized= true;

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
            blockTV.setVisibility(View.VISIBLE);

        }


        if(id==R.id.unblock){


            Blocklist blocklist = new Blocklist(userid);
            FirebaseDatabase.getInstance().getReference("Blocklist")
                    .child(firebaseUser.getUid())
                    .child(blocklist.getId()).removeValue();

            typemessage.setVisibility(View.VISIBLE);
            sendbtn.setVisibility(View.VISIBLE);
            blockTV.setVisibility(View.GONE);

            isFinalized=false;



        }



        return super.onOptionsItemSelected(item);
    }








    @Override
    protected void onPause() {

        super.onPause();


        reference.removeEventListener(isseen);

    }

}
