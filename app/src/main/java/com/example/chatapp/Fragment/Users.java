package com.example.chatapp.Fragment;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
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
import android.widget.SearchView;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Home;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Users extends Fragment implements SearchView.OnQueryTextListener {

    public RecyclerView recyclerView;
    public UserAdapter userAdapter;


    protected Toolbar toolbarr;

    public List<User> users;


    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);



        recyclerView=view.findViewById(R.id.recyclerviewusers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        users = new ArrayList<>();

        readusers();




        setHasOptionsMenu(true);

        return view;
    }




    @SuppressLint("ResourceType")
    @Override
    public void onCreateOptionsMenu(Menu menuu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.



        inflater.inflate(R.menu.search, menuu);


        SearchManager searchManager =
                (SearchManager) ((AppCompatActivity)getActivity()).getSystemService(((AppCompatActivity)getActivity()).SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(((AppCompatActivity)getActivity()).getComponentName()));
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menuu,inflater);
    }









    private void readusers(){


        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();



                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    assert user !=null;
                    assert firebaseUser !=null;

                    if(!user.getId().equals(firebaseUser.getUid())){


                        users.add(user);


                    }

                }


                userAdapter = new UserAdapter(getContext(),users);
                recyclerView.setAdapter(userAdapter);


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
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        final String finalNewText = newText;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                users.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    assert user !=null;
                    assert firebaseUser !=null;

                    if(!user.getId().equals(firebaseUser.getUid()) && user.getUsername().toLowerCase().contains(finalNewText)){


                        users.add(user);


                    }

                }


                userAdapter = new UserAdapter(getContext(),users);
                recyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return true;
    }


}






