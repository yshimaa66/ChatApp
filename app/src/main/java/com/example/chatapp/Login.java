package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button loginbtn;
    protected ProgressBar progressbar;
    protected FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginbtn) {


            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();


            if (email.trim().equals("")) {
                emailEditText.setError("required");
            } else if (email.trim().equals("")) {
                emailEditText.setError("required");
            } else if (password.trim().equals("")) {
                passwordEditText.setError("required");
            } else {


                login(email, password);



        }

        }
    }

    private void initView() {
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginbtn = (Button) findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(Login.this);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        auth = FirebaseAuth.getInstance();

        progressbar.setVisibility(View.GONE);
    }

    private void login(String email,String password) {

        progressbar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressbar.setVisibility(View.GONE);

                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Welcome Back :)", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            finish();

                        } else {

                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();


                        }

                        // ...
                    }
                });

    }
}
