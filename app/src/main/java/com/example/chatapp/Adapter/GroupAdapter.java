package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Fragment.Group;
import com.example.chatapp.Messages;
import com.example.chatapp.MessagesGroup;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.GroupChat;
import com.example.chatapp.Model.GroupsModel;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Viewholder> {



    private Context context;
    private List<GroupsModel> groups;





    FirebaseUser firebaseUser;
    DatabaseReference reference;



    public GroupAdapter(Context context, List<GroupsModel> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_group_item, parent, false);
        return new Viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {


        final GroupsModel groupsModel = groups.get(position);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MessagesGroup.class);
                intent.putExtra("groupid",groupsModel.getGroupid());
                context.startActivity(intent);

               // Toast.makeText(context, groupsModel.getGroupid()+"", Toast.LENGTH_SHORT).show();


            }
        });


       if(groupsModel.getImageURL().equals("default")){
            holder.groupimage.setImageResource(R.drawable.ic_groupphoto);
        }else{

            Glide.with(context).load(groupsModel.getImageURL()).into(holder.groupimage);

        }


        holder.groupname.setText(groupsModel.getGroupname());







        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        reference = FirebaseDatabase.getInstance().getReference("GroupChats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    GroupChat cchat = snapshot.getValue(GroupChat.class);

                    if (!cchat.getSender().equals(firebaseUser.getUid())) {


/*

                        holder.senderreciever.setText(groupsModel);
                        holder.lastmessage.setText(": "+cchat.getMessage()+" ");
                        holder.lastmessagetime.setText(cchat.getTime());

                        holder.lastmessage.setVisibility(View.VISIBLE);

                        holder.lastmessagetime.setVisibility(View.VISIBLE);

                        holder.senderreciever.setVisibility(View.VISIBLE);*/




                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });















    }


    @Override
    public int getItemCount() {
        return groups.size();
    }




    public class Viewholder extends RecyclerView.ViewHolder {

        public CircleImageView groupimage;
        public TextView groupname;

        public ImageView havemessage;


        public Viewholder(@NonNull View itemView) {

            super(itemView);

            groupimage = (CircleImageView) itemView.findViewById(R.id.groupimage);
            groupname = (TextView) itemView.findViewById(R.id.groupname);

            havemessage=itemView.findViewById(R.id.havemessage);



        }
    }
}
