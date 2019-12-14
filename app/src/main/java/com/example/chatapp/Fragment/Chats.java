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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Home;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notification.Token;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class Chats extends Fragment implements SearchView.OnQueryTextListener{



    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    protected List<User> users,userss,usersss;

    public static List<Chatlist> userList;

    protected Toolbar toolbarrr;


    FirebaseUser firebaseUser;

    DatabaseReference reference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chats, container, false);





        setHasOptionsMenu(true);




        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();



        userList=new ArrayList<>();





        recyclerView=view.findViewById(R.id.recyclerviewchats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        users = new ArrayList<>();

        userss = new ArrayList<>();


        usersss = new ArrayList<>();



        reference=FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                userList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){


                    Chatlist chatlist=snapshot.getValue(Chatlist.class);



                    userList.add(chatlist);


                }

                readusers();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        UpdateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }




    private void UpdateToken(String token){


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }






    private void readusers(){


        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        //Toast.makeText(getContext(), userList.size()+"", Toast.LENGTH_SHORT).show();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                users.clear();


                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    for(Chatlist chatlist : userList){


                        if(user.getId().equals(chatlist.getId())){



                            users.add(user);



                        }


                    }



                }

/*
                userss.clear();

                for(int i =0;i<users.size();i++){




                        DatabaseReference referencee = FirebaseDatabase.getInstance().getReference("Chats");


                        final int finalI = i;
                        referencee.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    Chat cchat = snapshot.getValue(Chat.class);

                                    if (cchat.getReceiver().equals(firebaseUser.getUid()) && cchat.getSender()
                                            .equals(users.get(finalI).getId())) {


                                        userss.add(users.get(finalI));

                                        break;


                                    } else if (cchat.getSender().equals(firebaseUser.getUid()) && cchat.getReceiver()
                                            .equals(users.get(finalI).getId())) {

                                        userss.add(users.get(finalI));

                                       break;

                                    }


                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }


                        });


                    }


*/













                chatAdapter = new ChatAdapter(getContext(),users);
                recyclerView.setAdapter(chatAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








        DatabaseReference referencee = FirebaseDatabase.getInstance().getReference("Chats");
        referencee.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userss.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat cchat = snapshot.getValue(Chat.class);
                    int k=0;

                    assert cchat != null;
                    assert firebaseUser != null;
                    if (cchat.getReceiver().equals(firebaseUser.getUid()) || cchat.getSender()
                            .equals(firebaseUser.getUid())) {

                        for(int i=0;i<users.size();i++){

                            if(cchat.getReceiver().equals(users.get(i).getId()) || cchat.getSender()
                                    .equals(users.get(i).getId())){

                        userss.add(0,users.get(i));

                     //   Toast.makeText(getContext(), users.get(i).getUsername()+"145", Toast.LENGTH_SHORT).show();



                        k=1;
                        break;

                    } } }


                }






                usersss.clear();

                for(int i=0;i<userss.size();i++){

                    if(!usersss.contains(userss.get(i))){

                        usersss.add(userss.get(i));

                    }


                }



                chatAdapter = new ChatAdapter(getContext(),usersss);
                recyclerView.setAdapter(chatAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });






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



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {


        newText= newText.toLowerCase();


        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        //Toast.makeText(getContext(), userList.size()+"", Toast.LENGTH_SHORT).show();

        final String finalNewText = newText;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                users.clear();


                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    for(Chatlist chatlist : userList){


                        if(user.getId().equals(chatlist.getId())&& user.getUsername().toLowerCase().contains(finalNewText)){


                            users.add(user);



                        }


                    }



                }


                chatAdapter = new ChatAdapter(getContext(),users);
                recyclerView.setAdapter(chatAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return true;
    }


}






