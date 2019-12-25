package com.example.chatapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapter.AddToGroupAdapter;
import com.example.chatapp.Adapter.UserGroupAdapter;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.GroupsModel;
import com.example.chatapp.Model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddToGroup extends AppCompatActivity {

    protected RecyclerView recyclerview;
    protected FloatingActionButton addbtn;
    protected List<User> users;

    private AddToGroupAdapter addToGroupAdapter;

    public static List<Chatlist> userList;


    public static List<User>userstoaddtogroup;


    FirebaseUser firebaseUser;


    public static List<User> userstoadd;

    public static List<User> usersingroup;

    public static List<User> usersingroupp;

    String groupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_to_group);


        recyclerview = (RecyclerView) findViewById(R.id.recyclerviewaddtogroup);

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(AddToGroup.this));


        addbtn = (FloatingActionButton) findViewById(R.id.addbtn);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();

        groupid = intent.getStringExtra("groupid");


        usersingroup=new ArrayList<>();

        userstoadd=new ArrayList<>();

        userList=new ArrayList<>();

        users= new ArrayList<>();

        usersingroupp=new ArrayList<>();




        DatabaseReference referencee= FirebaseDatabase.getInstance().getReference("Groups").child(groupid);

        referencee.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                final GroupsModel group = dataSnapshot.getValue(GroupsModel.class);


                usersingroup=group.getUser();





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });





        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());

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



        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referenceu= FirebaseDatabase.getInstance().getReference("Users");

        referenceu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                usersingroupp.clear();

                usersingroupp.addAll(usersingroup);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //usersingroup.clear();


                final DatabaseReference referenceee= FirebaseDatabase.getInstance().getReference("Groups").child(groupid);

                referenceee.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        final GroupsModel group = dataSnapshot.getValue(GroupsModel.class);


                        assert group != null;



                       // Toast.makeText(AddToGroup.this, usersingroupp.size()+"", Toast.LENGTH_SHORT).show();

                        group.setUser(usersingroupp);

                        FirebaseDatabase.getInstance().getReference("Groups")
                                .child(groupid)
                                .setValue(group);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });


                Toast.makeText(AddToGroup.this, "Group updated successfully", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(AddToGroup.this, Home.class);

                startActivity(intent);

                finish();


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



                for(int i=0;i<usersingroup.size();i++){

                    for(int j=0;j<users.size();j++) {

                    if(users.get(j).getId().equals(usersingroup.get(i).getId())){

                        users.remove(j);

                    }

                    }

                }

                if(users.size()==0){

                          AlertDialog alertDialog = new AlertDialog.Builder(AddToGroup.this)
//set icon
                            .setIcon(android.R.drawable.ic_dialog_alert)
//set title
                            .setTitle("Oops!")
//set message
                            .setMessage("All your friends are already in the group")
//set positive button
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked

                                    Intent intent = new Intent(AddToGroup.this, Home.class);

                                    startActivity(intent);

                                    finish();

                                    finish();

                                }
                            })
//set negative button
                            /*.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what should happen when negative button is clicked
                                    Toast.makeText(getApplicationContext(),"Nothing Happened",Toast.LENGTH_LONG).show();
                                }
                            })*/
                            .show();

                }

                addToGroupAdapter = new AddToGroupAdapter(AddToGroup.this, users);
                recyclerview.setAdapter(addToGroupAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }






}



