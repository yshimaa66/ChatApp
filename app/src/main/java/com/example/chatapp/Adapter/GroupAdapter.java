package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Messages;
import com.example.chatapp.MessagesGroup;
import com.example.chatapp.Model.GroupsModel;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Viewholder> {



    private Context context;
    private List<GroupsModel> groups;

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
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {


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


/*        if(groupsModel.getImageURL().equals("default")){
            holder.groupimage.setImageResource(R.drawable.ic_imgprofile);
        }else{

            Glide.with(context).load(groupsModel.getImageURL()).into(holder.groupimage);

        }

*/
        holder.groupname.setText(groupsModel.getGroupname());


    }


    @Override
    public int getItemCount() {
        return groups.size();
    }




    public class Viewholder extends RecyclerView.ViewHolder {

        public CircleImageView groupimage;
        public TextView groupname;



        public Viewholder(@NonNull View itemView) {

            super(itemView);

            groupimage = (CircleImageView) itemView.findViewById(R.id.groupimage);
            groupname = (TextView) itemView.findViewById(R.id.groupname);

        }
    }
}
