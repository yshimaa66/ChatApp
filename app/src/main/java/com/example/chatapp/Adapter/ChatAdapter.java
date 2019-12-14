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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Viewholder> {


    private Context context;
    private List<User> users;


    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public ChatAdapter(Context context, List<User> users) {
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
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {


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



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        reference = FirebaseDatabase.getInstance().getReference("Chats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat cchat = snapshot.getValue(Chat.class);

                    assert cchat != null;

                    if((!cchat.getSender().equals( user.getId())  && ! cchat.getReceiver().equals(firebaseUser.getUid()) )

                            && (! cchat.getReceiver().equals(user.getId()) && !cchat.getSender().equals( firebaseUser.getUid()))

                    ){





                    }

                    else if (cchat.getReceiver().equals(firebaseUser.getUid()) && cchat.getSender().equals(user.getId())) {


                        holder.senderreciever.setText(user.getUsername());


                        if(cchat.getMessage().contains("https://firebasestorage.googleapis.com/v0/b/chatapp-9b682.appspot.com/o/uploads%")){

                            holder.lastmessage.setText("sent a photo ");
                        }else{

                            holder.lastmessage.setText(": "+cchat.getMessage()+" ");

                        }

                        holder.lastmessagetime.setText(cchat.getTime());

                        holder.lastmessage.setVisibility(View.VISIBLE);

                        holder.lastmessagetime.setVisibility(View.VISIBLE);

                        holder.senderreciever.setVisibility(View.VISIBLE);


                        if(!cchat.isIsseen() && cchat.getReceiver().equals(firebaseUser.getUid()) && ! cchat.getSender().equals(firebaseUser.getUid())){

                            holder.havemessage.setVisibility(View.VISIBLE);


                        }

                        if(cchat.isIsseen() || ! cchat.getReceiver().equals(firebaseUser.getUid())  && cchat.getSender().equals(firebaseUser.getUid()) ){

                            holder.havemessage.setVisibility(View.GONE);

                        }



                    }


                    else if (cchat.getSender().equals(firebaseUser.getUid()) && cchat.getReceiver().equals(user.getId())) {




                        holder.senderreciever.setText("You");
                        if(cchat.getMessage().contains("https://firebasestorage.googleapis.com/v0/b/chatapp-9b682.appspot.com/o/uploads%")){

                            holder.lastmessage.setText("sent a photo ");
                        }else{

                            holder.lastmessage.setText(": "+cchat.getMessage()+" ");

                        }
                        holder.lastmessagetime.setText(cchat.getTime());


                        holder.lastmessage.setVisibility(View.VISIBLE);

                        holder.lastmessagetime.setVisibility(View.VISIBLE);

                        holder.senderreciever.setVisibility(View.VISIBLE);





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
        return users.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profileimage,havemessage;

        public TextView lastmessage,lastmessagetime,senderreciever;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            username=(TextView)itemView.findViewById(R.id.username);

            profileimage=(ImageView)itemView.findViewById(R.id.profile_imageuseritem);

            lastmessage = itemView.findViewById(R.id.lastmessage);

            lastmessagetime=itemView.findViewById(R.id.lastmessagetime);

            senderreciever=itemView.findViewById(R.id.senderreciever);

            havemessage=itemView.findViewById(R.id.havemessage);


        }
    }
}
