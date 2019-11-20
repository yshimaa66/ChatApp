package com.example.chatapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.chatapp.Adapter.GroupAdapter;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Adapter.UserGroupAdapter;
import com.example.chatapp.AddGroup;
import com.example.chatapp.Messages;
import com.example.chatapp.Model.GroupsModel;
import com.example.chatapp.Model.User;
import com.example.chatapp.Model.UserGroupModel;
import com.example.chatapp.R;
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


public class Group extends Fragment {

    public RecyclerView recyclerView;

    public GroupAdapter groupAdapter;

    public List<GroupsModel> groups;



    FloatingActionButton addGroupbtn;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);


        addGroupbtn = view.findViewById(R.id.add_group);




        addGroupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getContext(), AddGroup.class);
                getContext().startActivity(intent);



            }
        });








          recyclerView=view.findViewById(R.id.recyclerviewgroups);
          recyclerView.setHasFixedSize(true);
          recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groups = new ArrayList<>();

        readgroups();





        return view;
    }





    private void readgroups(){


        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groups.clear();



                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    GroupsModel group = snapshot.getValue(GroupsModel.class);

                    if(group.getAdminid().equals(firebaseUser.getUid())){

                    groups.add(group);

                    }else{

                        for(int i=0;i<group.getUser().size();i++){

                            if(group.getUser().get(i).getId().equals(firebaseUser.getUid())){

                                groups.add(group);

                            }


                        }


                    }

                }


                groupAdapter = new GroupAdapter(getContext(),groups);
                recyclerView.setAdapter(groupAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
