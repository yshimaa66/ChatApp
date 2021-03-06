package com.example.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Fragment.Group;
import com.example.chatapp.Messages;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.GroupChat;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.google.android.gms.common.util.Base64Utils.decode;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {


    public static final int message_left=0;
    public static final int message_right=1;



    private Context context;
    private List<Chat> chat;

    private List<GroupChat> groupchat;


    private String imageurl;

    FirebaseUser firebaseUser;



    public MessagesAdapter(Context context, List<Chat> chat, String imageurl) {
        this.context = context;
        this.chat = chat;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==message_right){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessagesAdapter.ViewHolder(view);

        }else{

            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessagesAdapter.ViewHolder(view);

        }


    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Chat cchat=chat.get(position);


        if(cchat.getMessage().contains("https://firebasestorage.googleapis.com/v0/b/chatapp-9b682.appspot.com/o/uploads%")){

            Glide.with(context).load(cchat.getMessage()).into(holder.sentedphoto);
            holder.sentedphoto.setVisibility(View.VISIBLE);
            holder.showmessage.setVisibility(View.GONE);

        }else{

            holder.showmessage.setText(decrypt(cchat.getMessage()));
            holder.sentedphoto.setVisibility(View.GONE);
            holder.showmessage.setVisibility(View.VISIBLE);



        }


        holder.messagetime.setText(cchat.getTime());




        if(cchat.isIsseen()){

            holder.seen.setVisibility(View.VISIBLE);
            holder.unseen.setVisibility(View.GONE);

        }

        else{
            holder.unseen.setVisibility(View.VISIBLE);
            holder.seen.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return chat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showmessage;

        public TextView messagetime;

        public ImageView seen,unseen,sentedphoto;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showmessage=itemView.findViewById(R.id.showmessage);


            messagetime=itemView.findViewById(R.id.messagetime);


            seen=itemView.findViewById(R.id.seen);

            unseen=itemView.findViewById(R.id.unseen);


            sentedphoto=itemView.findViewById(R.id.sentedphoto);


        }
    }



    public static String decrypt(String value) {
        String key = "aesEncryptionKey";
        String initVector = "encryptionIntVec";
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(decode(value));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }







    @Override
    public int getItemViewType(int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if(chat.get(position).getSender().equals(firebaseUser.getUid())){

            return message_right;

        }else{

            return message_left;

        }
    }


}