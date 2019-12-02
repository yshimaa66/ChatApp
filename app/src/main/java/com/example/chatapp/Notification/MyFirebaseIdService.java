package com.example.chatapp.Notification;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();

        if(user != null){

            UpdateToken(tokenRefresh);
        }

        //Log.e("NEW_TOKEN",s);
    }



    private void UpdateToken(String tokenRefresh) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token = new Token(tokenRefresh);

        ref.child(user.getUid()).setValue(token);

    }


}
