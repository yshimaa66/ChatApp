package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Messages;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {


    private Context context;
    private List<User> users;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_user_item,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {


        final User user=users.get(position);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Messages.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);


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
