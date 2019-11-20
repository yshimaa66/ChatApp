package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Fragment.Group;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.GroupChat;
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

public class GroupMessagesAdapter extends RecyclerView.Adapter<GroupMessagesAdapter.ViewHolder>{



    public static final int message_left=0;
    public static final int message_right=1;



    private Context context;


    private List<GroupChat> groupchat;


    private String imageurl;

    FirebaseUser firebaseUser;




    public GroupMessagesAdapter(Context context, List<GroupChat> groupchat, String imageurl) {
        this.context = context;
        this.groupchat = groupchat;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public GroupMessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==message_right){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new GroupMessagesAdapter.ViewHolder(view);

        }else{

            View view = LayoutInflater.from(context).inflate(R.layout.group_item_left,parent,false);
            return new GroupMessagesAdapter.ViewHolder(view);

        }








    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        final GroupChat cchat= groupchat.get(position);

        holder.showmessage.setText(cchat.getMessage());


        final String[] usernamestr = new String[1];


        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    User user = snapshot.getValue(User.class);

                    assert user !=null;
                    assert firebaseUser !=null;

                    if(user.getId().equals(cchat.getSender())){


                        holder.username.setText(user.getUsername());


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
        return groupchat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showmessage;

        public TextView username;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showmessage=itemView.findViewById(R.id.showmessage);

            username=itemView.findViewById(R.id.username);



        }
    }


    @Override
    public int getItemViewType(int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if(groupchat.get(position).getSender().equals(firebaseUser.getUid())){

            return message_right;

        }else{

            return message_left;

        }
    }
}





