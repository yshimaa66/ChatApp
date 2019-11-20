package com.example.chatapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Messages;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.User;
import com.example.chatapp.Model.UserGroupModel;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

public class UserGroupAdapter extends RecyclerView.Adapter<UserGroupAdapter.Viewholder> {


    private Context context;

    public UserGroupAdapter() {
    }

    private List<User> users;

    public static List<User>userstoaddtogroup;

    FirebaseUser firebaseUser;



    int c=0;

    public UserGroupAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserGroupAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_user_item,parent,false);

        return new UserGroupAdapter.Viewholder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {


        final User user=users.get(position);





        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();




        userstoaddtogroup=new ArrayList<>();



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



              holder.itemView.setBackground(Drawable.createFromPath("#ffff"));


              userstoaddtogroup.add(user);


              Toast.makeText(context, userstoaddtogroup.size()+"", Toast.LENGTH_SHORT).show();

            }
        });











        if(user.getImageURL().equals("default")){
            holder.profileimage.setImageResource(R.drawable.ic_imgprofile);
        }else{

            Glide.with(context).load(user.getImageURL()).into(holder.profileimage);

        }


        holder.username.setText(user.getUsername());


    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profileimage;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            username=(TextView)itemView.findViewById(R.id.username);

            profileimage=(ImageView)itemView.findViewById(R.id.profile_imageuseritem);


        }
    }


}
