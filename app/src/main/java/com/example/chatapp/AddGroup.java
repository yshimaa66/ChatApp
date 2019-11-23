package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapter.UserGroupAdapter;
import com.example.chatapp.Fragment.Group;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.GroupsModel;
import com.example.chatapp.Model.User;
import com.example.chatapp.Model.UserGroupModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;

public class AddGroup extends AppCompatActivity {

    protected EditText groupnameEditText;
    protected FloatingActionButton addbtn;

    public static int nm=0;

    private RecyclerView recyclerView;
    private UserGroupAdapter userAdapter;

    protected List<User> users;

    public static List<Chatlist> userList;


    public static List<User>userstoaddtogroup;


    FirebaseUser firebaseUser;

    DatabaseReference reference;

    public static int ug=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_group);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        userList = new ArrayList<>();


        userstoaddtogroup=new ArrayList<>();





        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


               userstoaddtogroup.clear();


                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    if(user.getId().equals(firebaseUser.getUid())){


                        userstoaddtogroup.add(user);


                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








        recyclerView = findViewById(R.id.recyclerviewuserstoaddtogroup);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddGroup.this));


        groupnameEditText = (EditText) findViewById(R.id.groupnameEditText);
        addbtn = findViewById(R.id.addbtn);


        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            String groupnamestr=groupnameEditText.getText().toString();

                if(groupnamestr.equals("")){

                    groupnameEditText.setError("Required");


                }

                else if(userstoaddtogroup.size()<=2){

                    Toast.makeText(AddGroup.this, "You need to create a group to select more friends", Toast.LENGTH_SHORT).show();

                }

                else{


                long time= System.currentTimeMillis();

                    //UserGroupModel userGroupModel = new UserGroupModel(UserGroupAdapter.userstoaddtogroup.get(i).getId());
                    GroupsModel groupsModel = new GroupsModel(firebaseUser.getUid()+groupnamestr+time
                            ,firebaseUser.getUid(),groupnamestr,"default",userstoaddtogroup);
                    FirebaseDatabase.getInstance().getReference("Groups")
                            .child(firebaseUser.getUid()+groupnamestr+time)
                            .setValue(groupsModel);




                /*DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

                HashMap<String,Object> hashMap= new HashMap<>();


                hashMap.put("adminid",firebaseUser.getUid());
                hashMap.put("imageURL","default");
                hashMap.put("groupname",groupnamestr);
                hashMap.put("members",UserGroupAdapter.userstoaddtogroup);


                reference.child("Groups").push().setValue(hashMap);*/



                Toast.makeText(AddGroup.this, "Group Added successfully", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(AddGroup.this, Home.class);

                startActivity(intent);

                finish();

            }}
        });


        users = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Chatlist chatlist = snapshot.getValue(Chatlist.class);


                    userList.add(chatlist);


                }

                readusers();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void readusers() {


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        //Toast.makeText(getContext(), userList.size()+"", Toast.LENGTH_SHORT).show();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                users.clear();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);

                    for (Chatlist chatlist : userList) {


                        if (user.getId().equals(chatlist.getId())) {


                            users.add(user);


                        }


                    }


                }


                userAdapter = new UserGroupAdapter(AddGroup.this, users);
                recyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



}
