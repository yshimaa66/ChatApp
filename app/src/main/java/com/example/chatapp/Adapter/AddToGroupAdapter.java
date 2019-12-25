package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.AddGroup;
import com.example.chatapp.AddToGroup;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AddToGroupAdapter extends RecyclerView.Adapter<AddToGroupAdapter.Viewholder> {

    private Context context;

    public AddToGroupAdapter() {
    }

    private List<User> users;

    //public static List<User>userstoaddtogroup;

    FirebaseUser firebaseUser;



    int c=0;

    public AddToGroupAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public AddToGroupAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.activity_user_item,parent,false);



        return new AddToGroupAdapter.Viewholder(view);


    }





    @Override
    public void onBindViewHolder(@NonNull final AddToGroupAdapter.Viewholder holder, final int position) {


        final User user=users.get(position);




        holder.checkBox.setVisibility(View.VISIBLE);


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        //userstoaddtogroup=new ArrayList<>();





        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.checkBox.isChecked()) {

                    //holder.itemView.setBackground(Drawable.createFromPath("#ffff"));

                    // holder.itemView.setEnabled(false);

                    AddToGroup.usersingroupp.add(user);

                    Toast.makeText(context, (AddToGroup.usersingroupp.size()) - 1 + "", Toast.LENGTH_SHORT).show();

                } else {


                    //holder.itemView.setBackground(Drawable.createFromPath("@color/colorPrimary"));

                    // holder.itemView.setEnabled(false);

                    AddToGroup.usersingroupp.remove(user);


                   Toast.makeText(context, (AddToGroup.usersingroupp.size())- 1 + "", Toast.LENGTH_SHORT).show();


                }


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

        public CheckBox checkBox;




        public Viewholder(@NonNull View itemView) {
            super(itemView);

            username=(TextView)itemView.findViewById(R.id.username);

            profileimage=(ImageView)itemView.findViewById(R.id.profile_imageuseritem);

            checkBox=itemView.findViewById(R.id.checkbox);



        }
    }


}
