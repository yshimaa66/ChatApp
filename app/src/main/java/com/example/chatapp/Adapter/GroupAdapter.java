package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Model.GroupsModel;
import com.example.chatapp.Model.User;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Viewholder> {



    private Context context;
    private List<GroupsModel> groups;

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.Viewholder holder, int position) {

    }



    @Override
    public int getItemCount() {
        return 0;
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        public Viewholder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
