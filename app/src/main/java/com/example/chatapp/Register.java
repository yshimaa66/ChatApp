package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener {

    protected EditText usernameEditText;
    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected EditText passwordconfirmEditText;
    protected Button registerbtn;
    protected FirebaseAuth auth;
    protected DatabaseReference reference;
    protected ProgressBar progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_register);

        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerbtn) {

            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String passwordconfirm = passwordconfirmEditText.getText().toString();

            if (username.trim().equals("")) {
                usernameEditText.setError("required");
            } else if (email.trim().equals("")) {
                emailEditText.setError("required");
            } else if (password.trim().equals("")) {
                passwordEditText.setError("required");
            } else if (passwordconfirm.trim().equals("")) {
                passwordconfirmEditText.setError("required");
            } else if (password.length() < 6) {
                passwordconfirmEditText.setError("It's so weak");
            } else if (!password.equals(passwordconfirm)) {
                passwordconfirmEditText.setError("It doesn't match password");
            } else {

                register(username, email, password);

            }

        }
    }

    private void initView() {
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordconfirmEditText = (EditText) findViewById(R.id.passwordconfirmEditText);
        registerbtn = (Button) findViewById(R.id.registerbtn);
        registerbtn.setOnClickListener(Register.this);
        auth = FirebaseAuth.getInstance();
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        progressbar.setVisibility(View.GONE);
    }


    private void register(final String username, final String email, String password) {

progressbar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            String userid = firebaseUser.getUid();

                            User user = new User(username, email,userid,"default");

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressbar.setVisibility(View.GONE);
                                        Toast.makeText(Register.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this, Home.class);
                                        startActivity(intent);
                                        finish();
                                    } else {

                                        progressbar.setVisibility(View.GONE);
                                        Toast.makeText(Register.this, "Invalid email or password", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                        } else {

                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });


    }


}
