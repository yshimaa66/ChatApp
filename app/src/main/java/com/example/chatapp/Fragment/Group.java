package com.example.chatapp.Fragment;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.chatapp.Adapter.GroupAdapter;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Adapter.UserGroupAdapter;
import com.example.chatapp.AddGroup;
import com.example.chatapp.Messages;
import com.example.chatapp.Model.Chatlist;
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


public class Group extends Fragment implements SearchView.OnQueryTextListener{

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


                //UserGroupAdapter.userstoaddtogroup=new ArrayList<>();


                Intent intent = new Intent(getContext(), AddGroup.class);
                getContext().startActivity(intent);



            }
        });








          recyclerView=view.findViewById(R.id.recyclerviewgroups);
          recyclerView.setHasFixedSize(true);
          recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groups = new ArrayList<>();

        readgroups();





        setHasOptionsMenu(true);

        return view;
    }



    @SuppressLint("ResourceType")
    @Override
    public void onCreateOptionsMenu(Menu menuuu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.



        inflater.inflate(R.menu.search, menuuu);


        SearchManager searchManagerrr =
                (SearchManager) ((AppCompatActivity)getActivity()).getSystemService(((AppCompatActivity)getActivity()).SEARCH_SERVICE);
        SearchView searchViewww = (SearchView) menuuu.findItem(R.id.action_search)
                .getActionView();
        searchViewww.setSearchableInfo(searchManagerrr
                .getSearchableInfo(((AppCompatActivity)getActivity()).getComponentName()));
        searchViewww.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menuuu,inflater);

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


                        for(int i=0;i<group.getUser().size();i++){

                            if(group.getUser().get(i)!=null){

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {


        newText= newText.toLowerCase();


        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");

        //Toast.makeText(getContext(), 1+"", Toast.LENGTH_SHORT).show();

        final String finalNewText = newText;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                groups.clear();


                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    GroupsModel group = snapshot.getValue(GroupsModel.class);


                    assert group != null;

                    if(group.getGroupname().toLowerCase().contains(finalNewText)){

                            for(int i=0;i<group.getUser().size();i++) {

                                if (group.getUser().get(i) != null) {

                                    if (group.getUser().get(i).getId().equals(firebaseUser.getUid())) {

                                        groups.add(group);

                                    }


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



        return true;
    }


}
