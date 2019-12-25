package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Edit_Group;
import com.example.chatapp.Messages;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.google.android.gms.common.util.Base64Utils.decode;

public class GroupEditAdapter extends RecyclerView.Adapter<GroupEditAdapter.Viewholder>{
    private Context context;
    private List<User> users;


    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public GroupEditAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public GroupEditAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_group_edit_item,parent,false);
        return new GroupEditAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupEditAdapter.Viewholder holder, int position) {


        final User user=users.get(position);



        if(user.getImageURL().equals("default")){
            holder.profileimage.setImageResource(R.drawable.ic_imgprofile);
        }else{

            Glide.with(context).load(user.getImageURL()).into(holder.profileimage);

        }


        holder.username.setText(user.getUsername());



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        holder.removebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



               // holder.itemView.setBackground(Drawable.createFromPath("#581920"));


                Edit_Group.userstoremove.add(user);

                holder.undobtn.setVisibility(View.VISIBLE);

                holder.removebtn.setVisibility(View.GONE);

            }
        });



        holder.undobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



               // holder.itemView.setBackground(Drawable.createFromPath("#416479"));


                Edit_Group.userstoremove.remove(user);

                holder.undobtn.setVisibility(View.GONE);

                holder.removebtn.setVisibility(View.VISIBLE);

            }
        });







    }








    @Override
    public int getItemCount() {
        return users.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profileimage,removebtn,undobtn;



        public Viewholder(@NonNull View itemView) {
            super(itemView);

            username=(TextView)itemView.findViewById(R.id.username);

            profileimage=(ImageView)itemView.findViewById(R.id.profile_imageuseritem);


            removebtn=itemView.findViewById(R.id.removebtn);

            undobtn=itemView.findViewById(R.id.undobtn);

        }
    }
}
