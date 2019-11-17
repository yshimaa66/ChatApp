package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button registerbtn;
    protected Button loginbtn;

    public String usernamestr;

    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();

        mAuth = FirebaseAuth.getInstance();







    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No user is signed in
        } else {
            Intent intent = new Intent(MainActivity.this, Home.class);
            intent.putExtra("username",usernamestr);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerbtn) {


            Intent intent=new Intent(MainActivity.this,Register.class);
            startActivity(intent);



        } else if (view.getId() == R.id.loginbtn) {


            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivity(intent);

        }
    }

    private void initView() {
        registerbtn = (Button) findViewById(R.id.registerbtn);
        registerbtn.setOnClickListener(MainActivity.this);
        loginbtn = (Button) findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(MainActivity.this);
    }
}
