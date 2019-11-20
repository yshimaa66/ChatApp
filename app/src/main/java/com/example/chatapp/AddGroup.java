package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
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

public class AddGroup extends AppCompatActivity {

    protected EditText groupnameEditText;
    protected Button addbtn;

    private RecyclerView recyclerView;
    private UserGroupAdapter userAdapter;

    protected List<User> users;

    public static List<Chatlist> userList;


    FirebaseUser firebaseUser;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_group);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        userList = new ArrayList<>();


        recyclerView = findViewById(R.id.recyclerviewuserstoaddtogroup);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddGroup.this));


        groupnameEditText = (EditText) findViewById(R.id.groupnameEditText);
        addbtn = (Button) findViewById(R.id.addbtn);


        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String groupnamestr=groupnameEditText.getText().toString();

                Toast.makeText(AddGroup.this, UserGroupAdapter.userstoaddtogroup.size()+"", Toast.LENGTH_SHORT).show();

                long time= System.currentTimeMillis();

                    //UserGroupModel userGroupModel = new UserGroupModel(UserGroupAdapter.userstoaddtogroup.get(i).getId());
                    GroupsModel groupsModel = new GroupsModel(firebaseUser.getUid()+groupnamestr+time,firebaseUser.getUid(),groupnamestr,"default",UserGroupAdapter.userstoaddtogroup);
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

            }
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
