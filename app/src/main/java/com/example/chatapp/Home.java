package com.example.chatapp;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Adapter.ViewPagerAdapter;
import com.example.chatapp.Fragment.Chats;
import com.example.chatapp.Fragment.Group;
import com.example.chatapp.Fragment.Profile;
import com.example.chatapp.Fragment.Users;
import com.example.chatapp.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity implements SearchView.OnQueryTextListener{

    protected Toolbar toolbar;
    protected TextView username;
    protected CircleImageView profileimage;

    FirebaseUser firebaseUser;
    DatabaseReference reference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_home);


        profileimage=(CircleImageView)findViewById(R.id.profile_image);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        username = (TextView) findViewById(R.id.username);




        @SuppressLint("WrongViewCast") TabLayout tabLayout = findViewById(R.id.tabLayout);

        ViewPager viewPager=(ViewPager) findViewById(R.id.viewpager);

        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new Chats(),"Chats");
        viewPagerAdapter.addFragment(new Users(),"Users");
        viewPagerAdapter.addFragment(new Group(),"Groups");
        viewPagerAdapter.addFragment(new Profile(),"Profile");



        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);



        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat App");



        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user=dataSnapshot.getValue(User.class);

                username.setText(user.getUsername());


                if(user.getImageURL().equals("default")){
                    profileimage.setImageResource(R.drawable.ic_imgprofile);
                }else{

                    Glide.with(Home.this).load(user.getImageURL()).into(profileimage);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Intent intent = getIntent();
        String usernamestr = intent.getStringExtra("username");

        username.setText(usernamestr);



    }





    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.



        getMenuInflater().inflate(R.menu.home_menu, menu);




        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {


            Intent intent = new Intent(Home.this, MainActivity.class);

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Home.this,MainActivity.class));

            startActivity(intent);

            finish();


        }








        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
